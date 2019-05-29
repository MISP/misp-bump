package lu.circl.mispbump.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.Objects;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.DialogManager;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.restful_client.MispRestClient;
import lu.circl.mispbump.restful_client.Organisation;
import lu.circl.mispbump.restful_client.User;

public class LoginActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private TextInputLayout serverUrl;
    private TextInputLayout serverAutomationKey;
    private ProgressBar progressBar;

    private PreferenceManager preferenceManager;
    private View.OnClickListener onClickDownload = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String url = Objects.requireNonNull(serverUrl.getEditText()).getText().toString();
            String authkey = Objects.requireNonNull(serverAutomationKey.getEditText()).getText().toString();

            boolean error = false;

            serverUrl.setError(null);
            serverAutomationKey.setError(null);

            if (!isValidUrl(url)) {
                error = true;
                serverUrl.setError("Invalid Server URL");
            }

            if (!isValidAutomationKey(authkey)) {
                error = true;
                serverAutomationKey.setError("Invalid automation key");
            }

            if (error) {
                return;
            }

            // save authkey and url for login
            preferenceManager.setAutomationKey(authkey);
            preferenceManager.setServerUrl(url);

            // instance of MispRestClient with given URL
            final MispRestClient mispRestClient = new MispRestClient(getApplicationContext());

            // display progress bar
            progressBar.setVisibility(View.VISIBLE);

            // get my user information and the organisation associated with my user
            mispRestClient.getMyUser(new MispRestClient.UserCallback() {
                @Override
                public void success(final User user) {

                    preferenceManager.setUserInfo(user);

                    mispRestClient.getOrganisation(user.org_id, new MispRestClient.OrganisationCallback() {
                        @Override
                        public void success(Organisation organisation) {
                            preferenceManager.setUserOrgInfo(organisation);
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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // populate Toolbar (Actionbar)
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
        }

        constraintLayout = findViewById(R.id.login_root);
        progressBar = findViewById(R.id.login_progressbar);
        serverUrl = findViewById(R.id.login_server_url);
        serverAutomationKey = findViewById(R.id.login_automation_key);
        Button downloadInfoButton = findViewById(R.id.login_download_button);

        downloadInfoButton.setOnClickListener(onClickDownload);

        preferenceManager = PreferenceManager.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_login_help:
                showHelpDialog();
                return true;

            default:
                // invoke superclass to handle unrecognized item (eg. homeAsUp)
                return super.onOptionsItemSelected(item);

        }
    }

    private void showHelpDialog() {
        DialogManager.loginHelpDialog(LoginActivity.this);
    }

    private boolean isValidUrl(String url) {
        return url.startsWith("https://") || url.startsWith("http://");
    }

    private boolean isValidAutomationKey(String automationKey) {
        return !TextUtils.isEmpty(automationKey);
    }
}
