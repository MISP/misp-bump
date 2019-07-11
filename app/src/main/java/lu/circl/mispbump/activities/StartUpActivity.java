package lu.circl.mispbump.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.models.restModels.User;

/**
 * This activity navigates to the next activity base on the user status.
 * This is the first activity that gets loaded when the user starts the app.
 */
public class StartUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (isUserLoggedIn()) {
//            Intent home = new Intent(this, HomeActivity.class);
//            startActivity(home);
//        } else {
//            Intent login = new Intent(this, LoginActivity.class);
//            startActivity(login);
//        }

        Intent i = new Intent(this, ExchangeActivity2.class);
        startActivity(i);

        // closes the activity to prevent going back to this (empty) activity
        finish();
    }

    private boolean isUserLoggedIn() {
        PreferenceManager preferenceManager = PreferenceManager.getInstance(this);
        User user = preferenceManager.getUserInfo();
        return user != null;
    }
}
