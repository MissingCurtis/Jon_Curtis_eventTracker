package com.example.jon_curtis_eventtracker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.metrics.Event;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class EventActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1;

    RecyclerView recyclerView;
    FloatingActionButton add_event;
    Button settings;

    EventAdapter eventAdaptor;

    Event_DBHelper eventDB;
    ArrayList<String> event_id, event_title, event_description, event_date, event_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        recyclerView = findViewById(R.id.recyclerView);
        add_event = findViewById(R.id.add_event);

        add_event.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // The launcher with the Intent you want to start
                Intent intent = new Intent(EventActivity.this, AddActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        eventDB = new Event_DBHelper(EventActivity.this);
        event_id = new ArrayList<>();
        event_title = new ArrayList<>();
        event_description = new ArrayList<>();
        event_date = new ArrayList<>();
        event_time = new ArrayList<>();

        storeDataInArrays();

        // use the customAdaptor class to build each row
        eventAdaptor = new EventAdapter(EventActivity.this, this, event_id, event_title,
                event_description, event_date, event_time);
        recyclerView.setAdapter(eventAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(EventActivity.this));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    void storeDataInArrays(){
        Cursor cursor = eventDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();

        }
        else{
            while (cursor.moveToNext()){
                event_id.add(cursor.getString(0));
                event_title.add(cursor.getString(1));
                event_description.add(cursor.getString(2));
                event_date.add(cursor.getString(3));
                event_time.add(cursor.getString(4));


            }
        }
    }
    void delete(){

    }
}