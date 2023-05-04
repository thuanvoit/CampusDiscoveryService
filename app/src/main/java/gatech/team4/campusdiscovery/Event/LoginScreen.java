package gatech.team4.campusdiscovery.Event;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import gatech.team4.campusdiscovery.Database.DBHelper;
import gatech.team4.campusdiscovery.Models.User;
import gatech.team4.campusdiscovery.R;

public class LoginScreen extends AppCompatActivity {
    private EditText editTextName;
    private EditText editEmail;
    private RadioGroup personCategoriesGroup;
    private RadioButton userRadioButton;
    private RadioButton organizerRadioButton;
    private RadioButton adminRadioButton;
    private Button loginButton;
    private Intent intent;
    private DBHelper db;
    private User user;
    private int role;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;
    private boolean debug = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Login");
        }

        sharedPreferences = getSharedPreferences("CampusDiscoveryPrefs",
                MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();

        db = new DBHelper(this);

        editTextName = findViewById(R.id.editTextName);
        editEmail = findViewById(R.id.emailTextName);

        personCategoriesGroup = findViewById(R.id.personCategoriesGroup);
        userRadioButton = findViewById(R.id.userRadioButton);
        organizerRadioButton = findViewById(R.id.organizerRadioButton);
        adminRadioButton = findViewById(R.id.adminRadioButton);


        if (debug) {
            editTextName.setText("Team4Organizer");
            editEmail.setText("team4@mail.org");
            organizerRadioButton.setChecked(true);
        }


        loginButton = findViewById(R.id.inviteButton);

        loginButton.setOnClickListener(view -> {
            loginClick();
        });
    }

    private void loginClick() {

        String name = editTextName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        if (editTextName == null || name.isEmpty()) {
            Toast.makeText(LoginScreen.this, "You must put a name!",
                    Toast.LENGTH_LONG).show();
        } else if (personCategoriesGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(LoginScreen.this, "Please select a category!",
                    Toast.LENGTH_LONG).show();
        } else if (editEmail == null || email.isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(LoginScreen.this, "Please check your email!",
                    Toast.LENGTH_LONG).show();
        } else {
            intent = new Intent(LoginScreen.this, MainActivity.class);
            // retrieve User by email
            User checkUser = db.getUserFromEmail(email);
            if (checkUser == null) { // user doesn't exist
                int radioId = personCategoriesGroup.getCheckedRadioButtonId();
                if (radioId == R.id.organizerRadioButton) {
                    role = 1;
                } else if (radioId == R.id.adminRadioButton) {
                    role = 0;
                } else {
                    role = 2;
                }
                db.insertUserData(email, name, role);
                //
                Toast.makeText(LoginScreen.this, "New User Account Created!",
                        Toast.LENGTH_LONG).show();
            } else { // user exist, retrieve data
                role = db.getUserRole(email);
                //
                Toast.makeText(LoginScreen.this, "Welcome back!",
                        Toast.LENGTH_LONG).show();
            }

            // store data privately

            prefsEditor.putString("userEmail", email);
            prefsEditor.putString("filterOrganizer", "");
            prefsEditor.putString("filterLocation", "");
            prefsEditor.putString("filterDate", "");
            prefsEditor.commit();

            startActivity(intent);
            finish();
        }
    }
}