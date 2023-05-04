package gatech.team4.campusdiscovery.Event;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import gatech.team4.campusdiscovery.R;


public class WelcomeScreen extends AppCompatActivity implements View.OnClickListener {
    private Button exploreButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        exploreButton = findViewById(R.id.exploreButton);
        exploreButton.setOnClickListener(this);

        // fix Map SDK malfunction by clearing cache
        getCacheDir().delete();

    }

    @Override
    public void onClick(View v) {
        if (v == exploreButton) {
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
            finish();
        }
    }
}