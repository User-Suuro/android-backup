package com.example.mytodolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mytodolist.adapters.ItemTask;
import com.example.mytodolist.data.PrefsHelper;
import com.example.mytodolist.data.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    PrefsHelper<TaskModel> TasksPrefs;
    FloatingActionButton btnOpen;
    EditText inputTitle, inputContent;
    Button addTaskBtn;
    Dialog dialog;
    RecyclerView recyclerTasks;
    ItemTask tasksAdapter;

    List<TaskModel> tasks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TasksPrefs = new PrefsHelper<>(MainActivity.this, TaskModel.class);

        btnOpen = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        recyclerTasks = (RecyclerView) findViewById(R.id.RecyclerTasks);

        btnOpen.setOnClickListener(view -> openModal(view));
        tasks = TasksPrefs.getAllData();

        recyclerTasks.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ItemTask(this, tasks);
        recyclerTasks.setAdapter(tasksAdapter);
    }

    public void openModal(View view) {

        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.modal_addtask);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        inputTitle = (EditText) dialog.findViewById(R.id.InputTitle);
        inputContent = (EditText) dialog.findViewById(R.id.InputContent);
        addTaskBtn = (Button) dialog.findViewById(R.id.AddTaskBtn);

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = inputTitle.getText().toString();
                String content = inputContent.getText().toString();

                // VALIDATORS

                if (title.isEmpty()) {
                    inputTitle.setError("Please fill the title");
                    return;
                }

                if (content.isEmpty()) {
                    inputContent.setError("Please fill the content");
                    return;
                }

                TaskModel task = new TaskModel.Builder(title, content).build();

                if (TasksPrefs.saveData(task)) {
                    Toast.makeText(MainActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                    tasks = TasksPrefs.getAllData(); // update list
                    tasksAdapter.setTasks(tasks); // Make sure your adapter exposes a method to update its data
                    tasksAdapter.notifyItemInserted(tasks.size() - 1);
                    dialog.hide();
                };

            }
        });

        dialog.show();
    }
}