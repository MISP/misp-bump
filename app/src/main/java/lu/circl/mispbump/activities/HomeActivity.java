package lu.circl.mispbump.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.adapters.SyncAdapter;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.models.UploadInformation;
import lu.circl.mispbump.restful_client.MispRestClient;
import lu.circl.mispbump.restful_client.Organisation;
import lu.circl.mispbump.restful_client.User;
import lu.circl.mispbump.security.KeyStoreWrapper;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "Home";

    private CoordinatorLayout layout;
    private TextView title;

    private RecyclerView recyclerView;

    private PreferenceManager preferenceManager;
    private MispRestClient mispRestClient;

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

        View titleView = getLayoutInflater().inflate(R.layout.actionbar_home, null);
        title = titleView.findViewById(R.id.actionbar_title);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);

        ab.setCustomView(titleView, params);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        layout = findViewById(R.id.layout);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        preferenceManager = PreferenceManager.getInstance(this);
        mispRestClient = new MispRestClient(this);

        populateViewsWithInfo();
        populateRecyclerView();

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
        if (item.getItemId() == R.id.main_menu_clear_and_logout) {
            clearDeviceAndLogOut();
            return true;
        }

        if (item.getItemId() == R.id.update) {
            updateProfile();
            return true;
        }

        // invoke superclass to handle unrecognized item (eg. homeAsUp)
        return super.onOptionsItemSelected(item);
    }


    public void updateProfile() {
        mispRestClient.getMyUser(new MispRestClient.UserCallback() {
            @Override
            public void success(final User user) {

                preferenceManager.setUserInfo(user);

                mispRestClient.getOrganisation(user.org_id, new MispRestClient.OrganisationCallback() {
                    @Override
                    public void success(Organisation organisation) {
                        preferenceManager.setUserOrgInfo(organisation);
                        populateViewsWithInfo();
                    }

                    @Override
                    public void failure(String error) {
                        Snackbar.make(layout, error, Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void failure(String error) {
                Snackbar.make(layout, error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void populateViewsWithInfo() {
        Organisation org = preferenceManager.getUserOrganisation();
        title.setText(org.name);

        TextView userCount = findViewById(R.id.user_count);
        userCount.setText("" + org.user_count);

        TextView sector = findViewById(R.id.sector);
        sector.setText(org.sector);

        TextView nationality = findViewById(R.id.nationality);
        nationality.setText(org.nationality);
    }

    private void populateRecyclerView() {
        List<UploadInformation> uploadInformationList = preferenceManager.getUploadInformation();
        Log.i(TAG, "Size: " + uploadInformationList.size());
        SyncAdapter syncAdapter = new SyncAdapter(uploadInformationList);
        recyclerView.setAdapter(syncAdapter);
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

    @Override
    protected void onResume() {
        super.onResume();
        populateRecyclerView();
    }
}
