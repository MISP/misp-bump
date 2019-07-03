package lu.circl.mispbump.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

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

    private CoordinatorLayout rootLayout;
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


    private boolean errorWhileUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        preferenceManager = PreferenceManager.getInstance(UploadActivity.this);
        restClient = MispRestClient.getInstance(this);

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
        rootLayout = findViewById(R.id.rootLayout);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayShowTitleEnabled(true);
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

    /**
     * Start upload to misp instance.
     */
    private void startUpload() {
        availableAction.setCurrentUploadState(UploadAction.UploadState.LOADING);
        restClient.isAvailable(availableCallback);
    }


    private User generateSyncUser(Organisation organisation) {
        User syncUser = new User();
        syncUser.org_id = organisation.id;
        syncUser.role_id = User.ROLE_SYNC_USER;
        syncUser.email = uploadInformation.getRemote().syncUserEmail;
        syncUser.password = uploadInformation.getRemote().syncUserPassword;
        syncUser.authkey = uploadInformation.getRemote().syncUserAuthkey;
        syncUser.termsaccepted = true;

        return syncUser;
    }

    private Server generateSyncServer() {
        Server server = new Server();
        server.name = uploadInformation.getRemote().organisation.name + "'s Sync Server";
        server.url = uploadInformation.getRemote().baseUrl;
        server.remote_org_id = uploadInformation.getRemote().organisation.id;
        server.authkey = uploadInformation.getLocal().syncUserAuthkey;
        server.pull = uploadInformation.isPull();
        server.push = uploadInformation.isPush();
        server.caching_enabled = uploadInformation.isCached();
        server.self_signed = uploadInformation.isAllowSelfSigned();
        return server;
    }


    private void mispAvailable(boolean available, String error) {
        if (available) {
            availableAction.setCurrentUploadState(UploadAction.UploadState.DONE);
            availableAction.setError(null);

            restClient.addOrganisation(uploadInformation.getRemote().organisation, organisationCallback);
        } else {
            availableAction.setCurrentUploadState(UploadAction.UploadState.ERROR);
            availableAction.setError(error);

            uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.FAILURE);
            errorWhileUpload = true;

            Snackbar sb = Snackbar.make(rootLayout, error, Snackbar.LENGTH_INDEFINITE);

            sb.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    availableAction.setError(null);
                    availableAction.setCurrentUploadState(UploadAction.UploadState.LOADING);
                    errorWhileUpload = false;
                    startUpload();
                }
            });

            sb.show();
        }
    }

    private void organisationAdded(Organisation organisation) {
        if (organisation != null) {
            orgAction.setCurrentUploadState(UploadAction.UploadState.DONE);

            uploadInformation.getRemote().organisation.id = organisation.id;
            restClient.addUser(generateSyncUser(organisation), userCallback);
        } else {
            restClient.getOrganisation(uploadInformation.getRemote().organisation.uuid, new MispRestClient.OrganisationCallback() {
                @Override
                public void success(Organisation organisation) {
                    organisationAdded(organisation);
                }

                @Override
                public void failure(String error) {
                    uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.FAILURE);
                    orgAction.setCurrentUploadState(UploadAction.UploadState.ERROR);
                    orgAction.setError(error);
                    errorWhileUpload = true;
                }
            });
        }
    }

    private void userAdded(User user) {
        if (user != null) {
            userAction.setCurrentUploadState(UploadAction.UploadState.DONE);
            restClient.getAllServers(allServersCallback);
        } else {

            restClient.getUser(uploadInformation.getRemote().syncUserEmail, new MispRestClient.UserCallback() {
                @Override
                public void success(User user) {
                    userAdded(user);
                }

                @Override
                public void failure(String error) {
                    uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.FAILURE);
                    userAction.setCurrentUploadState(UploadAction.UploadState.ERROR);
                    userAction.setError(error);
                    errorWhileUpload = true;
                }
            });
        }
    }

    private void allServersReceived(Server[] servers) {
        Server serverToUpload = generateSyncServer();

        for (Server server : servers) {
            if (server.remote_org_id.equals(serverToUpload.remote_org_id)) {
                // server already exists
                serverToUpload.id = server.id;
                break;
            }
        }

        restClient.addServer(serverToUpload, serverCallback);
    }

    private void serverAdded(Server server) {
        if (server != null) {
            serverAction.setCurrentUploadState(UploadAction.UploadState.DONE);
            uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.COMPLETE);
            saveCurrentState();
        } else {
            uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.FAILURE);
            serverAction.setCurrentUploadState(UploadAction.UploadState.ERROR);
            serverAction.setError("Could not add server");
            errorWhileUpload = true;
        }
    }

}
