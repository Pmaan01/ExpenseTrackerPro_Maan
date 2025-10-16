package com.android_assignments.expensetrackerpro_maan.fragments;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android_assignments.expensetrackerpro_maan.R;
import com.android_assignments.expensetrackerpro_maan.adapters.FavoritesAdapter;
import com.android_assignments.expensetrackerpro_maan.models.Transaction;
import com.android_assignments.expensetrackerpro_maan.utils.JsonStorageHelper;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements FavoritesAdapter.FavListener {
    private RecyclerView recycler;
    private FavoritesAdapter adapter;
    private List<Transaction> favorites;
    private List<Transaction> all;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favorites, container, false);
        recycler = v.findViewById(R.id.recyclerFavorites);
        all = JsonStorageHelper.getTransactions(requireContext());
        favorites = new ArrayList<>();
        for (Transaction t : all) if (t.isFavorite) favorites.add(t);
        adapter = new FavoritesAdapter(requireContext(), favorites, this);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);
        return v;
    }

    @Override public void onRemoveRequested(Transaction t) {
        // show confirm dialog
        ConfirmRemoveDialogFragment dlg = ConfirmRemoveDialogFragment.newInstance(t.id);
        dlg.setListener(new ConfirmRemoveDialogFragment.Listener() {
            @Override public void onConfirmed(String id) {
                // find transaction in all and unset favorite
                for (Transaction tx : all) {
                    if (tx.id.equals(id)) {
                        tx.isFavorite = false;
                        break;
                    }
                }
                JsonStorageHelper.saveTransactions(requireContext());
                refreshList();
            }
        });
        dlg.show(getChildFragmentManager(), "confirm");
    }

    private void refreshList() {
        favorites.clear();
        for (Transaction t : all) if (t.isFavorite) favorites.add(t);
        adapter.notifyDataSetChanged();
    }
}
