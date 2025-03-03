package com.example.mytodolist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytodolist.helpers.AdapterHelper;
import com.example.mytodolist.helpers.PrefsHelper;
import com.example.mytodolist.data.TaskModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    PrefsHelper<TaskModel> TasksPrefs;
    FloatingActionButton btnOpen;

    RecyclerView recyclerTasks;
    AdapterHelper<TaskModel> tasksAdapter;
    List<TaskModel> tasks;
    EditText searchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TasksPrefs = new PrefsHelper<>(MainActivity.this, TaskModel.class);

        btnOpen = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        btnOpen.setOnClickListener(view -> openCRDModal(null));

        searchBar = (EditText) findViewById(R.id.SearchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Optional: Do something before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Perform search as the text changes
                List<TaskModel> allTasks = TasksPrefs.getAllData(); // Assuming this returns a List<Task>

                List<TaskModel> filteredTasks = new ArrayList<>();

                // Convert query to lower case to make the search case-insensitive
                String lowerQuery = s.toString().toLowerCase();

                // Loop through all tasks and filter those that match the query
                for (TaskModel task : allTasks) {
                    // Here we check if the task name contains the query string.
                    if (task.getTitle().toLowerCase().contains(lowerQuery)) {
                        filteredTasks.add(task);
                    }
                }

                tasksAdapter.reloadItems(filteredTasks);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Optional: Do something after text changes
            }
        });

        recyclerTasks = (RecyclerView) findViewById(R.id.RecyclerTasks);
        tasks = TasksPrefs.getAllData();
        tasksAdapter = new AdapterHelper<>(
                this,
                tasks,
                R.layout.adapter_itemtask,
                ((itemView, task, position) -> {
                    TextView titleTextView = itemView.findViewById(R.id.TaskTitle);
                    TextView contentTextView = itemView.findViewById(R.id.TaskContent);
                    titleTextView.setText(task.getTitle());
                    contentTextView.setText(task.getContent());

                    itemView.setOnLongClickListener(v -> {
                        showPopupMenu(v, task, position);
                        return true; // Return true to consume the event
                    });

                })
        );

        recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerTasks.setAdapter(tasksAdapter);


    }

    public Dialog initDialog(Activity activity, int layout) {

        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(layout);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        return dialog;
    }

    public void openCRDModal(@Nullable TaskModel taskToEdit) {

        Dialog dialog = initDialog(MainActivity.this, R.layout.modal_addtask);

        EditText inputTitle = dialog.findViewById(R.id.InputTitle);
        EditText inputContent = dialog.findViewById(R.id.InputContent);
        Button addTaskBtn = dialog.findViewById(R.id.AddTaskBtn);
        TextView taskTitle = dialog.findViewById(R.id.TaskTitle);

        // If taskToEdit is provided, pre-populate fields for edit mode
        boolean isEditMode = taskToEdit != null;

        if (isEditMode) {
            inputTitle.setText(taskToEdit.getTitle());
            inputContent.setText(taskToEdit.getContent());
            addTaskBtn.setText("Update Task");

            taskTitle.setText("Update " + taskToEdit.getTitle());
        } else {
            addTaskBtn.setText("Add Task");
        }

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = inputTitle.getText().toString().trim();
                String content = inputContent.getText().toString().trim();

                // VALIDATORS
                if (title.isEmpty()) {
                    inputTitle.setError("Please fill the title");
                    return;
                }
                if (content.isEmpty()) {
                    inputContent.setError("Please fill the content");
                    return;
                }

                // Create a new task instance using builder
                TaskModel newTask = new TaskModel.Builder(title, content).build();

                // In edit mode, set the existing task id (if your TaskModel supports it)
                if (isEditMode) {
                    if (TasksPrefs.update(taskToEdit.getId(), newTask)) { // update logic for edit mode
                        Toast.makeText(MainActivity.this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (TasksPrefs.saveData(newTask)) { // add new task
                        Toast.makeText(MainActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                    }
                }

                // Update list and adapter
                tasks = TasksPrefs.getAllData(); // update list
                tasksAdapter.reloadItems(tasks);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void openAlertModal(@Nullable TaskModel taskToDelete) {
        Dialog dialog = initDialog(MainActivity.this, R.layout.modal_confirmation);

        TextView textContent = dialog.findViewById(R.id.ConfirmationContent);
        MaterialButton btnAccept = dialog.findViewById(R.id.btnAccept);
        MaterialButton btnClose = dialog.findViewById(R.id.btnClose);

        textContent.setText("Are you sure you want to delete " + taskToDelete.getTitle());

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TasksPrefs.remove(taskToDelete.getId());  // update logic for edit mode
                Toast.makeText(MainActivity.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                tasks = TasksPrefs.getAllData(); // update list
                tasksAdapter.reloadItems(tasks);
                dialog.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });



        dialog.show();
    }


    private void showPopupMenu(View view, TaskModel task, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.task_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {

                if(item.getItemId() == R.id.menu_edit) {
                    openCRDModal(task);
                    return true;
                }

                else if (item.getItemId() == R.id.menu_delete) {
                    openAlertModal(task);
                    return true;
                }

                else {
                     return false;
                }
        });

        popupMenu.show();
    }
}