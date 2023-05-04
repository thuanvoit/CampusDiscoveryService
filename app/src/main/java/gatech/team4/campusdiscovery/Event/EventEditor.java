package gatech.team4.campusdiscovery.Event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import gatech.team4.campusdiscovery.Database.DBHelper;
import gatech.team4.campusdiscovery.Models.Admin;
import gatech.team4.campusdiscovery.Models.CampusActivity;
import gatech.team4.campusdiscovery.Models.Location;
import gatech.team4.campusdiscovery.Models.Organizer;
import gatech.team4.campusdiscovery.Models.User;
import gatech.team4.campusdiscovery.R;

public class EventEditor extends AppCompatActivity {
    private Intent intent;
    private DBHelper db;
    private CampusActivity activity;
    private User user;
    private Button saveButton;
    private Button discardButton;

    private EditText subjectText;
    private EditText roomText;
    private EditText dateText;
    private EditText timeText;
    private EditText descriptionText;
    private CheckBox inviteOnly;
    private Spinner buildingSpinner;

    private ScrollView scrollView;

    private ArrayList<Location> buildings;

    private SharedPreferences sharedPreferences;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);
        sharedPreferences = getSharedPreferences("CampusDiscoveryPrefs",
                MODE_PRIVATE);

        db = new DBHelper(this);
        getUser();

        intent = getIntent();

        boolean fromEditing = intent.getBooleanExtra("editing", false);
        activity = (CampusActivity) intent.getSerializableExtra("activity");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (activity != null) {
                getSupportActionBar().setTitle(activity.getSubject());
            } else {
                getSupportActionBar().setTitle("Add New Event");
            }
        }

        scrollView = findViewById(R.id.scrollView);
        saveButton = findViewById(R.id.eventSaveButton);
        discardButton = findViewById(R.id.eventDiscardButton);
        subjectText = findViewById(R.id.subjectEditText);
        roomText = findViewById(R.id.roomEditText);
        dateText = findViewById(R.id.dateEditText);
        timeText = findViewById(R.id.timeEditText);
        descriptionText = findViewById(R.id.infoEditText);
        inviteOnly = findViewById(R.id.isInviteOnlyCheckBox);
        buildingSpinner = findViewById(R.id.buildingSpinner);


        buildings = getBuildings();
        String[] arr = new String[buildings.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = buildings.get(i).getBuilding();
        }

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                getBuildingsArray());

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buildingSpinner.setAdapter(spinnerAdapter);

        buildingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                hideKeyboard(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (activity != null) {
            subjectText.setText(activity.getSubject());
            String[] locationArr = activity.getLocation().split("__,__");
            buildingSpinner.setSelection(spinnerAdapter.getPosition(locationArr[0]));
            roomText.setText(locationArr[1]);
            dateText.setText(activity.getDate());
            timeText.setText(activity.getTime());
            descriptionText.setText(activity.getInfo());
            Log.i("INVITE ONLY", "" + activity.getInviteOnly());
            inviteOnly.setChecked(activity.getInviteOnly() != 0);
        }


        dateText.setOnClickListener(view -> {
            hideKeyboard(view);
            dateClick();
        });
        timeText.setOnClickListener(view -> {
            hideKeyboard(view);
            timeClick();
        });

        saveButton.setOnClickListener(view -> {
            hideKeyboard(view);
            saveClick();
        });
        discardButton.setOnClickListener(view -> {
            hideKeyboard(view);
            finish();
        });
    }

    private ArrayList<Location> getBuildings() {
        ArrayList<Location> buildings = new ArrayList<>();
        Resources res = getResources();
        String[] arr = res.getStringArray(R.array.building_list);
        for (int i = 0; i < arr.length; i++) {
            try {
                JSONObject jsonObject = new JSONObject(arr[i]);
                String buildingName = jsonObject.getString("name");
                double buildingLat = jsonObject.getDouble("lat");
                double buildingLon = jsonObject.getDouble("lon");
                Location thisBuilding = new Location(buildingName, buildingLat, buildingLon);
                buildings.add(thisBuilding);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return buildings;
    }

    private String[] getBuildingsArray() {
        Resources res = getResources();
        String[] arr = res.getStringArray(R.array.building_list);
        for (int i = 0; i < arr.length; i++) {
            try {
                JSONObject jsonObject = new JSONObject(arr[i]);
                String buildingName = jsonObject.getString("name");
                arr[i] = buildingName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return arr;
    }

    private void saveClick() {
        String subject;
        String info;
        String building;
        String room;
        String date;
        String time;
        String host = user.getEmail();
        int inviteOnlyChecked;
        if (subjectText == null || roomText.getText().toString().trim().equals("")) {
            Toast.makeText(EventEditor.this,
                    "You must enter a title for the event!",
                    Toast.LENGTH_LONG).show();
        } else if (roomText == null || roomText.getText().toString().trim().equals("")) {
            Toast.makeText(EventEditor.this,
                    "You must enter a location for the event!",
                    Toast.LENGTH_LONG).show();
        } else if (dateText == null || dateText.getText().toString().trim().equals("")) {
            Toast.makeText(EventEditor.this,
                    "You must enter a date for the event!",
                    Toast.LENGTH_LONG).show();
        } else if (timeText == null || timeText.getText().toString().trim().equals("")) {
            Toast.makeText(EventEditor.this,
                    "You must enter a time for the event!",
                    Toast.LENGTH_LONG).show();
        } else if (descriptionText == null
                || descriptionText.getText().toString().trim().equals("")) {
            Toast.makeText(EventEditor.this,
                    "You must enter a description for the event!",
                    Toast.LENGTH_LONG).show();
        } else {
            subject = subjectText.getText().toString().trim();
            info = descriptionText.getText().toString().trim();
            building = buildings.get(buildingSpinner.getSelectedItemPosition()).getBuilding();
            room = roomText.getText().toString().trim();
            date = dateText.getText().toString().trim();
            time = timeText.getText().toString().trim();
            inviteOnlyChecked = inviteOnly.isChecked() ? 1 : 0;

            String location = String.format("%s__,__%s", building, room);

            if (user instanceof Organizer) {
                String[] args = {subject, info, location, date, time, host};
                if (db.insertActivityData(
                        args, R.drawable.activity_default_image, inviteOnlyChecked)) {
                    finish();
                } else {
                    Toast.makeText(EventEditor.this,
                            "Error! Please choose a different event name!",
                            Toast.LENGTH_LONG).show();
                }
            } else if (user instanceof Admin) {
                String[] args = {subject, info, location, date, time, activity.getHost()};
                if (db.insertActivityData(
                        args, R.drawable.activity_default_image, inviteOnlyChecked)) {
                    finish();
                } else {
                    Toast.makeText(EventEditor.this,
                            "Error! Please choose a different event name!",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void dateClick() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (dateText.getText().toString() != null
                && !dateText.getText().toString().trim().equalsIgnoreCase("")) {

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate dateFromString = LocalDate.parse(dateText.getText().toString(),
                    dateFormatter);

            year = dateFromString.getYear();
            month = dateFromString.getMonthValue() - 1;
            day = dateFromString.getDayOfMonth();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (view, year1, month1, day1) -> dateText.setText(
                String.format("%02d/%02d/%02d",
                        month1 + 1, day1, year1)), year, month, day);
        datePickerDialog.show();
    }

    private void timeClick() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (!timeText.getText().toString().trim().equalsIgnoreCase("")) {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("hh:mm a",
                    Locale.ENGLISH);
            LocalTime time = LocalTime.parse(timeText.getText().toString(), inputFormatter);

            hour = time.getHour();
            minute = time.getMinute();
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour,
                                          int minute) {
                        String amPm = (hour < 12) ? "AM" : "PM";
                        timeText.setText(String.format("%02d:%02d %s", hour % 12, minute, amPm));
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    // back button on actionbar
    @Override
    public boolean onSupportNavigateUp() {
        if (activity != null) {
            String[] args = {activity.getSubject(), activity.getInfo(), activity.getLocation(),
                    activity.getDate(), activity.getTime(), activity.getHost()};

            db.insertActivityData(args, activity.getImage(), activity.getInviteOnly());
        }
        finish();
        return true;
    }

    //
    private void getUser() {
        String userEmail = sharedPreferences.getString("userEmail", "");
        int userRole = db.getUserRole(userEmail);
        String name = db.getUserFromEmail(userEmail).getName();
        if (userRole == 0) {
            user = new Admin(name, userEmail);
        } else if (userRole == 1) {
            user = new Organizer(name, userEmail);
        } else {
            user = new User(name, userEmail);
        }
    }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }

}