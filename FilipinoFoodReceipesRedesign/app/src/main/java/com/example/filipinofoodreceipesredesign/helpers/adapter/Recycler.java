package com.example.filipinofoodreceipesredesign.helpers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Recycler<T> extends RecyclerView.Adapter<Recycler.ViewHolder> {
    private List<T> items;
    private int layoutResId;
    private Bindable<T> binder;
    private Context context;
    private OnItemClickListener<T> onItemClickListener;

    public interface Bindable<T> {
        void bind(View itemView, T item, int position);
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View itemView, T item, int position);
    }

    // Constructor
    public Recycler(Context context, List<T> items, int layoutResId, Bindable<T> binder) {
        this.context = context;
        this.items = items;
        this.layoutResId = layoutResId;
        this.binder = binder;
    }

    // Inflate the item layout and create the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    // Bind data to the views
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T item = items.get(position);
        binder.bind(holder.itemView, item, position);

        // Set click listener on the itemView
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.onItemClickListener = listener;
    }

    public void reloadItems(List<T> newItems) {
        DiffUtil.Callback diffCallback = new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return items != null ? items.size() : 0;
            }

            @Override
            public int getNewListSize() {
                return newItems != null ? newItems.size() : 0;
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                // Customize this logic if you have a unique identifier
                return items.get(oldItemPosition).equals(newItems.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                // If your items have more complex state, compare them appropriately
                return items.get(oldItemPosition).equals(newItems.get(newItemPosition));
            }
        };

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.items = newItems;
        diffResult.dispatchUpdatesTo(this);
    }
}
