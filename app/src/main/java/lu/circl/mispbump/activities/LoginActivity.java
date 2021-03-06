package lu.circl.mispbump.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.DialogManager;
import lu.circl.mispbump.auxiliary.MispRestClient;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.models.restModels.Organisation;
import lu.circl.mispbump.models.restModels.Role;
import lu.circl.mispbump.models.restModels.User;


public class LoginActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    private ConstraintLayout constraintLayout;
    private TextInputLayout serverAuthkey;
    private TextInputLayout serverUrl;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferenceManager = PreferenceManager.getInstance(this);

        getWindow().setStatusBarColor(getColor(R.color.colorPrimary));

        initializeViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_login_help) {
            DialogManager.loginHelpDialog(LoginActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initializeViews() {
        constraintLayout = findViewById(R.id.rootLayout);

        Toolbar myToolbar = findViewById(R.id.appbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setDisplayShowTitleEnabled(false);
        }

        Button downloadInfoButton = findViewById(R.id.login_download_button);
        downloadInfoButton.setOnClickListener(onLogin);

        serverUrl = findViewById(R.id.login_server_url);
        serverAuthkey = findViewById(R.id.login_automation_key);
        progressBar = findViewById(R.id.login_progressbar);
    }

    /**
     * Called when the user clicks on the login button.
     */
    private View.OnClickListener onLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String url = Objects.requireNonNull(serverUrl.getEditText()).getText().toString();
            final String authkey = Objects.requireNonNull(serverAuthkey.getEditText()).getText().toString();

            boolean error = false;

            serverUrl.setError(null);
            serverAuthkey.setError(null);

            if (!isValidUrl(url)) {
                error = true;
                serverUrl.setError("Invalid Server URL");
            }

            if (!isValidAutomationKey(authkey)) {
                error = true;
                serverAuthkey.setError("Invalid automation key");
            }

            if (error) {
                return;
            }

            final MispRestClient mispRestClient = MispRestClient.getInstance(url, authkey);

            // display progress bar
            progressBar.setVisibility(View.VISIBLE);

            // get my user information and the organisation associated with my user
            mispRestClient.isAvailable(new MispRestClient.AvailableCallback() {
                @Override
                public void available() {
                    mispRestClient.getRoles(new MispRestClient.AllRolesCallback() {
                        @Override
                        public void success(final Role[] roles) {
                            preferenceManager.setRoles(roles);

                            mispRestClient.getMyUser(new MispRestClient.UserCallback() {
                                @Override
                                public void success(final User user) {
                                    preferenceManager.setMyUser(user);
                                    for (Role role : roles) {
                                        if (role.getId().equals(user.getRoleId())) {
                                            if (!role.getPermAdmin()) {
                                                progressBar.setVisibility(View.GONE);
                                                Snackbar.make(constraintLayout, "No admin is associated with this authkey.", Snackbar.LENGTH_LONG).show();
                                                return;
                                            }
                                        }
                                    }

                                    mispRestClient.getOrganisation(user.getRoleId(), new MispRestClient.OrganisationCallback() {
                                        @Override
                                        public void success(Organisation organisation) {
                                            preferenceManager.setMyOrganisation(organisation);
                                            preferenceManager.setUserCredentials(url, authkey);

                                            progressBar.setVisibility(View.GONE);

                                            Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                                            startActivity(home);
                                            finish();
                                        }

                                        @Override
                                        public void failure(String error) {
                                            progressBar.setVisibility(View.GONE);
                                            Snackbar.make(constraintLayout, error, Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                }

                                @Override
                                public void failure(String error) {
                                    progressBar.setVisibility(View.GONE);
                                    Snackbar.make(constraintLayout, error, Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void failure(String error) {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(constraintLayout, error, Snackbar.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void unavailable(String error) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(constraintLayout, error, Snackbar.LENGTH_LONG).show();
                }
            });
        }
    };

    /**
     * @param url url to check
     * @return true if valid else false
     */
    private boolean isValidUrl(String url) {
        Uri uri = Uri.parse(url);

        if (uri == null) {
            return false;
        }

        return uri.getScheme() != null;
    }

    /**
     * @param automationKey the key to check
     * @return true if not empty else false
     */
    private boolean isValidAutomationKey(String automationKey) {
        return !automationKey.isEmpty();
    }
}
