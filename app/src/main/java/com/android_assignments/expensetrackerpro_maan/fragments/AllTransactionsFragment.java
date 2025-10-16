package com.android_assignments.expensetrackerpro_maan.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android_assignments.expensetrackerpro_maan.R;
import com.android_assignments.expensetrackerpro_maan.adapters.TransactionsAdapter;
import com.android_assignments.expensetrackerpro_maan.models.Transaction;
import com.android_assignments.expensetrackerpro_maan.utils.JsonStorageHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class AllTransactionsFragment extends Fragment {
    private RecyclerView recycler;
    private TransactionsAdapter adapter;
    private List<Transaction> all;
    private List<Transaction> filtered;
    private Spinner spinner;
    private static final String[] CATEGORIES = {
            "All Categories","Food & Dining","Transportation","Shopping","Utilities",
            "Entertainment","Health & Fitness","Travel","Education","Insurance",
            "Rent & Mortgage","Personal Care","Gifts & Donations","Savings & Investments","Miscellaneous"
    };

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_transactions, container, false);
        recycler = v.findViewById(R.id.recyclerTransactions);
        spinner = v.findViewById(R.id.spinnerCategories);
        FloatingActionButton fab = v.findViewById(R.id.fabAdd);

        all = JsonStorageHelper.getTransactions(requireContext());
        filtered = new ArrayList<>(all);

        adapter = new TransactionsAdapter(requireContext(), filtered);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        ArrayAdapter<String> a = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, CATEGORIES);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(a);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sel = CATEGORIES[position];
                applyFilter(sel);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        fab.setOnClickListener(view -> {
            AddTransactionDialogFragment dlg = new AddTransactionDialogFragment();
            dlg.setListener(new AddTransactionDialogFragment.Listener() {
                @Override public void onSaved(Transaction t) {
                    all.add(0, t);
                    JsonStorageHelper.saveTransactions(requireContext());
                    applyFilter(spinner.getSelectedItem().toString());
                }
            });
            dlg.show(getChildFragmentManager(), "add");
        });
        return v;
    }

    private void applyFilter(String category) {
        filtered.clear();
        if (category == null || category.equals("All Categories")) {
            filtered.addAll(all);
        } else {
            for (Transaction t : all) if (category.equals(t.category)) filtered.add(t);
        }
        adapter.notifyDataSetChanged();
    }
}
