package com.example.jon_curtis_eventtracker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {

    private Context context;
    Activity activity;
    private ArrayList event_id, event_title, event_description, event_date, event_time;

    EventAdapter(Activity activity, Context context,
                  ArrayList event_id,
                  ArrayList event_title,
                  ArrayList event_description,
                  ArrayList event_date,
                  ArrayList event_time){
        this.activity = activity;
        this.context = context;
        this.event_id = event_id;
        this.event_title = event_title;
        this.event_description = event_description;
        this.event_date = event_date;
        this.event_time = event_time;

    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.event_table, parent, false);
        return new EventHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, final int position) {

        holder.event_id.setText(String.valueOf(event_id.get(position)));
        holder.event_title.setText(String.valueOf(event_title.get(position)));
        holder.event_description.setText(String.valueOf(event_description.get(position)));
        holder.event_date.setText(String.valueOf(event_date.get(position)));
        holder.event_time.setText(String.valueOf(event_time.get(position)));

        holder.mainLayout.setOnClickListener((view) -> {

        });
        holder.itemView.findViewById(R.id.edit_button).setOnClickListener((view -> {
            Intent intent = new Intent(context, EditActivity.class);
            intent.putExtra("id", String.valueOf(event_id.get(position)));
            intent.putExtra("title", String.valueOf(event_title.get(position)));
            intent.putExtra("description", String.valueOf(event_description.get(position)));
            intent.putExtra("date", String.valueOf(event_date.get(position)));
            intent.putExtra("time", String.valueOf(event_time.get(position)));
            activity.startActivityForResult(intent, 1);
        }));

    }

    @Override
    public int getItemCount() {
        return event_id.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {

        TextView event_id, event_title, event_description, event_date, event_time;
        LinearLayout mainLayout;

        public EventHolder(@NonNull View itemView) {
            super(itemView);
            event_id = itemView.findViewById(R.id.event_id);
            event_title = itemView.findViewById(R.id.event_title);
            event_description = itemView.findViewById(R.id.event_description);
            event_date = itemView.findViewById(R.id.date_to_fire);
            event_time = itemView.findViewById(R.id.time_to_fire);
            mainLayout = itemView.findViewById((R.id.mainLayout));

        }
    }
}