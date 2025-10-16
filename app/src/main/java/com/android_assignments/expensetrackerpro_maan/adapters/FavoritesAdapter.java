package com.android_assignments.expensetrackerpro_maan.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android_assignments.expensetrackerpro_maan.R;
import com.android_assignments.expensetrackerpro_maan.activities.TransactionDetailsActivity;
import com.android_assignments.expensetrackerpro_maan.models.Transaction;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.VH> {
    public interface FavListener { void onRemoveRequested(Transaction t); }
    private final List< Transaction> list;
    private final Context ctx;
    private final FavListener listener;

    public FavoritesAdapter(Context ctx, List<Transaction> list, FavListener listener) { this.ctx = ctx; this.list = list; this.listener = listener; }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH holder, int position) {
        Transaction t = list.get(position);
        holder.title.setText(t.title);
        holder.category.setText(t.category);
        holder.amount.setText(String.format("$%.2f", t.amount));
        if (t.receiptPath != null) {
            holder.thumb.setVisibility(View.VISIBLE);
            holder.thumb.setImageURI(android.net.Uri.parse(t.receiptPath));
        } else holder.thumb.setVisibility(View.GONE);

        holder.remove.setOnClickListener(v -> { if (listener != null) listener.onRemoveRequested(t); });

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(ctx, TransactionDetailsActivity.class);
            i.putExtra("transaction", t);
            ctx.startActivity(i);
        });
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, category, amount;
        ImageView thumb, remove;
        VH(@NonNull View v) { super(v); title = v.findViewById(R.id.tvFavTitle); category = v.findViewById(R.id.tvFavCategory); amount = v.findViewById(R.id.tvFavAmount); thumb = v.findViewById(R.id.ivFavThumb); remove = v.findViewById(R.id.ivRemoveFav); }
    }
}
