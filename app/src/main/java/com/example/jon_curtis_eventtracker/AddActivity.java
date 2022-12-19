package com.example.jon_curtis_eventtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.net.ParseException;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private final int SMS_PERMISSION_CODE = 1;

    EditText event_name, event_details;
    Button add_button, set_date, set_time, perm_button;
    String alarm_generator;

    long notificationCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        event_name = findViewById(R.id.event_name);
        event_details = findViewById(R.id.event_details);
        set_date = findViewById(R.id.set_date);
        set_time = findViewById(R.id.set_time);
        add_button = findViewById(R.id.add_button);

        event_name.addTextChangedListener(textWatcher);
        event_details.addTextChangedListener(textWatcher);
        set_date.addTextChangedListener(textWatcher);
        set_time.addTextChangedListener(textWatcher);

        perm_button = findViewById(R.id.perm_button);

        set_date.setOnClickListener(view -> selectDate());
        set_time.setOnClickListener(view -> selectTime());

        add_button.setOnClickListener(view -> {

            requestSmsPermission();

            Event_DBHelper eventDB = new Event_DBHelper(AddActivity.this);

            notificationCounter = eventDB.addReminder(event_name.getText().toString().trim(),
                    event_details.getText().toString().trim(),
                    set_date.getText().toString().trim(),
                    set_time.getText().toString().trim());

            if (ContextCompat.checkSelfPermission(AddActivity.this,
                    Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
                try {
                    setAlarm(event_name.getText().toString().trim(),
                            event_details.getText().toString().trim(),
                            set_date.getText().toString().trim(),
                            set_time.getText().toString().trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else{
                Toast.makeText(AddActivity.this, "You must grant permission to use this feature", Toast.LENGTH_SHORT).show();
            }

            finish();

        });

        perm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddActivity.this,
                        Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AddActivity.this, "You have already granted this permission", Toast.LENGTH_SHORT).show();

                } else {
                    requestSmsPermission();
                }

            }
        });

    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String title = event_name.getText().toString().trim();
            String description = event_details.getText().toString().trim();
            String date = set_date.getText().toString().trim();
            String time = set_time.getText().toString().trim();

            add_button.setEnabled(!title.isEmpty() && !description.isEmpty() &&
                    !date.isEmpty() && !time.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    // Setting date and time methods
    private void selectTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, i, i1) -> {
            alarm_generator = i + ":" + i1;
            set_time.setText(formatTime(i, i1));
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        @SuppressLint("DefaultLocale") DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (datePicker, year1, month1, day1) ->
                set_date.setText(String.format(
                        "%d-%d-%d", day1, month1 + 1, year1)),
                year, month, day);
        datePickerDialog.show();
    }

    public String formatTime(int hour, int minute) {

        String time;
        String minutes_reformatted;

        if (minute / 10 == 0) {
            minutes_reformatted = "0" + minute;
        } else {
            minutes_reformatted = "" + minute;
        }

        // format input time to be readable
        if (hour == 0) {
            time = "12" + ":" + minutes_reformatted + " AM";
        } else if (hour < 12) {
            time = hour + ":" + minutes_reformatted + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + minutes_reformatted + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + minutes_reformatted + " PM";
        }
        return time;
    }

    private void setAlarm(String text, String description, String date, String time) throws ParseException {

        Intent intent = new Intent(AddActivity.this, Alarm.class);
        intent.putExtra("event", text);
        intent.putExtra("description", description);
        intent.putExtra("time", date);
        intent.putExtra("date", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                AddActivity.this,
                (int)notificationCounter,               // use the ID field from db
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);     // use this flag to update the event
        // else event gets overwritten
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        String dateAndTime = date + " " + alarm_generator;
        SimpleDateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");

        try{
            Date dateToSet = (Date) formatter.parse(dateAndTime);
            assert dateToSet != null;       // check that date is not null

            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    dateToSet.getTime(),
                    pendingIntent);
            Toast.makeText(getApplicationContext(), "Alarm set!", Toast.LENGTH_SHORT).show();

        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }

    }

    private void requestSmsPermission(){
        new AlertDialog.Builder(this)
                .setTitle("Read SMS Messages")
                .setMessage("This permission is needed in order to set an SMS reminder for your events.")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                AddActivity.this,
                                new String[] {Manifest.permission.READ_SMS},
                                SMS_PERMISSION_CODE);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}