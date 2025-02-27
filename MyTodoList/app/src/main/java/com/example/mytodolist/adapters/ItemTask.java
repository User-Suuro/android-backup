package com.example.mytodolist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytodolist.R;
import com.example.mytodolist.data.TaskModel;

import java.util.List;

public class ItemTask extends RecyclerView.Adapter<ItemTask.TaskViewHolder> {
    private List<TaskModel> tasks;
    private Context context;

    // Constructor
    public ItemTask(Context context, List<TaskModel> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    public void setTasks(List<TaskModel> tasks) {
        this.tasks = tasks;
    }

    // Inflate the item layout and create the holder
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_itemtask, parent, false);
        return new TaskViewHolder(view);
    }

    // Bind data to the views
    @Override
    public void onBindViewHolder(@NonNull ItemTask.TaskViewHolder holder, int position) {
        TaskModel task = tasks.get(position);
        holder.titleTextView.setText(task.getTitle());
        holder.descriptionTextView.setText(task.getContent());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    // ViewHolder class to hold item views
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.TaskTitle);
            descriptionTextView = itemView.findViewById(R.id.TaskContent);
        }
    }


}
