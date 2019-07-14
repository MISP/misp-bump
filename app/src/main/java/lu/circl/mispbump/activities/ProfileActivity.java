package lu.circl.mispbump.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.MispRestClient;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.auxiliary.TileDrawable;
import lu.circl.mispbump.customViews.MaterialPreferenceText;
import lu.circl.mispbump.models.restModels.Organisation;
import lu.circl.mispbump.models.restModels.User;
import lu.circl.mispbump.security.KeyStoreWrapper;

public class ProfileActivity extends AppCompatActivity {

    private CoordinatorLayout rootLayout;
    private MispRestClient mispRestClient;
    private PreferenceManager preferenceManager;

    private FloatingActionButton fab;
    private AnimatedVectorDrawable fabLoadingDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        preferenceManager = PreferenceManager.getInstance(this);
        mispRestClient = MispRestClient.getInstance(preferenceManager.getServerUrl(), preferenceManager.getAuthKey());

        init();
        populateInformationViews();
    }

    private void init() {
        rootLayout = findViewById(R.id.rootLayout);

        ImageView headerBg = findViewById(R.id.headerBg);
        headerBg.setImageDrawable(new TileDrawable(getRandomHeader(), Shader.TileMode.REPEAT));

        // populate Toolbar (Actionbar)
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowTitleEnabled(true);
        }

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(onFabClicked());

        fabLoadingDrawable = (AnimatedVectorDrawable) getDrawable(R.drawable.animated_sync);
    }

    private void populateInformationViews() {
        Organisation organisation = preferenceManager.getUserOrganisation();

        TextView name = findViewById(R.id.orgName);
        name.setText(organisation.name);

        final MaterialPreferenceText uuid = findViewById(R.id.uuid);
        uuid.setSubtitle(organisation.uuid);

        MaterialPreferenceText nationality = findViewById(R.id.nationality);
        nationality.setSubtitle(organisation.nationality);

        MaterialPreferenceText sector = findViewById(R.id.sector);
        if (organisation.sector == null) {
            sector.setVisibility(View.GONE);
        } else {
            sector.setSubtitle(organisation.sector);
        }

        MaterialPreferenceText description = findViewById(R.id.description);
        description.setSubtitle(organisation.description);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_delete_profile) {
            clearDeviceAndLogOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener onFabClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.setImageDrawable(fabLoadingDrawable);
                fabLoadingDrawable.start();
                updateProfile();
            }
        };
    }

    private Drawable getRandomHeader() {
        int[] ids = {R.drawable.ic_bank_note, R.drawable.ic_polka_dots, R.drawable.ic_wiggle, R.drawable.ic_circuit_board};
        return getDrawable(ids[new Random().nextInt(ids.length)]);
    }

    public void updateProfile() {
//        progressBar.setVisibility(View.VISIBLE);

        mispRestClient.getMyUser(new MispRestClient.UserCallback() {
            @Override
            public void success(final User user) {

                preferenceManager.setUserInfo(user);

                mispRestClient.getOrganisation(user.org_id, new MispRestClient.OrganisationCallback() {
                    @Override
                    public void success(Organisation organisation) {
                        fabLoadingDrawable.stop();
                        preferenceManager.setUserOrgInfo(organisation);
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
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Delete & Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceManager prefs = PreferenceManager.getInstance(ProfileActivity.this);
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
