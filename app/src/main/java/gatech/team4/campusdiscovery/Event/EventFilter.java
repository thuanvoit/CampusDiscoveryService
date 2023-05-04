package gatech.team4.campusdiscovery.Event;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import gatech.team4.campusdiscovery.Models.Location;
import gatech.team4.campusdiscovery.R;

public class EventFilter extends AppCompatActivity {
    private EditText organizerText;
    private Spinner buildingSpinner;
    private EditText dateText;
    private Button filterButton;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;
    private final Calendar calendar = Calendar.getInstance();

    private ArrayList<Location> buildings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        sharedPreferences = getSharedPreferences("CampusDiscoveryPrefs",
                MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Event Filter");
        }

        organizerText = findViewById(R.id.hostFilter);
        buildingSpinner = findViewById(R.id.buildingSpinner);
        dateText = findViewById(R.id.dateFilter);
        dateText.setOnClickListener(view -> {
            dateClick();
        });

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

        filterButton = findViewById(R.id.filterButton);
        filterButton.setOnClickListener(view -> {
            filterClick();
        });
    }

    private ArrayList<Location> getBuildings() {
        ArrayList<Location> buildings = new ArrayList<>();
        buildings.add(new Location("", "", 0, 0));
        Resources res = getResources();
        String[] arr = res.getStringArray(R.array.building_list);
        for (String building : arr) {
            try {
                JSONObject jsonObject = new JSONObject(building);
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
        String[] returnArr = new String[arr.length + 1];
        for (int i = 0; i < arr.length; i++) {
            try {
                JSONObject jsonObject = new JSONObject(arr[i]);
                String buildingName = jsonObject.getString("name");
                returnArr[i + 1] = buildingName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        returnArr[0] = "<EMPTY>";
        return returnArr;
    }

    private void dateClick() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (!dateText.getText().toString().trim().equalsIgnoreCase("")) {
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

    private void filterClick() {
        String organizer = organizerText.getText().toString().trim();
        String location = buildings.get(buildingSpinner.getSelectedItemPosition()).getBuilding();
        String date = dateText.getText().toString().trim();
        prefsEditor.putString("filterOrganizer", organizer);
        prefsEditor.putString("filterLocation", location);
        prefsEditor.putString("filterDate", date);
        prefsEditor.commit();
        finish();
    }

    // back button on actionbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
    }
}
