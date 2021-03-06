package lu.circl.mispbump.activities;


import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.util.Pair;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.MispRestClient;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.auxiliary.TileDrawable;
import lu.circl.mispbump.customViews.MaterialPreferenceText;
import lu.circl.mispbump.models.restModels.Organisation;
import lu.circl.mispbump.models.restModels.Role;
import lu.circl.mispbump.models.restModels.User;
import lu.circl.mispbump.security.KeyStoreWrapper;


public class ProfileActivity extends AppCompatActivity {

    private CoordinatorLayout rootLayout;
    private MispRestClient mispRestClient;
    private PreferenceManager preferenceManager;

    private FloatingActionButton fab;
    private AnimatedVectorDrawable fabLoadingDrawable;

    private View.OnClickListener onFabClicked = view -> {
        fab.setImageDrawable(fabLoadingDrawable);
        fabLoadingDrawable.start();
        updateProfileInformation();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        preferenceManager = PreferenceManager.getInstance(this);
        Pair<String, String> credentials = preferenceManager.getUserCredentials();
        mispRestClient = MispRestClient.getInstance(credentials.first, credentials.second);

        initToolbar();
        initViews();

        populateInformationViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.menu_delete_profile) {
            clearDeviceAndLogOut();
            return true;
        }

        return false;
    }


    private void initToolbar() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowTitleEnabled(true);
        }
    }

    private void initViews() {
        rootLayout = findViewById(R.id.rootLayout);

        ImageView headerBg = findViewById(R.id.headerBg);
        headerBg.setImageDrawable(new TileDrawable(getRandomHeader(), Shader.TileMode.REPEAT));

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(onFabClicked);

        fabLoadingDrawable = (AnimatedVectorDrawable) getDrawable(R.drawable.animated_sync);
    }

    private void populateInformationViews() {
        Organisation organisation = preferenceManager.getUserOrganisation();

        TextView name = findViewById(R.id.orgName);
        name.setText(organisation.getName());

        final MaterialPreferenceText uuid = findViewById(R.id.uuid);
        uuid.setSubtitle(organisation.getUuid());

        MaterialPreferenceText nationality = findViewById(R.id.nationality);
        nationality.setSubtitle(organisation.getNationality());

        MaterialPreferenceText sector = findViewById(R.id.sector);
        sector.setSubtitle(organisation.getSector());

        MaterialPreferenceText description = findViewById(R.id.description);
        description.setSubtitle(organisation.getDescription());

        Role[] roles = preferenceManager.getRoles();
        for (Role role : roles) {
            Log.d("ROLES", role.toString());
        }
    }


    public void updateProfileInformation() {
        mispRestClient.getRoles(new MispRestClient.AllRolesCallback() {
            @Override
            public void success(Role[] roles) {
                preferenceManager.setRoles(roles);
            }

            @Override
            public void failure(String error) {
                Snackbar.make(rootLayout, error, Snackbar.LENGTH_LONG).show();
            }
        });

        mispRestClient.getMyUser(new MispRestClient.UserCallback() {
            @Override
            public void success(final User user) {
                preferenceManager.setMyUser(user);
                mispRestClient.getOrganisation(user.getRoleId(), new MispRestClient.OrganisationCallback() {
                    @Override
                    public void success(Organisation organisation) {
                        fabLoadingDrawable.stop();
                        preferenceManager.setMyOrganisation(organisation);
                        Snackbar.make(rootLayout, "Successfully update profile", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(String error) {
                        fabLoadingDrawable.stop();
                        Snackbar.make(rootLayout, error, Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void failure(String error) {
                fabLoadingDrawable.stop();
                Snackbar.make(rootLayout, error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void clearDeviceAndLogOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Clear all saved data and logout");
        builder.setMessage("Do you really want to delete all data and logout?");
        builder.setNegativeButton("Discard", (dialog, which) -> dialog.cancel());

        builder.setPositiveButton("Delete & Logout", (dialog, which) -> {
            preferenceManager.clearAllData();
            KeyStoreWrapper.deleteAllStoredKeys();

            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(login);
            finish();
        });

        builder.create().show();
    }


    private Drawable getRandomHeader() {
        int[] ids = {R.drawable.ic_bank_note, R.drawable.ic_polka_dots, R.drawable.ic_wiggle, R.drawable.ic_circuit_board};
        return getDrawable(ids[new Random().nextInt(ids.length)]);
    }
}
