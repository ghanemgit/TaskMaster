package com.example.taskmaster.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.R;
import com.example.taskmaster.data.Task;

import java.util.List;

public class CustomListRecyclerViewAdapter extends RecyclerView.Adapter<CustomListRecyclerViewAdapter.CustomViewHolder> {


    List<Task> dataList;
    CustomClickListener listener;

    public CustomListRecyclerViewAdapter(List<Task> dataList, CustomClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItemView = layoutInflater.inflate(R.layout.task_item_layout, parent, false);

        return new CustomViewHolder(listItemView, listener);

    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.title.setText(dataList.get(position).getTitle());
        holder.body.setText(dataList.get(position).getBody());
        holder.state.setText(dataList.get(position).getTaskState().getDisplayValue());


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView body;
        TextView state;


        public CustomViewHolder(@NonNull View itemView, CustomClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            state = itemView.findViewById(R.id.state);

            itemView.setOnClickListener(view -> listener.onTaskClicked(getAdapterPosition()));

        }
    }

    public interface CustomClickListener {
        void onTaskClicked(int position);
    }
}
