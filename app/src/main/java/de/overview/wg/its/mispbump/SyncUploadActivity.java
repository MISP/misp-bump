package de.overview.wg.its.mispbump;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import de.overview.wg.its.mispbump.adapter.UploadStateAdapter;
import de.overview.wg.its.mispbump.auxiliary.ReadableError;
import de.overview.wg.its.mispbump.auxiliary.TempAuth;
import de.overview.wg.its.mispbump.model.*;
import de.overview.wg.its.mispbump.network.MispRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class SyncUploadActivity extends AppCompatActivity implements View.OnClickListener {

    static final String PARTNER_INFO_BUNDLE_KEY = "partner_info";

    private FloatingActionButton fabStart, fabFinish, fabRetry;

    private MispRequest mispRequest;

    private Organisation partnerOrganisation;
    private Server partnerServer;
    private User partnerSyncUser;

    private UploadStateAdapter uploadStateAdapter;
    private UploadState[] uploadStates;
    private int currentTask = 0;

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.fab_start) {
            startUpload();
            fabStart.setVisibility(View.GONE);
        }

        if (id == R.id.fab_retry) {
            fabRetry.setVisibility(View.GONE);
            fabFinish.setVisibility(View.GONE);

            //TODO retry implementation
        }

        if (id == R.id.fab_finish) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initializeContent();
    }


    private void initializeContent() {

        // Toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // FABs

        fabStart = findViewById(R.id.fab_start);
        fabStart.setVisibility(View.VISIBLE);
        fabStart.setOnClickListener(this);

        fabFinish = findViewById(R.id.fab_finish);
        fabFinish.setVisibility(View.GONE);
        fabFinish.setOnClickListener(this);

        fabRetry = findViewById(R.id.fab_retry);
        fabRetry.setVisibility(View.GONE);
        fabRetry.setOnClickListener(this);

        // RecyclerView

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        uploadStateAdapter = new UploadStateAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(uploadStateAdapter);

        // UploadStates

        uploadStates = new UploadState[6];

        uploadStates[0] = new UploadState("Validate upload information");
        uploadStates[1] = new UploadState("Check connection to server");
        uploadStates[2] = new UploadState("Create local organisation");
        uploadStates[3] = new UploadState("Create sync user & add to organisation");
        uploadStates[4] = new UploadState("Create external organisation");
        uploadStates[5] = new UploadState("Create sync server");

        uploadStateAdapter.setStates(uploadStates);

        // Request

        mispRequest = MispRequest.Instance(this, true);
    }

    private void startUpload() {
        currentTask = 0;
        executeTask(currentTask);
    }

    private void undoTask(int index) {
        switch (index) {

            case 2:
                createOrganisation(uploadStates[index], true);
                break;

            case 3:
                createSyncUser(uploadStates[index], true);
                break;

            case 4:
                createExternalOrganisation(uploadStates[index], true);
                break;

            case 5:
                createSyncServer(uploadStates[index], true);
                break;

        }
    }

    private void executeTask(int index) {

        switch (index) {
            case 0:
                checkBundle(uploadStates[index]);
                break;

            case 1:
                checkConnection(uploadStates[index]);
                break;

            case 2:
                createOrganisation(uploadStates[index], false);
                break;

            case 3:
                createSyncUser(uploadStates[index], false);
                break;

            case 4:
                createExternalOrganisation(uploadStates[index], false);
                break;

            case 5:
                createSyncServer(uploadStates[index], false);
                break;

        }

        uploadStateAdapter.notifyDataSetChanged();
    }

    private void executeNextTask() {

        currentTask++;

        if (currentTask > uploadStates.length) {
            return;
        }

        executeTask(currentTask);
    }

    private void setApplicationError(boolean canRetry) {

        setErrorOnRemainingTasks();

        uploadStateAdapter.notifyDataSetChanged();

        fabFinish.setVisibility(View.VISIBLE);

        if (canRetry) {
            fabRetry.setVisibility(View.VISIBLE);
        }
    }

    private void setErrorOnRemainingTasks() {

        boolean errorFound = false;

        for(UploadState state : uploadStates) {

            if (!errorFound && state.getCurrentState() == UploadState.State.ERROR) {
                errorFound = true;
                continue;
            }

            if (errorFound) {
                state.setFollowError();
            }
        }
    }

    // Upload States

    private void checkBundle(UploadState state) {
        state.setInProgress();

        Bundle b = getIntent().getExtras();

        if (b != null) {

            String info = b.getString(PARTNER_INFO_BUNDLE_KEY);

            SyncInformationQr partnerInformation = new Gson().fromJson(info, SyncInformationQr.class);

            partnerOrganisation = partnerInformation.getOrganisation();
            partnerServer = partnerInformation.getServer();
            partnerSyncUser = partnerInformation.getUser();

            if (partnerOrganisation == null || partnerServer == null || partnerSyncUser == null) {
                state.setError("Partners information format is incorrect");
                setApplicationError(false);
            } else {
                state.setDone();
                executeNextTask();
            }

        } else {
            state.setError("Partners information format is incorrect");
            setApplicationError(false);
        }
    }

    private void checkConnection(final UploadState state) {
        state.setInProgress();

        mispRequest.testConnection(new MispRequest.ConnectionCallback() {
            @Override
            public void onResult(boolean connected) {
                if (connected) {
                    state.setDone();
                    executeNextTask();
                } else {
                    state.setError("Could not connect to server");
                    setApplicationError(true);
                }
            }
        });
    }

    private void createOrganisation(final UploadState state, boolean undo) {

        state.setInProgress();

        if (!undo) {
            mispRequest.addOrganisation(partnerOrganisation, new MispRequest.OrganisationCallback() {
                @Override
                public void onResult(JSONObject organisationInformation) {
                    try {

                        partnerSyncUser.setOrgId(new Organisation(organisationInformation).getId());

                        state.setDone();
                        executeNextTask();

                    } catch (JSONException e) {
                        state.setError("Unknown error: could not read server response");
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(VolleyError volleyError) {
                    state.setError(ReadableError.toReadable(volleyError));
                    setApplicationError(true);
                }
            });
        } else {
            mispRequest.removeOrganisation(partnerOrganisation.getId(), new MispRequest.DeleteCallback() {
                @Override
                public void onSuccess() {
                    state.setDone();
                }

                @Override
                public void onError(VolleyError volleyError) {
                    state.setError(ReadableError.toReadable(volleyError));
                }
            });
        }
    }

    private void createSyncUser(final UploadState state, boolean undo) {

        state.setInProgress();

        partnerSyncUser.setAuthkey(TempAuth.TMP_AUTH_KEY);
        partnerSyncUser.setRoleId(User.RoleId.SYNC_USER);

        if (!undo) {
            mispRequest.addUser(partnerSyncUser, new MispRequest.UserCallback() {
                @Override
                public void onResult(JSONObject myUserInformation) {
                    state.setDone();
                    executeNextTask();
                }

                @Override
                public void onError(VolleyError volleyError) {
                    state.setError(ReadableError.toReadable(volleyError));
                    setApplicationError(true);
                }
            });
        } else {
            mispRequest.removeUser(partnerSyncUser.getId(), new MispRequest.DeleteCallback() {
                @Override
                public void onSuccess() {
                    state.setDone();
                }

                @Override
                public void onError(VolleyError volleyError) {
                    state.setError(ReadableError.toReadable(volleyError));
                }
            });
        }
    }

    private void createExternalOrganisation(final UploadState state, boolean undo) {

        final String originalOrgName = partnerOrganisation.getName();

        state.setInProgress();

        if (!undo) {
            partnerOrganisation.setName(partnerOrganisation.getName() + " (Remote)");
            partnerOrganisation.setLocal(false);

            mispRequest.addOrganisation(partnerOrganisation, new MispRequest.OrganisationCallback() {

                @Override
                public void onResult(JSONObject organisationInformation) {
                    try {

                        int extOrgId = new Organisation(organisationInformation).getId();
                        partnerServer.setRemoteOrgId(extOrgId);
                        partnerServer.setPush(true);

                        // Reset partner organisation name TODO why?
                        partnerOrganisation.setName(originalOrgName);

                        state.setDone();
                        executeNextTask();

                    } catch (JSONException e) {
                        state.setError("Could not interpret server response");
                    }
                }

                @Override
                public void onError(VolleyError volleyError) {
                    state.setError(ReadableError.toReadable(volleyError));
                    setApplicationError(true);
                }
            });
        } else {
            mispRequest.removeOrganisation(partnerOrganisation.getId(), new MispRequest.DeleteCallback() {
                @Override
                public void onSuccess() {
                    state.setDone();
                }

                @Override
                public void onError(VolleyError volleyError) {
                    state.setError(ReadableError.toReadable(volleyError));
                }
            });
        }
    }

    private void createSyncServer(final UploadState state, boolean undo) {
        state.setInProgress();

        if (!undo) {
            mispRequest.addServer(partnerServer, new MispRequest.ServerCallback() {
                @Override
                public void onResult(JSONObject servers) {
                    state.setDone();
                    executeNextTask();
                }

                @Override
                public void onError(VolleyError volleyError) {
                    state.setError(ReadableError.toReadable(volleyError));
                }
            });
        } else {

        }
    }

    private void addToSyncedList() {
        // todo implementation
    }
}
