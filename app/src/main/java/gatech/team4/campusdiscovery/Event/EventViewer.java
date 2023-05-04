package gatech.team4.campusdiscovery.Event;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import gatech.team4.campusdiscovery.Database.DBHelper;
import gatech.team4.campusdiscovery.Models.Admin;
import gatech.team4.campusdiscovery.Models.CampusActivity;
import gatech.team4.campusdiscovery.Models.Organizer;
import gatech.team4.campusdiscovery.Models.User;
import gatech.team4.campusdiscovery.R;

public class EventViewer extends AppCompatActivity {

    private Intent intent;
    private CampusActivity activity;
    private User user;
    private Button rsvpButton;
    private Button potentiallyRsvpButton;
    private Button cancelRsvpButton;
    private Button attendingListButton;
    private Button potentialListButton;
    private Button inviteButton;
    private DBHelper db;
    private SharedPreferences sharedPreferences;

    private ImageView eventImageView;
    private TextView subjectTextView;
    private TextView willgoTextView;
    private TextView maygoTextView;
    private TextView locationTextView;
    private TextView hostTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_viewer);
        sharedPreferences = getSharedPreferences("CampusDiscoveryPrefs",
                MODE_PRIVATE);

        db = new DBHelper(this);
        getUser();

        intent = getIntent();
        activity = (CampusActivity) intent.getSerializableExtra("activity");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (activity != null) {
                getSupportActionBar().setTitle(activity.getSubject());
            } else {
                getSupportActionBar().setTitle("Discovery Campus");
            }
        }

        eventImageView = findViewById(R.id.eventImageView);
        subjectTextView = findViewById(R.id.subjectTextView);
        willgoTextView = findViewById(R.id.willgoTextView);
        maygoTextView = findViewById(R.id.maygoTextView);
        locationTextView = findViewById(R.id.locationTextView);
        hostTextView = findViewById(R.id.hostTextView);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

        //editButton = findViewById(R.id.eventEditButton);
        rsvpButton = findViewById(R.id.rsvpButton);
        potentiallyRsvpButton = findViewById(R.id.potentialrsvpButton);
        cancelRsvpButton = findViewById(R.id.cancelrsvpButton);
        attendingListButton = findViewById(R.id.attendingListButton);
        potentialListButton = findViewById(R.id.potentialListButton);
        inviteButton = findViewById(R.id.inviteButton);

        //editButton.setVisibility(View.GONE);
        rsvpButton.setVisibility(View.GONE);
        potentiallyRsvpButton.setVisibility(View.GONE);
        cancelRsvpButton.setVisibility(View.GONE);
        attendingListButton.setVisibility(View.GONE);
        potentialListButton.setVisibility(View.GONE);
        inviteButton.setVisibility(View.GONE);

        subjectTextView.setText(activity.getSubject());

        String[] location = activity.getLocation().split("__,__");

        locationTextView.setText(String.format("Location: %s\n\nRoom: %s",
                location[0], location[1]));
        hostTextView.setText("Host: " + db.getUserFromEmail(activity.getHost()).getName());
        dateTextView.setText("Date: " + activity.getDate());
        timeTextView.setText("Time: " + activity.getTime());
        descriptionTextView.setText(activity.getInfo());
        willgoTextView.setText("Will Go: "
                + db.getNumberOfAttendingUser(activity.getSubject()));
        maygoTextView.setText("May Go: "
                + db.getNumberOfPotentiallyAttendingUser(activity.getSubject()));

        setVisibility();

        attendingListButton.setOnClickListener(view -> {
            attendingListButtonClicked();
        });

        potentialListButton.setOnClickListener(view -> {
            potentialListButtonClicked();
        });

        rsvpButton.setOnClickListener(view -> {
            rsvpButtonClick();
        });

        potentiallyRsvpButton.setOnClickListener(view -> {
            potentiallyRsvpButtonClick();
        });

        cancelRsvpButton.setOnClickListener(view -> {
            cancelRsvpButtonClick();
        });

        inviteButton.setOnClickListener(view -> {
            inviteButtonClick();
        });

    }

    private void inviteButtonClick() {
        Intent attendingList = new Intent(EventViewer.this, InviteUser.class);
        attendingList.putExtra("activity", activity);
        startActivity(attendingList);
    }

    private void potentiallyRsvpButtonClick() {
        if (db.addPotentiallyAttendingUser(activity.getSubject(), user.getEmail())) {
            Toast.makeText(EventViewer.this,
                    "Hope to see you there!",
                    Toast.LENGTH_LONG).show();
            potentiallyRsvpButton.setVisibility(View.GONE);
        } else {
            Toast.makeText(EventViewer.this,
                    "Error! Already potentially Rsvped!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void cancelRsvpButtonClick() {
        if (db.removeAttending(activity.getSubject(), user.getEmail())) {
            Toast.makeText(EventViewer.this,
                    "Sorry to see you go!",
                    Toast.LENGTH_LONG).show();
            db.removePotentiallyAttending(activity.getSubject(), user.getEmail());
            if (activity.getInviteOnly() == 0) {
                rsvpButton.setVisibility(View.VISIBLE);
                potentiallyRsvpButton.setVisibility(View.VISIBLE);
                cancelRsvpButton.setVisibility(View.GONE);
            } else {
                rsvpButton.setVisibility(View.GONE);
                potentiallyRsvpButton.setVisibility(View.GONE);
                cancelRsvpButton.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(EventViewer.this,
                    "Error! Something wrong!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void rsvpButtonClick() {
        if (db.addAttendingUser(activity.getSubject(), user.getEmail())) {
            Toast.makeText(EventViewer.this,
                    "Rsvped!",
                    Toast.LENGTH_LONG).show();
            rsvpButton.setVisibility(View.GONE);
            potentiallyRsvpButton.setVisibility(View.GONE);
            cancelRsvpButton.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(EventViewer.this,
                    "Error! Already Rsvped!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setVisibility() {
        if (user instanceof Admin) {
            attendingListButton.setVisibility(View.VISIBLE);
            potentialListButton.setVisibility(View.VISIBLE);
        } else if (user instanceof Organizer
                && user.getEmail().equalsIgnoreCase(activity.getHost())) {
            attendingListButton.setVisibility(View.VISIBLE);
            potentialListButton.setVisibility(View.VISIBLE);
            inviteButton.setVisibility(View.VISIBLE);
        } else {
            // if Not invite only
            Log.i("INVITE ONYL", "" + activity.getInviteOnly());
            if (activity.getInviteOnly() == 0) {
                // check potentially
                if (db.getPotentiallyAttending(activity.getSubject()).contains(user.getEmail())) {
                    rsvpButton.setVisibility(View.VISIBLE);
                    cancelRsvpButton.setVisibility(View.GONE);
                    potentiallyRsvpButton.setVisibility(View.GONE);
                }
                // check real attending
                if (db.getAttending(activity.getSubject()).contains(user.getEmail())) {
                    cancelRsvpButton.setVisibility(View.VISIBLE);
                    potentiallyRsvpButton.setVisibility(View.GONE);
                    rsvpButton.setVisibility(View.GONE);
                }
                // check potentially but not attending
                if (!db.getPotentiallyAttending(activity.getSubject()).contains(user.getEmail())
                        && !db.getAttending(activity.getSubject()).contains(user.getEmail())) {
                    rsvpButton.setVisibility(View.VISIBLE);
                    potentiallyRsvpButton.setVisibility(View.VISIBLE);
                }
            } else {
                if (db.getPotentiallyAttending(activity.getSubject()).contains(user.getEmail())) {
                    rsvpButton.setVisibility(View.VISIBLE);
                    cancelRsvpButton.setVisibility(View.GONE);
                    potentiallyRsvpButton.setVisibility(View.GONE);
                }
                // check real attending
                if (db.getAttending(activity.getSubject()).contains(user.getEmail())) {
                    cancelRsvpButton.setVisibility(View.VISIBLE);
                    potentiallyRsvpButton.setVisibility(View.GONE);
                    rsvpButton.setVisibility(View.GONE);
                }
                // check potentially but not attending
                if (!db.getPotentiallyAttending(activity.getSubject()).contains(user.getEmail())
                        && !db.getAttending(activity.getSubject()).contains(user.getEmail())) {
                    rsvpButton.setVisibility(View.INVISIBLE);
                    potentiallyRsvpButton.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    //Edit button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (user instanceof Admin
                || (user instanceof Organizer
                && user.getEmail().equalsIgnoreCase(activity.getHost()))) {
            getMenuInflater().inflate(R.menu.edit_menu_bar, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    //Edit button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.editButton) {
            editClicked();
        }
        return super.onOptionsItemSelected(item);
    }

    // back button on actionbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void attendingListButtonClicked() {
        if (user instanceof Organizer && user.getEmail().equalsIgnoreCase(activity.getHost())) {
            Intent attendingList = new Intent(EventViewer.this, AttendingList.class);
            attendingList.putExtra("activity", activity);
            attendingList.putExtra("attendingType", 0);
            startActivity(attendingList);
            Log.i("DEBUG", "clicked");

        }
    }

    private void potentialListButtonClicked() {
        if (user instanceof Organizer && user.getEmail().equalsIgnoreCase(activity.getHost())) {
            Intent attendingList = new Intent(EventViewer.this, AttendingList.class);
            attendingList.putExtra("activity", activity);
            attendingList.putExtra("attendingType", 1);
            startActivity(attendingList);
            Log.i("DEBUG", "clicked");
        }
    }

    private void editClicked() {
        if (user instanceof Admin || (user instanceof Organizer
                && user.getEmail().equalsIgnoreCase(activity.getHost()))) {
            Intent editIntent = new Intent(EventViewer.this, EventEditor.class);
            editIntent.putExtra("activity", activity);
            editIntent.putExtra("editing", true);
            if (db.deleteActivity(activity.getSubject())) {
                startActivity(editIntent);
            } else {
                Toast.makeText(EventViewer.this, "SQL deletion error!",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(EventViewer.this, "You don't have permission to edit.",
                    Toast.LENGTH_LONG).show();
        }
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