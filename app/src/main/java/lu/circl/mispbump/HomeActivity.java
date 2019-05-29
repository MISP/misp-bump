package lu.circl.mispbump;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.restful_client.Organisation;
import lu.circl.mispbump.restful_client.User;
import lu.circl.mispbump.security.KeyStoreWrapper;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // populate Toolbar (Actionbar)
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
        }

        populateViewsWithInfo();

        FloatingActionButton sync_fab = findViewById(R.id.home_fab);
        sync_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SyncActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_clear_and_logout:
                clearDeviceAndLogOut();
                return true;

            default:
                // invoke superclass to handle unrecognized item (eg. homeAsUp)
                return super.onOptionsItemSelected(item);

        }
    }


    private void populateViewsWithInfo() {
        PreferenceManager preferenceManager = PreferenceManager.getInstance(this);

        User user = preferenceManager.getUserInfo();
        Organisation org = preferenceManager.getUserOrganisation();

        TextView orgTitle = findViewById(R.id.home_org_name);
        TextView orgDesc = findViewById(R.id.home_org_desc);

        orgTitle.setText(org.name);
        orgDesc.setText(org.description);
    }

    private void clearDeviceAndLogOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Clear all saved data and logout");
        builder.setMessage("Do you really want to delete all data and logout?");
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Delete & Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceManager prefs = PreferenceManager.getInstance(getApplicationContext());
                prefs.clearAllData();
                KeyStoreWrapper.deleteAllStoredKeys();

                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                finish();
            }
        });

        builder.create().show();
    }

}
