package lu.circl.mispbump.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.UUID;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.MispRestClient;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.customViews.UploadAction;
import lu.circl.mispbump.models.UploadInformation;
import lu.circl.mispbump.models.restModels.Organisation;
import lu.circl.mispbump.models.restModels.Server;
import lu.circl.mispbump.models.restModels.User;


public class UploadActivity extends AppCompatActivity {

    public static String EXTRA_UPLOAD_INFO = "uploadInformation";

    private PreferenceManager preferenceManager;
    private UploadInformation uploadInformation;

    private MispRestClient restClient;
    private UploadAction availableAction, orgAction, userAction, serverAction;

    private MispRestClient.AvailableCallback availableCallback = new MispRestClient.AvailableCallback() {
        @Override
        public void available() {
            mispAvailable(true, null);
        }

        @Override
        public void unavailable(String error) {
            mispAvailable(false, error);
        }
    };
    private MispRestClient.OrganisationCallback organisationCallback = new MispRestClient.OrganisationCallback() {
        @Override
        public void success(Organisation organisation) {
            organisationAdded(organisation);
        }

        @Override
        public void failure(String error) {
            organisationAdded(null);
        }
    };
    private MispRestClient.UserCallback userCallback = new MispRestClient.UserCallback() {
        @Override
        public void success(User user) {
            userAdded(user);
        }

        @Override
        public void failure(String error) {
            userAdded(null);
        }
    };
    private MispRestClient.AllServersCallback allServersCallback = new MispRestClient.AllServersCallback() {
        @Override
        public void success(Server[] servers) {
            allServersReceived(servers);
        }

        @Override
        public void failure(String error) {
            allServersReceived(null);
        }
    };
    private MispRestClient.ServerCallback serverCallback = new MispRestClient.ServerCallback() {
        @Override
        public void success(Server server) {
            serverAdded(server);
        }

        @Override
        public void failure(String error) {
            serverAdded(null);
        }
    };

    private FloatingActionButton fab;

    private boolean errorWhileUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        preferenceManager = PreferenceManager.getInstance(UploadActivity.this);
        Pair<String, String> credentials = preferenceManager.getUserCredentials();
        restClient = MispRestClient.getInstance(credentials.first, credentials.second);

