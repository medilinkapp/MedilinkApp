package com.jat.medilinkapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jat.medilinkapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolderTask> {

    ArrayList<String> tasks;

    public TaskAdapter(ArrayList<String> tasks) {
        this.tasks = tasks;
    }

    public void setList(ArrayList<String> tasks){
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderTask onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.task_item_layout, parent, false);
        return new ViewHolderTask(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTask holder, int position) {
        String task = tasks.get(position);

        TextView tvTaskCode = holder.tvTaskCode;
        tvTaskCode.setText(task);

        ImageButton btDelete = holder.btDelete;
        btDelete.setOnClickListener((v -> {
            tasks.remove(task);
            notifyDataSetChanged();
        }));
    }

    @Override
    public int getItemCount() {
        if (tasks != null) {
            return tasks.size();
        }
        return 0;

    }

    class ViewHolderTask extends RecyclerView.ViewHolder {
        private TextView tvTaskCode;
        private ImageButton btDelete;

        public ViewHolderTask(@NonNull View v) {
            super(v);
            tvTaskCode = (TextView) v.findViewById(R.id.tv_task_code);
            btDelete = (ImageButton) v.findViewById(R.id.bt_delete_task);
        }
    }
}
