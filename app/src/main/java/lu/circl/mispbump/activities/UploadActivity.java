package lu.circl.mispbump.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.DialogManager;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.custom_views.UploadAction;
import lu.circl.mispbump.models.UploadInformation;
import lu.circl.mispbump.restful_client.MispRestClient;
import lu.circl.mispbump.restful_client.MispServer;
import lu.circl.mispbump.restful_client.Organisation;
import lu.circl.mispbump.restful_client.Server;
import lu.circl.mispbump.restful_client.User;

public class UploadActivity extends AppCompatActivity {

    public static final String EXTRA_UPLOAD_INFO = "uploadInformation";

    private PreferenceManager preferenceManager;
    private MispRestClient restClient;
    private UploadInformation uploadInformation;

    private CoordinatorLayout rootLayout;

    private UploadAction availableAction, orgAction, userAction, serverAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        parseExtra();
        init();
    }

    private void parseExtra() {
        String uploadInfoString = getIntent().getStringExtra(EXTRA_UPLOAD_INFO);
        uploadInformation = new Gson().fromJson(uploadInfoString, UploadInformation.class);
        assert uploadInformation != null;
    }

    private void init() {
        preferenceManager = PreferenceManager.getInstance(this);
        restClient = new MispRestClient(this);
        rootLayout = findViewById(R.id.rootLayout);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_close);

        // fab
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpload();
            }
        });

        availableAction = findViewById(R.id.availableAction);
        orgAction = findViewById(R.id.orgAction);
        userAction = findViewById(R.id.userAction);
        serverAction = findViewById(R.id.serverAction);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                preferenceManager.addUploadInformation(uploadInformation);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

        String emailSaveOrgName = organisation.name.replace(" ", "").toLowerCase();
        String syncUserEmailFormat = uploadInformation.getRemote().syncUserEmail;
        syncUser.email = syncUserEmailFormat.replace("[ORG]", emailSaveOrgName);
        uploadInformation.getRemote().syncUserEmail = syncUser.email;

        syncUser.password = uploadInformation.getRemote().syncUserPassword;
        syncUser.authkey = uploadInformation.getRemote().syncUserAuthkey;
        syncUser.termsaccepted = true;

        return syncUser;
    }

    private MispRestClient.AvailableCallback availableCallback = new MispRestClient.AvailableCallback() {
        @Override
        public void available() {
            availableAction.setCurrentUploadState(UploadAction.UploadState.DONE);
            orgAction.setCurrentUploadState(UploadAction.UploadState.LOADING);

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int orgId = organisationExists();
                        if (orgId != -1) {
                            Snackbar.make(rootLayout, "exists", Snackbar.LENGTH_INDEFINITE).show();
                            uploadInformation.getRemote().organisation.id = orgId;
                            // TODO if exists: add User
                        } else {
                            restClient.addOrganisation(uploadInformation.getRemote().organisation, organisationCallback);
                            Snackbar.make(rootLayout, "does not exist", Snackbar.LENGTH_INDEFINITE).show();
                        }
                    } catch (IOException e) {
                        Snackbar.make(rootLayout, "Some error", Snackbar.LENGTH_INDEFINITE).show();
                        e.printStackTrace();
                    }
                }
            });

            t.start();
        }

        @Override
        public void unavailable(String error) {
            availableAction.setCurrentUploadState(UploadAction.UploadState.ERROR);
            availableAction.setError(error);
            uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.FAILURE);

            Snackbar sb = Snackbar.make(rootLayout, error, Snackbar.LENGTH_INDEFINITE);
            sb.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    availableAction.setError(null);
                    availableAction.setCurrentUploadState(UploadAction.UploadState.LOADING);
                    startUpload();
                }
            });
            sb.show();
        }
    };

    private MispRestClient.OrganisationCallback organisationCallback = new MispRestClient.OrganisationCallback() {
        @Override
        public void success(Organisation organisation) {
            orgAction.setCurrentUploadState(UploadAction.UploadState.DONE);
            userAction.setCurrentUploadState(UploadAction.UploadState.LOADING);

            // for later reference in add user callback
            uploadInformation.getRemote().organisation.id = organisation.id;

            restClient.addUser(generateSyncUser(organisation), userCallback);
        }

        @Override
        public void failure(String error) {


            // IF error = org already exists:
            // resClient.addUser()

            orgAction.setCurrentUploadState(UploadAction.UploadState.ERROR);
            uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.FAILURE);
            preferenceManager.addUploadInformation(uploadInformation);
            Snackbar.make(rootLayout, error, Snackbar.LENGTH_LONG).show();
        }
    };

    private MispRestClient.UserCallback userCallback = new MispRestClient.UserCallback() {
        @Override
        public void success(User user) {
            userAction.setCurrentUploadState(UploadAction.UploadState.DONE);

            Server server = new Server();
            server.name = uploadInformation.getRemote().organisation.name + "'s Sync Server";
            server.url = uploadInformation.getRemote().baseUrl;
            server.remote_org_id = uploadInformation.getRemote().organisation.id;
            server.authkey = uploadInformation.getLocal().syncUserAuthkey;
            server.pull = uploadInformation.isPull();
            server.push = uploadInformation.isPush();
            server.caching_enabled = uploadInformation.isCached();
            server.self_signed = uploadInformation.isAllowSelfSigned();

            restClient.addServer(server, serverCallback);
        }

        @Override
        public void failure(String error) {
            userAction.setCurrentUploadState(UploadAction.UploadState.ERROR);
            uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.FAILURE);
            preferenceManager.addUploadInformation(uploadInformation);
            Snackbar.make(rootLayout, error, Snackbar.LENGTH_LONG).show();
        }
    };

    private MispRestClient.ServerCallback serverCallback = new MispRestClient.ServerCallback() {
        @Override
        public void success(List<MispServer> servers) {

        }

        @Override
        public void success(MispServer server) {

        }

        @Override
        public void success(Server server) {
            serverAction.setCurrentUploadState(UploadAction.UploadState.DONE);
            uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.COMPLETE);
            preferenceManager.addUploadInformation(uploadInformation);
            finish();
        }

        @Override
        public void failure(String error) {
            serverAction.setCurrentUploadState(UploadAction.UploadState.ERROR);
            uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.FAILURE);
            preferenceManager.addUploadInformation(uploadInformation);
            Snackbar.make(rootLayout, error, Snackbar.LENGTH_LONG).show();
        }
    };


    private int organisationExists() throws IOException {
        final UUID uuidToCheck = UUID.fromString(uploadInformation.getRemote().organisation.uuid);

        Organisation[] organisations = restClient.getAllOrganisations();

        if (organisations != null) {
            for (Organisation organisation : organisations) {
                if (uuidToCheck.compareTo(UUID.fromString(organisation.uuid)) == 0) {
                    return organisation.id;
                }
            }
        }

        return -1;
    }

    private int userExists() {

        return -1;
    }
}
