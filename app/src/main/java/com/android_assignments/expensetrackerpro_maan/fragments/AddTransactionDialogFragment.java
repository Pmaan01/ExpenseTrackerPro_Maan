package com.android_assignments.expensetrackerpro_maan.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android_assignments.expensetrackerpro_maan.R;
import com.android_assignments.expensetrackerpro_maan.models.Transaction;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AddTransactionDialogFragment extends DialogFragment {
    public interface Listener { void onSaved(Transaction t); }
    private Listener listener;

    public void setListener(Listener l) { listener = l; }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(requireContext());
        View v = requireActivity().getLayoutInflater().inflate(R.layout.dialog_add_transaction, null);

        EditText etTitle = v.findViewById(R.id.etTitle);
        Spinner spCategory = v.findViewById(R.id.spCategory);
        EditText etAmount = v.findViewById(R.id.etAmount);
        EditText etDesc = v.findViewById(R.id.etDescription);

        String[] cats = getResources().getStringArray(R.array.categories_array);
        ArrayAdapter<String> aa = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, cats);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(aa);

        b.setView(v)
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = etTitle.getText().toString();
                    String cat = spCategory.getSelectedItem().toString();
                    String amtS = etAmount.getText().toString();
                    String desc = etDesc.getText().toString();
                    if (TextUtils.isEmpty(title) || TextUtils.isEmpty(amtS)) {
                        Toast.makeText(requireContext(), "Title and amount required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double amt = 0;
                    try { amt = Double.parseDouble(amtS); } catch (Exception ignored) {}
                    Transaction t = new Transaction();
                    t.title = title;
                    t.category = cat;
                    t.amount = amt;
                    t.date = OffsetDateTime.now().toString();
                    t.description = desc;
                    t.isFavorite = false;
                    if (listener != null) listener.onSaved(t);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        return b.create();
    }
}
