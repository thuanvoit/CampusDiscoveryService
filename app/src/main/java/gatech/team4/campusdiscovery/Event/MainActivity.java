package gatech.team4.campusdiscovery.Event;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import gatech.team4.campusdiscovery.Database.DBHelper;
import gatech.team4.campusdiscovery.Models.Admin;
import gatech.team4.campusdiscovery.Models.CampusActivity;
import gatech.team4.campusdiscovery.Models.Organizer;
import gatech.team4.campusdiscovery.Models.User;
import gatech.team4.campusdiscovery.R;
import gatech.team4.campusdiscovery.RecyclerView.RecyclerViewAdapter;
import gatech.team4.campusdiscovery.RecyclerView.RecyclerViewInterface;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {
    private Intent intent;
    private User user;
    private ArrayList<CampusActivity> campusActivityList;
    private RecyclerView campusActivityRecyclerView;
    private FloatingActionButton addEventButton;
    private FloatingActionButton mapButton;
    private FloatingActionButton myEventsButton;
    private DBHelper db;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;

    //Taskbar buttons
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Taskbar buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settingButton) {
            onSettingClick();
        } else if (id == R.id.filterButton) {
            onFilterClick();
        } else if (id == R.id.refreshButton) {
            onRefreshClick();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSettingClick() {
        Intent settingIntent = new Intent(MainActivity.this, Settings.class);
        startActivity(settingIntent);
    }

    private void onFilterClick() {
        Intent filterIntent = new Intent(MainActivity.this, EventFilter.class);
        startActivity(filterIntent);
    }

    private void refreshFilterPrefs() {
        prefsEditor.putString("filterOrganizer", "");
        prefsEditor.putString("filterLocation", "");
        prefsEditor.putString("filterDate", "");
        prefsEditor.commit();
        getSupportActionBar().setTitle("All Events");
    }

    private void onRefreshClick() {
        campusActivityList = db.getActivityData();
        campusActivityRecyclerView.setAdapter(
                new RecyclerViewAdapter(campusActivityList, this));
        refreshFilterPrefs();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("CampusDiscoveryPrefs",
                MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();
        intent = getIntent();
        sharedPreferences = getSharedPreferences("CampusDiscoveryPrefs",
                MODE_PRIVATE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("All Events");
        }

        db = new DBHelper(this);
        getUser();

        campusActivityList = db.getActivityData();
        campusActivityRecyclerView = findViewById(R.id.campusActivityRecyclerView);
        campusActivityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        campusActivityRecyclerView.setAdapter(
                new RecyclerViewAdapter(campusActivityList, this));

        addEventButton = findViewById(R.id.addEventButton);
        mapButton = findViewById(R.id.mapButton);
        myEventsButton = findViewById(R.id.myEventsButton);

        if (!(user instanceof Organizer) || user instanceof Admin) {
            addEventButton.setVisibility(View.GONE);
        }

        addEventButton.setOnClickListener(view -> {
            if (user instanceof Organizer) {
                Intent addIntent = new Intent(MainActivity.this, EventEditor.class);
                startActivity(addIntent);
            }
        });

        mapButton.setOnClickListener(view -> {
            onMapClick();
        });

        myEventsButton.setOnClickListener(view -> {
            Intent myEventIntent = new Intent(MainActivity.this, MyEventManager.class);
            startActivity(myEventIntent);
        });
    }

    private void onMapClick() {
        Intent mapIntent = new Intent(MainActivity.this, MapsActivity.class);
        mapIntent.putExtra("activities", campusActivityList);
        startActivity(mapIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        getUser();

        String filterOrganizer = sharedPreferences.getString("filterOrganizer", "");
        String filterLocation = sharedPreferences.getString("filterLocation", "");
        String filterDate = sharedPreferences.getString("filterDate", "");
        if (!filterOrganizer.isEmpty() || !filterLocation.isEmpty() || !filterDate.isEmpty()) {
            getSupportActionBar().setTitle("Filtered Events");
        } else {
            getSupportActionBar().setTitle("All Events");
        }
        campusActivityList = db.getFilteredActivityData(
                filterOrganizer, filterLocation, filterDate);

        campusActivityRecyclerView.setAdapter(
                new RecyclerViewAdapter(campusActivityList, this));
    }

    @Override
    public void onItemClick(int position) {
        Intent activityIntent = new Intent(MainActivity.this, EventViewer.class);
        activityIntent.putExtra("activity", campusActivityList.get(position));
        startActivity(activityIntent);
    }

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


}