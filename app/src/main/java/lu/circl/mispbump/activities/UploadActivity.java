package lu.circl.mispbump.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import java.util.UUID;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.MispRestClient;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.customViews.ProgressActionView;
import lu.circl.mispbump.models.SyncInformation;
import lu.circl.mispbump.models.restModels.Organisation;
import lu.circl.mispbump.models.restModels.Server;
import lu.circl.mispbump.models.restModels.User;


public class UploadActivity extends AppCompatActivity {

    public static final String EXTRA_SYNC_INFO_UUID = "EXTRA_SYNC_INFO_UUID";

    private View rootLayout;

    private PreferenceManager preferenceManager;
    private MispRestClient mispRest;
    private SyncInformation syncInformation;

    private ProgressActionView availableAction, organisationAction, userAction, serverAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        rootLayout = findViewById(R.id.rootLayout);

        preferenceManager = PreferenceManager.getInstance(UploadActivity.this);

        Pair<String, String> credentials = preferenceManager.getUserCredentials();
        mispRest = MispRestClient.getInstance(credentials.first, credentials.second);

        parseExtra();
        initToolbar();
        initProgressActionViews();
        startUpload();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return false;
    }


    private void parseExtra() {
        Intent i = getIntent();
        String syncInfoUuid = i.getStringExtra(EXTRA_SYNC_INFO_UUID);
        syncInformation = preferenceManager.getSyncInformation(UUID.fromString(syncInfoUuid));
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

    private void initProgressActionViews() {
        availableAction = findViewById(R.id.availableProgressAction);
        organisationAction = findViewById(R.id.organisationProgressAction);
        userAction = findViewById(R.id.userProgressAction);
        serverAction = findViewById(R.id.serverProgressAction);

        availableAction.pending();
        organisationAction.pending();
        userAction.pending();
        serverAction.pending();
    }


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


    private User generateSyncUser(Organisation organisation) {
        User syncUser = syncInformation.getSyncUser();

        syncUser.setOrg_id(organisation.getId());
        syncUser.setRole_id(6);
        syncUser.setTermsaccepted(true);

        return syncUser;
    }

    private Server generateSyncServer() {
        Server server = syncInformation.getSyncServer();
        server.setName(syncInformation.getRemoteOrganisation().getName() + "'s Sync Server");
        server.setRemote_org_id(syncInformation.getRemoteOrganisation().getId());
        server.setAuthkey(syncInformation.getLocal().getSyncUser().getAuthkey());
        server.setPull(syncInformation.getSyncServer().getPull());
        server.setPush(syncInformation.getSyncServer().getPush());
        server.setCaching_enabled(syncInformation.getSyncServer().getCaching_enabled());
        server.setSelf_signed(syncInformation.getSyncServer().getCaching_enabled());
        return server;
    }


    private void startUpload() {
        availableAction.start();
        mispRest.isAvailable(availableCallback);
    }

    private void mispAvailable(boolean available, String error) {
        if (available) {
            availableAction.done();
            organisationAction.start();

            mispRest.addOrganisation(syncInformation.getRemoteOrganisation(), organisationCallback);
        } else {
            availableAction.error(error);
        }
    }

    private void organisationAdded(Organisation organisation) {
        if (organisation != null) {
            organisationAction.done();
            userAction.start();

            syncInformation.getRemoteOrganisation().setId(organisation.getId());
            mispRest.addUser(generateSyncUser(organisation), userCallback);
        } else {
            mispRest.getOrganisation(syncInformation.getRemoteOrganisation().getUuid(), new MispRestClient.OrganisationCallback() {
                @Override
                public void success(Organisation organisation) {
                    organisationAdded(organisation);
                    organisationAction.done("Organisation already on MISP instance");
                }

                @Override
                public void failure(String error) {
                    organisationAction.error(error);
                }
            });
        }
    }

    private void userAdded(User user) {
        if (user != null) {
            userAction.done();
            serverAction.start();

            mispRest.getAllServers(allServersCallback);
        } else {
            mispRest.getUser(syncInformation.getLocal().getSyncUser().getEmail(), new MispRestClient.UserCallback() {
                @Override
                public void success(User user) {
                    userAction.done("User already on MISP instance");
                    userAdded(user);
                }

                @Override
                public void failure(String error) {
                    userAction.error(error);
                }
            });
        }
    }

    private void allServersReceived(Server[] servers) {
        if (servers != null) {
            Server serverToUpload = generateSyncServer();

            for (Server server : servers) {
                if (server.getRemote_org_id().equals(serverToUpload.getRemote_org_id())) {
                    // server already exists: override id to update instead
                    serverToUpload.setId(server.getId());
                    break;
                }
            }

            mispRest.addServer(serverToUpload, serverCallback);
        } else {
            serverAction.error("Unknown error while creating the Sync Server");
        }
    }

    private void serverAdded(Server server) {
        if (server != null) {
            serverAction.done();
            preferenceManager.addSyncInformation(syncInformation);
        }
    }
}
