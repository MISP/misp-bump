package lu.circl.mispbump.activities;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import lu.circl.mispbump.auxiliary.PreferenceManager;


/**
 * Starts either the login or home activity.
 */
public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isUserLoggedIn()) {
            Intent home = new Intent(this, HomeActivity.class);
            startActivity(home);
        } else {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        }

        // closes the activity to prevent going back to this (empty) activity
        finish();
    }

    private boolean isUserLoggedIn() {
        PreferenceManager preferenceManager = PreferenceManager.getInstance(this);
        return preferenceManager.getUserCredentials() != null && preferenceManager.getUserInfo() != null;
    }
}
