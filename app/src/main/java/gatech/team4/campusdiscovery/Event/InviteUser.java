package gatech.team4.campusdiscovery.Event;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import gatech.team4.campusdiscovery.Database.DBHelper;
import gatech.team4.campusdiscovery.Models.CampusActivity;
import gatech.team4.campusdiscovery.Models.User;
import gatech.team4.campusdiscovery.R;

public class InviteUser extends AppCompatActivity {
    private Intent intent;
    private CampusActivity activity;
    private Button inviteButton;
    private EditText emailTextName;
    private DBHelper db;
    private ArrayList<User> potentiallyRSVPedUsers;
    private ArrayList<User> rsvpedUsers;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inviteuser_screen);

        sharedPreferences = getSharedPreferences("CampusDiscoveryPrefs",
                MODE_PRIVATE);
        db = new DBHelper(this);
        intent = getIntent();
        activity = (CampusActivity) intent.getSerializableExtra("activity");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Invite User");
        }

        inviteButton = findViewById(R.id.inviteButton);
        emailTextName = findViewById(R.id.emailTextName);


        potentiallyRSVPedUsers = db.getPotentiallyAttendingUser(activity.getSubject());
        rsvpedUsers = db.getAttendingUser(activity.getSubject());

        inviteButton.setOnClickListener(view -> {
            String email = emailTextName.getText().toString().trim();
            if (emailTextName == null || email.isEmpty()) {
                Toast.makeText(InviteUser.this, "Please enter an email!",
                        Toast.LENGTH_LONG).show();
            } else if (db.getUserFromEmail(email) == null) {
                Toast.makeText(InviteUser.this, "No user with that email exists!",
                        Toast.LENGTH_LONG).show();
            } else {
                boolean notInvited = true;
                for (User u : potentiallyRSVPedUsers) {
                    if (u.getEmail().equals(email)) {
                        notInvited = false;
                        Toast.makeText(InviteUser.this, "User has already been invited!",
                                Toast.LENGTH_LONG).show();
                    }
                }
                for (User u : rsvpedUsers) {
                    if (u.getEmail().equals(email)) {
                        notInvited = false;
                        Toast.makeText(InviteUser.this, "User has already signed up!",
                                Toast.LENGTH_LONG).show();
                    }
                }

                if (notInvited) {
                    db.addPotentiallyAttendingUser(activity.getSubject(), email);
                    Toast.makeText(InviteUser.this, "User invited!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // back button on actionbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
