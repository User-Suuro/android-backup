package com.example.todolist_redesign.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterHelper<T> extends RecyclerView.Adapter<AdapterHelper.ViewHolder> {
    private List<T> items;
    private int layoutResId;
    private Bindable<T> binder;
    private Context context;
    private OnItemClickListener<T> onItemClickListener;

    // Callback interface for binding data
    public interface Bindable<T> {
        void bind(View itemView, T item, int position);
    }

    // Callback interface for item clicks
    public interface OnItemClickListener<T> {
        void onItemClick(View itemView, T item, int position);
    }

    // Constructor
    public AdapterHelper(Context context, List<T> items, int layoutResId, Bindable<T> binder) {
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

    // ViewHolder class to hold item views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    // Setters
    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.onItemClickListener = listener;
    }

    // Methods
    public void reloadItems(List<T> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
}
