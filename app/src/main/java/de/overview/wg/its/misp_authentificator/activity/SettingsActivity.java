package de.overview.wg.its.misp_authentificator.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import de.overview.wg.its.misp_authentificator.PreferenceManager;
import de.overview.wg.its.misp_authentificator.R;
import de.overview.wg.its.misp_authentificator.network.MispRequest;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "MISP-TAG";

    private PreferenceManager preferenceManager;
    private ProgressBar progressBar;
    private TextInputLayout serverUrlLayout, apiKeyLayout;
    private EditText serverUrlText, apiKeyText;

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

        FloatingActionButton fabDlOrgInfo = findViewById(R.id.fab_download_own_org_info);

        fabDlOrgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadMyOrgInfo();
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


    private void restoreSavedValues() {
        preferenceManager = PreferenceManager.Instance(this);

        serverUrlText.setText(preferenceManager.getMyServerUrl());
        apiKeyText.setText(preferenceManager.getMyServerApiKey());
    }

    private void downloadMyOrgInfo(){

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
        }

        final MispRequest request = MispRequest.Instance(this);

        request.setServerCredentials(tmpServerUrl, tmpApiKey);

        progressBar.setVisibility(View.VISIBLE);

        request.myUserInformation(new MispRequest.UserInformationCallback() {
            @Override
            public void onResult(JSONObject myUserInformation) {

                int orgID;

                try {
                    orgID = myUserInformation.getInt("org_id");

                    request.OrganisationInformation(orgID, new MispRequest.OrganisationInformationCallback() {
                        @Override
                        public void onResult(JSONObject organisationInformation) {
                            progressBar.setVisibility(View.GONE);
                            Log.i(TAG, "onResult: " + organisationInformation.toString());
                        }

                        @Override
                        public void onError(VolleyError volleyError) {
                            progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "onError: " + volleyError.toString());
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                progressBar.setVisibility(View.GONE);

                if(volleyError instanceof NoConnectionError) {
                    MakeAlert("No connection to server");
                } else if(volleyError instanceof AuthFailureError) {
                    MakeAlert("Wrong API key");
                }
            }
        });

        // If auth was successful: save new credentials
        preferenceManager.setMyServerUrl(tmpServerUrl);
        preferenceManager.setMyServerApiKey(tmpApiKey);

    }

    private void MakeAlert(String msg){

        View contextView = findViewById(R.id.coordinator);

        Snackbar.make(contextView, msg, Snackbar.LENGTH_LONG)
                .addCallback(new Snackbar.Callback(){
                    @Override
                    public void onShown(Snackbar sb) {
                        progressBar.setVisibility(View.GONE);
                    }
                }).show();
    }
}