        parseExtra();
        initViews();
        startUpload();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            saveCurrentState();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveCurrentState();
    }


    private void parseExtra() {
        Intent i = getIntent();

        UUID currentUUID = (UUID) i.getSerializableExtra(EXTRA_UPLOAD_INFO);

        for (UploadInformation ui : preferenceManager.getUploadInformationList()) {
            if (ui.getUuid().compareTo(currentUUID) == 0) {
                uploadInformation = ui;
                return;
            }
        }

        if (uploadInformation == null) {
            throw new RuntimeException("Could not find UploadInfo with UUID {" + currentUUID.toString() + "}");
        }
    }

    private void initViews() {
        getWindow().setStatusBarColor(getColor(R.color.colorPrimary));

        fab = findViewById(R.id.fab);
        fab.hide();

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;

        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_close);

        availableAction = findViewById(R.id.availableAction);
        orgAction = findViewById(R.id.orgAction);
        userAction = findViewById(R.id.userAction);
        serverAction = findViewById(R.id.serverAction);
    }

    private void saveCurrentState() {
        if (errorWhileUpload) {
            uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.FAILURE);
        }
        preferenceManager.addUploadInformation(uploadInformation);
    }

    private void setUploadActionState(UploadAction uploadAction, UploadAction.UploadState state, @Nullable String error) {
        uploadAction.setCurrentUploadState(state);
        uploadAction.setError(error);

        switch (state) {
            case PENDING:
                if (fab.isShown()) {
                    fab.hide();
                }
                break;
            case LOADING:
                errorWhileUpload = false;
                if (fab.isShown()) {
                    fab.hide();
                }
                break;
            case DONE:
                errorWhileUpload = false;
                break;
            case ERROR:
                uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.FAILURE);

                fab.setImageResource(R.drawable.ic_autorenew);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setUploadActionState(availableAction, UploadAction.UploadState.LOADING, null);
                        startUpload();
                    }
                });
                if (!fab.isShown()) {
                    fab.show();
                }
                errorWhileUpload = true;
                break;
        }
    }


    private User generateSyncUser(Organisation organisation) {
        User syncUser = new User();
        syncUser.org_id = organisation.getId();
        syncUser.role_id = User.ROLE_SYNC_USER;
        syncUser.email = uploadInformation.getRemote().syncUserEmail;
        syncUser.password = uploadInformation.getRemote().syncUserPassword;
        syncUser.authkey = uploadInformation.getRemote().syncUserAuthkey;
        syncUser.termsaccepted = true;

        return syncUser;
    }

    private Server generateSyncServer() {
        Server server = new Server();
        server.name = uploadInformation.getRemote().organisation.getName() + "'s Sync Server";
        server.url = uploadInformation.getRemote().baseUrl;
        server.remote_org_id = uploadInformation.getRemote().organisation.getId();
        server.authkey = uploadInformation.getLocal().syncUserAuthkey;
        server.pull = uploadInformation.isPull();
        server.push = uploadInformation.isPush();
        server.caching_enabled = uploadInformation.isCached();
        server.self_signed = uploadInformation.isAllowSelfSigned();
        return server;
    }


    /**
     * Start upload to misp instance.
     */
    private void startUpload() {
        availableAction.setCurrentUploadState(UploadAction.UploadState.LOADING);
        restClient.isAvailable(availableCallback);
    }

    private void mispAvailable(boolean available, String error) {
        if (available) {
            setUploadActionState(availableAction, UploadAction.UploadState.DONE, null);
            restClient.addOrganisation(uploadInformation.getRemote().organisation, organisationCallback);
        } else {
            setUploadActionState(availableAction, UploadAction.UploadState.ERROR, error);
        }
    }

    private void organisationAdded(Organisation organisation) {
        if (organisation != null) {
            setUploadActionState(orgAction, UploadAction.UploadState.DONE, null);
            uploadInformation.getRemote().organisation.setId(organisation.getId());
            restClient.addUser(generateSyncUser(organisation), userCallback);
        } else {
            // search by UUID because the error does not give the actual ID
            restClient.getOrganisation(uploadInformation.getRemote().organisation.getUuid(), new MispRestClient.OrganisationCallback() {
                @Override
                public void success(Organisation organisation) {
                    organisationAdded(organisation);
                }

                @Override
                public void failure(String error) {
                    setUploadActionState(orgAction, UploadAction.UploadState.ERROR, error);
                }
            });
        }
    }

    private void userAdded(User user) {
        if (user != null) {
            setUploadActionState(userAction, UploadAction.UploadState.DONE, null);
            restClient.getAllServers(allServersCallback);
        } else {
            restClient.getUser(uploadInformation.getRemote().syncUserEmail, new MispRestClient.UserCallback() {
                @Override
                public void success(User user) {
                    userAdded(user);
                }

                @Override
                public void failure(String error) {
                    setUploadActionState(userAction, UploadAction.UploadState.ERROR, error);
                }
            });
        }
    }

    private void allServersReceived(Server[] servers) {
        if (servers != null) {
            Server serverToUpload = generateSyncServer();

            for (Server server : servers) {
                if (server.remote_org_id.equals(serverToUpload.remote_org_id)) {
                    // server already exists: override id to update instead
                    serverToUpload.id = server.id;
                    break;
                }
            }

            restClient.addServer(serverToUpload, serverCallback);
        } else {
            setUploadActionState(serverAction, UploadAction.UploadState.ERROR, "Could not retrieve server information");
        }
    }

    private void serverAdded(Server server) {
        if (server != null) {
            setUploadActionState(serverAction, UploadAction.UploadState.DONE, null);
            uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.COMPLETE);
            saveCurrentState();

            fab.setImageResource(R.drawable.ic_check);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            fab.show();
        } else {
            setUploadActionState(serverAction, UploadAction.UploadState.ERROR, "Could not add server");
        }
    }
}
