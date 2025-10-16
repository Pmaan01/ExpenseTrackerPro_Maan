package com.android_assignments.expensetrackerpro_maan.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android_assignments.expensetrackerpro_maan.R;
import com.android_assignments.expensetrackerpro_maan.activities.TransactionDetailsActivity;
import com.android_assignments.expensetrackerpro_maan.models.Transaction;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.VH> {
    private final Context ctx;
    private final List<Transaction> list;

    public TransactionsAdapter(Context ctx, List<Transaction> list) { this.ctx = ctx; this.list = list; }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH holder, int position) {
        Transaction t = list.get(position);
        holder.title.setText(t.title);
        holder.category.setText(t.category);
        holder.amount.setText(String.format("$%.2f", t.amount));
        if (t.receiptPath != null) {
            holder.thumb.setVisibility(View.VISIBLE);
            try { holder.thumb.setImageURI(Uri.parse(t.receiptPath)); } catch (Exception e) { holder.thumb.setVisibility(View.GONE); }
        } else holder.thumb.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(ctx, TransactionDetailsActivity.class);
            i.putExtra("transaction", t);
            ctx.startActivity(i);
        });
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, category, amount;
        ImageView thumb;
        VH(@NonNull View v) { super(v); title = v.findViewById(R.id.tvTitle); category = v.findViewById(R.id.tvCategory); amount = v.findViewById(R.id.tvAmount); thumb = v.findViewById(R.id.ivThumb); }
    }
}
