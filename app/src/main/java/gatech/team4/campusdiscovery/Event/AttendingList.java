package gatech.team4.campusdiscovery.Event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import gatech.team4.campusdiscovery.Database.DBHelper;
import gatech.team4.campusdiscovery.Models.CampusActivity;
import gatech.team4.campusdiscovery.Models.User;
import gatech.team4.campusdiscovery.R;
import gatech.team4.campusdiscovery.RecyclerView.RecyclerViewInterface;
import gatech.team4.campusdiscovery.RecyclerView.UserViewAdapter;

public class AttendingList extends AppCompatActivity implements RecyclerViewInterface {

    private RecyclerView attendeeRecyclerView;
    private ArrayList<User> attendees;
    private CampusActivity activity;
    private String[] attendeesArray;
    private DBHelper db;
    private Intent intent;
    private int attendingType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attending_list);

        db = new DBHelper(this);
        intent = getIntent();
        attendees = new ArrayList<>();
        attendingType = intent.getIntExtra("attendingType", 0);
        activity = (CampusActivity) intent.getSerializableExtra("activity");
        Log.i("DEBUG", activity.getSubject());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (activity != null) {
                getSupportActionBar().setTitle(activity.getSubject() + "'s Attendees");
            } else {
                getSupportActionBar().setTitle("List");
            }
        }

        if (attendingType == 0) {
            attendees = db.getAttendingUser(activity.getSubject());
        } else {
            attendees = db.getPotentiallyAttendingUser(activity.getSubject());
        }

        Log.i("DEBUG", "" + attendees);
        attendeeRecyclerView = findViewById(R.id.attendeeRecyclerView);
        attendeeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendeeRecyclerView.setAdapter(new UserViewAdapter(attendees, this));

    }

    @Override
    public void onItemClick(int position) {
        Log.i("DEBUG", "CLICK" + position);
        Log.i("DEBUG", attendees.get(position).getEmail());
        Log.i("DEBUG", activity.getSubject());

        if (attendingType == 0) {
            db.removeAttending(activity.getSubject(), attendees.get(position).getEmail());
            attendees = db.getAttendingUser(activity.getSubject());
        } else {
            attendees = db.getPotentiallyAttendingUser(activity.getSubject());
            db.removePotentiallyAttending(
                    activity.getSubject(), attendees.get(position).getEmail());
        }

        attendeeRecyclerView.setAdapter(new UserViewAdapter(attendees, this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}