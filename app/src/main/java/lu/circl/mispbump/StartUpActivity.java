package lu.circl.mispbump;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.restful_client.User;

public class StartUpActivity extends AppCompatActivity {

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

        // closes the activity thus prevents going back to this (empty) activity
        finish();
    }

    private boolean isUserLoggedIn() {
        PreferenceManager preferenceManager = PreferenceManager.getInstance(this);
        User user = preferenceManager.getUserInfo();
        return user != null;
    }
}
