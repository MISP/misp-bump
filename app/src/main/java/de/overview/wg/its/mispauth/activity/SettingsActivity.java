package de.overview.wg.its.mispauth.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.VolleyError;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.auxiliary.PreferenceManager;
import de.overview.wg.its.mispauth.auxiliary.ReadableError;
import de.overview.wg.its.mispauth.model.Organisation;
import de.overview.wg.its.mispauth.model.User;
import de.overview.wg.its.mispauth.network.MispRequest;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG";

    private PreferenceManager preferenceManager;
    private ProgressBar progressBar;
    private TextInputLayout serverUrlLayout, apiKeyLayout;
    private EditText serverUrlText, apiKeyText;

    private Organisation org;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        serverUrlLayout = findViewById(R.id.input_layout_server_url);
        apiKeyLayout = findViewById(R.id.input_layout_api_key);
        serverUrlText = findViewById(R.id.edit_server_url);
        apiKeyText = findViewById(R.id.edit_api_key);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.fab_download_own_org_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadMyOrgInfo();
            }
        });

        apiKeyText.setOnKeyListener(new View.OnKeyListener() {
	        public boolean onKey(View v, int keyCode, KeyEvent event) {
		        if (keyCode == 66) {
		        	hideKeyboard(v);
		        	apiKeyText.clearFocus();
			        return true;
		        }
		        return false;
	        }
        });

        restoreSavedValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_deleteData) {
            serverUrlText.setText("");
            apiKeyText.setText("");
            preferenceManager.deleteAllLocalData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setOrganisation(Organisation org) {

    	if(org == null) {
    		return;
	    }

		TextView title = findViewById(R.id.organisation_title);
		TextView uuid = findViewById(R.id.organisation_uuid);
		TextView description = findViewById(R.id.organisation_description);
	    TextView nationality = findViewById(R.id.organisation_nationality);
	    TextView sector = findViewById(R.id.organisation_sector);
		TextView userCount = findViewById(R.id.organisation_user_count);

		title.setText(org.getName());
		uuid.setText(org.getUuid());
		description.setText(org.getDescription());
		nationality.setText(org.getNationality());
		sector.setText(org.getSector());
		userCount.setText("" + org.getUserCount());
    }

    private void restoreSavedValues() {
        preferenceManager = PreferenceManager.Instance(this);

        serverUrlText.setText(preferenceManager.getMyServerUrl());
        apiKeyText.setText(preferenceManager.getMyServerApiKey());

        setOrganisation(preferenceManager.getMyOrganisation());
    }

    private void downloadMyOrgInfo(){
	    user = new User();
	    org = new Organisation();

        boolean failed = false;

        String tmpServerUrl = serverUrlText.getText().toString();
        String tmpApiKey = apiKeyText.getText().toString();

        if(tmpServerUrl.isEmpty()) {
            serverUrlLayout.setError("Server URL is required");
            failed = true;
        }

        if(tmpApiKey.isEmpty()) {
            apiKeyLayout.setError("API Key is required");
            failed = true;
        }

        if(failed) {
            return;
        } else {
        	serverUrlLayout.setError(null);
        	apiKeyLayout.setError(null);
        }

        final MispRequest request = MispRequest.Instance(this);
        request.setServerCredentials(tmpServerUrl, tmpApiKey);

        progressBar.setVisibility(View.VISIBLE);

        request.myUserInformation(new MispRequest.UserCallback() {

            @Override
            public void onResult(JSONObject myUserInformation) {

            	user.fromJSON(myUserInformation);
                preferenceManager.setMyUser(user);

            	int orgID = user.getOrgId();

                request.getOrganisation(orgID, new MispRequest.OrganisationCallback() {

                	@Override
                    public void onResult(JSONObject organisationInformation) {
                        progressBar.setVisibility(View.GONE);

                        org.fromJSON(organisationInformation);

                        preferenceManager.setMyOrganisation(org);

		                setOrganisation(org);
                    }

                    @Override
                    public void onError(VolleyError volleyError) {
                        progressBar.setVisibility(View.GONE);
                        MakeSnackbar(ReadableError.toReadable(volleyError));
                        Log.e(TAG, "onError: " + volleyError.toString());
                    }
                });
            }

            @Override
            public void onError(VolleyError volleyError) {
                progressBar.setVisibility(View.GONE);
                MakeSnackbar(ReadableError.toReadable(volleyError));
            }
        });

        // If auth was successful: save new credentials
        preferenceManager.setMyServerUrl(tmpServerUrl);
        preferenceManager.setMyServerApiKey(tmpApiKey);
    }

    private void MakeSnackbar(String msg){
        View contextView = findViewById(R.id.coordinator);
        Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG).show();
    }

	private void hideKeyboard(View view) {
		InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE);
		if (manager != null) {
			manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
}
