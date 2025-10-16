package com.android_assignments.expensetrackerpro_maan.fragments;

import android.app.Dialog;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ConfirmRemoveDialogFragment extends DialogFragment {
    public interface Listener { void onConfirmed(String id); }
    private Listener listener;
    private static final String ARG_ID = "id";

    public static ConfirmRemoveDialogFragment newInstance(String id) {
        ConfirmRemoveDialogFragment f = new ConfirmRemoveDialogFragment();
        Bundle b = new Bundle();
        b.putString(ARG_ID, id);
        f.setArguments(b);
        return f;
    }

    public void setListener(Listener l) { listener = l; }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        String id = getArguments().getString(ARG_ID);
        AlertDialog.Builder b = new AlertDialog.Builder(requireContext());
        b.setTitle("Remove Favorite")
                .setMessage("Are you sure you want to remove this from favorites?")
                .setPositiveButton("Yes", (d, w) -> { if (listener != null) listener.onConfirmed(id); })
                .setNegativeButton("Cancel", (d, w) -> d.dismiss());
        return b.create();
    }
}
