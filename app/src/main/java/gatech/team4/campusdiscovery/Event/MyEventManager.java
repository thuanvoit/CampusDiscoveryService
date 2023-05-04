package gatech.team4.campusdiscovery.Event;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import gatech.team4.campusdiscovery.Database.DBHelper;
import gatech.team4.campusdiscovery.Models.Admin;
import gatech.team4.campusdiscovery.Models.CampusActivity;
import gatech.team4.campusdiscovery.Models.Organizer;
import gatech.team4.campusdiscovery.Models.User;
import gatech.team4.campusdiscovery.R;
import gatech.team4.campusdiscovery.RecyclerView.RecyclerViewIndicatorAdapter;
import gatech.team4.campusdiscovery.RecyclerView.RecyclerViewInterface;

public class MyEventManager extends AppCompatActivity implements RecyclerViewInterface {

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
        Intent settingIntent = new Intent(MyEventManager.this, Settings.class);
        startActivity(settingIntent);
    }

    private void onFilterClick() {
        Intent filterIntent = new Intent(MyEventManager.this, EventFilter.class);
        startActivity(filterIntent);
    }

    private void onRefreshClick() {
        if (user instanceof Organizer) {
            campusActivityList = db.getActivityFromEmail(user.getEmail());
            Log.i("USER DATA", "Get from organizer");
        } else {
            campusActivityList = db.getUserRSVPedActivityData(user.getEmail());
            Log.i("USER DATA", "Get from User");
        }
        campusActivityRecyclerView.setAdapter(
                new RecyclerViewIndicatorAdapter(
                        campusActivityList, this, campusActivityList));
        refreshFilterPrefs();
    }

    private void refreshFilterPrefs() {
        prefsEditor.putString("filterOrganizer", "");
        prefsEditor.putString("filterLocation", "");
        prefsEditor.putString("filterDate", "");
        prefsEditor.commit();
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
            getSupportActionBar().setTitle("My Events");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = new DBHelper(this);

        getUser();

        if (user instanceof Organizer) {
            campusActivityList = db.getActivityFromEmail(user.getEmail());
            Log.i("USER DATA", "Get from organizer");
        } else {
            campusActivityList = db.getUserRSVPedActivityData(user.getEmail());
            Log.i("USER DATA", "Get from User");
        }

        for (CampusActivity event : campusActivityList) {
            Log.i("USER DATA", "" + event.getSubject());

        }

        campusActivityRecyclerView = findViewById(R.id.campusActivityRecyclerView);
        campusActivityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        campusActivityRecyclerView.setAdapter(
                new RecyclerViewIndicatorAdapter(
                        campusActivityList, this, campusActivityList));

        addEventButton = findViewById(R.id.addEventButton);
        mapButton = findViewById(R.id.mapButton);
        myEventsButton = findViewById(R.id.myEventsButton);

        if (!(user instanceof Organizer) || user instanceof Admin) {
            addEventButton.setVisibility(View.GONE);
        }

        //addEventButton.setVisibility(View.GONE);
        myEventsButton.setVisibility(View.GONE);

        mapButton.setOnClickListener(view -> {
            onMapClick();
        });

        addEventButton.setOnClickListener(view -> {
            if (user instanceof Organizer) {
                Intent addIntent = new Intent(MyEventManager.this, EventEditor.class);
                startActivity(addIntent);
            }
        });
    }

    private void onMapClick() {
        Intent mapIntent = new Intent(MyEventManager.this, MapsActivity.class);
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
        List<CampusActivity> totalActivity;

        if (user instanceof Organizer) {
            campusActivityList = db.getFilteredActivityData(
                    filterOrganizer, filterLocation, filterDate);
            totalActivity = db.getActivityFromEmail(user.getEmail());
        } else {
            campusActivityList = db.getUserFilteredActivityData(
                    user.getEmail(), filterOrganizer, filterLocation, filterDate);
            totalActivity = db.getUserRSVPedActivityData(user.getEmail());
        }
        campusActivityRecyclerView.setAdapter(
                new RecyclerViewIndicatorAdapter(
                        campusActivityList, this, totalActivity));
    }

    @Override
    public void onItemClick(int position) {
        Intent activityIntent = new Intent(MyEventManager.this, EventViewer.class);
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

    // back button on actionbar
    @Override
    public boolean onSupportNavigateUp() {
        Log.i("REFRESH", "START");
        refreshFilterPrefs();
        finish();
        return true;
    }
}