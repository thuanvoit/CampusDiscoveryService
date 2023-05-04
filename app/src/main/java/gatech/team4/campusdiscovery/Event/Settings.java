package gatech.team4.campusdiscovery.Event;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import gatech.team4.campusdiscovery.Database.DBHelper;
import gatech.team4.campusdiscovery.Models.Admin;
import gatech.team4.campusdiscovery.Models.Organizer;
import gatech.team4.campusdiscovery.Models.User;
import gatech.team4.campusdiscovery.R;


public class Settings extends AppCompatActivity {
    private Button updateButton;
    private Button logoutButton;

    private EditText userNameText;
    private EditText userEmailText;

    private Intent intent;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;
    private DBHelper db;
    private User user;
    private int userRole;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Setting");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = getSharedPreferences("CampusDiscoveryPrefs",
                MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();

        db = new DBHelper(this);

        getUser();

        intent = getIntent();


        userNameText = findViewById(R.id.settingName);
        userNameText.setText(user.getName());

        userEmailText = findViewById(R.id.settingEmail);
        userEmailText.setText(user.getEmail());

        updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(view -> {
            updateUser();
        });
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> {
            endSession();
        });
    }

    private boolean checkNameAndEmail(String name, String email) {
        if (name == null || name.equals("")) {
            Toast.makeText(Settings.this, "You must put a name!",
                    Toast.LENGTH_LONG).show();
            return false;
        } else if (email == null || email.equals("")
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(Settings.this, "Please check your email address!",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void updateUser() {
        String newName = userNameText.getText().toString().trim();
        String newEmail = userEmailText.getText().toString().trim();
        if (checkNameAndEmail(newName, newEmail)) {
            if (!user.getEmail().equals(newEmail) || !user.getName().equals(newName)) {

                db.updateUserInfo(user.getEmail(), newEmail, newName);
                if (user instanceof Organizer) {
                    db.updateActivitiesHost(user.getEmail(), newEmail);
                }
                prefsEditor.putString("userEmail", newEmail);
                prefsEditor.commit();
            }
            finish();
        }
    }

    private void getUser() {
        String userEmail = sharedPreferences.getString("userEmail", "");
        Log.i("UserEmail", userEmail);
        userRole = db.getUserRole(userEmail);
        String name = db.getUserFromEmail(userEmail).getName();
        if (userRole == 0) {
            user = new Admin(name, userEmail);
        } else if (userRole == 1) {
            user = new Organizer(name, userEmail);
        } else {
            user = new User(name, userEmail);
        }
    }

    private void endSession() {
        prefsEditor.putString("userEmail", null);
        prefsEditor.commit();
        Intent logout = new Intent(Settings.this, LoginScreen.class);
        startActivity(logout);
        finish();
    }

    // back button on actionbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}