package de.overview.wg.its.mispbump;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import com.android.volley.VolleyError;
import de.overview.wg.its.mispbump.adapter.OrganisationInfoEntryAdapter;
import de.overview.wg.its.mispbump.auxiliary.PreferenceManager;
import de.overview.wg.its.mispbump.auxiliary.ReadableError;
import de.overview.wg.its.mispbump.model.Organisation;
import de.overview.wg.its.mispbump.model.StringPair;
import de.overview.wg.its.mispbump.model.User;
import de.overview.wg.its.mispbump.network.MispRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyOrganisationActivity extends AppCompatActivity implements View.OnClickListener {

    private PreferenceManager preferenceManager;
    private RecyclerView recyclerView;
    private OrganisationInfoEntryAdapter adapter;
    private View empty;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_organisation);

        initializeContent();
        loadMyInformation();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.fab_download:

                enterCredentialsDialog();

                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_org, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.menu_delete_local_data:
                deleteLocalDataDialog();
                break;

            case R.id.load_config:

                if (Build.VERSION.SDK_INT > 25) {
                    preferenceManager.setServerUrl("http://192.168.178.200");
                    preferenceManager.setAutomationKey("d2UEstcJiySUWpsaeiXnEFGoI1xcWhAEIiVgZOmW");
                } else {
                    preferenceManager.setServerUrl("http://192.168.178.201");
                    preferenceManager.setAutomationKey("eCcz0TTLEc8MeZihsoyyeqlYpd4V8PCDsDA4tM75");
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initializeContent() {

        Toolbar t = findViewById(R.id.toolbar);
        setSupportActionBar(t);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        empty = findViewById(R.id.empty);
        progressBar = findViewById(R.id.progressBar);

        adapter = new OrganisationInfoEntryAdapter(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_download);
        fab.setOnClickListener(this);

        preferenceManager = PreferenceManager.Instance(this);
    }

    private void storeCredentials(String url, String automationKey, boolean saveAutomationKey) {

        if (saveAutomationKey) {
            preferenceManager.setAutomationKey(automationKey);
        } else {
            preferenceManager.setAutomationKey("");
        }

        preferenceManager.setServerUrl(url);
    }

    private void storeMyInformation(Organisation org, User user) {

        if (org != null) {
            preferenceManager.setMyOrganisation(org);
        }

        if (user != null) {
            preferenceManager.setMyUser(user);
        }

    }

    private void loadMyInformation() {

        empty.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        Organisation myOrg = preferenceManager.getMyOrganisation();
        User myUser = preferenceManager.getMyUser();

        visualizeInformation(myOrg, myUser);
    }

    private void downloadOrganisationInformation(String url, String automationKey) {

        empty.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        final MispRequest mispRequest = MispRequest.Instance(this, false);
        mispRequest.setServerCredentials(url, automationKey);

        final User myUser = new User();
        final Organisation myOrganisation = new Organisation();

        mispRequest.getMyUser(new MispRequest.UserCallback() {
            @Override
            public void onResult(JSONObject jsonUser) {
                try {

                    myUser.fromJSON(jsonUser);

                } catch (JSONException e) {

                    alert(e.getMessage());

                    return;
                }

                mispRequest.getOrganisation(myUser.getId(), new MispRequest.OrganisationCallback() {
                    @Override
                    public void onResult(JSONObject organisationInformation) {
                        try {

                            myOrganisation.fromJSON(organisationInformation);

                            storeMyInformation(myOrganisation, myUser);
                            visualizeInformation(myOrganisation, myUser);

                        } catch (JSONException e) {

                            alert(e.getMessage());
                            visualizeInformation(null, null);

                            return;
                        }
                    }

                    @Override
                    public void onError(VolleyError volleyError) {
                        alert(ReadableError.toReadable(volleyError));
                        visualizeInformation(null, null);
                    }
                });
            }

            @Override
            public void onError(VolleyError volleyError) {
                alert(ReadableError.toReadable(volleyError));
                visualizeInformation(null, null);
            }
        });
    }

    private void visualizeInformation(Organisation org, User user) {

        progressBar.setVisibility(View.GONE);

        if (org != null && user != null) {

            empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        } else {

            empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            return;
        }

        getSupportActionBar().setTitle(org.getName());

        List<StringPair> orgInfoEntries = new ArrayList<>();

        orgInfoEntries.add(new StringPair("UUID", org.getUuid()));
        orgInfoEntries.add(new StringPair("Description", org.getDescription()));
        orgInfoEntries.add(new StringPair("Nationality", org.getNationality()));
        orgInfoEntries.add(new StringPair("Sector", org.getSector()));
        orgInfoEntries.add(new StringPair("User Count", "" + org.getUserCount()));

        adapter.setList(orgInfoEntries);

    }

    private void enterCredentialsDialog() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        adb.setTitle("MISP Credentials");

        View v = inflater.inflate(R.layout.dialog_enter_credentials, null);
        adb.setView(v);

        final CheckBox saveAutomationKey = v.findViewById(R.id.check_save_authkey);
        final TextInputLayout serverUrlLayout = v.findViewById(R.id.input_layout_server_url);
        final TextInputLayout automationKeyLayout = v.findViewById(R.id.input_layout_automation_key);

        saveAutomationKey.setChecked(preferenceManager.saveAuthKeyEnabled());
        serverUrlLayout.getEditText().setText(preferenceManager.getMyServerUrl());
        automationKeyLayout.getEditText().setText(preferenceManager.getMyServerAutomationKey());

        adb.setPositiveButton("Download", null);
        adb.setNegativeButton(android.R.string.cancel, null);

        final Dialog dialog = adb.create();
        dialog.show();

        Button posButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

        posButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = serverUrlLayout.getEditText().getText().toString();
                String automationKey = automationKeyLayout.getEditText().getText().toString();

                boolean validCredentials = true;

                if (url.equals("")) {
                    validCredentials = false;
                    serverUrlLayout.setError(getString(R.string.error_url_required));
                }

                if (automationKey.equals("")) {
                    validCredentials = false;
                    automationKeyLayout.setError(getString(R.string.error_automation_key));
                }

                boolean save = saveAutomationKey.isChecked();
                preferenceManager.setSaveAuthKeyEnabled(save);

                if (validCredentials) {
                    dialog.dismiss();
                    storeCredentials(url, automationKey, save);
                    downloadOrganisationInformation(url, automationKey);
                }
            }
        });
    }

    private void deleteLocalDataDialog() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        adb.setTitle("Delete Local Data");

        adb.setMessage(getString(R.string.delete_local_data_msg));

        adb.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                preferenceManager.clearCredentialPreferences();
            }
        });

        adb.setNegativeButton(android.R.string.cancel, null);

        adb.create().show();
    }

    private void alert(String message) {
        Snackbar.make(findViewById(R.id.coordinator), message, Snackbar.LENGTH_LONG).show();
    }
}
