package lu.circl.mispbump.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.adapters.SyncInfoAdapter;
import lu.circl.mispbump.auxiliary.MispRestClient;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.interfaces.OnRecyclerItemClickListener;
import lu.circl.mispbump.models.SyncInformation;
import lu.circl.mispbump.models.restModels.Organisation;
import lu.circl.mispbump.models.restModels.Role;
import lu.circl.mispbump.models.restModels.User;


public class HomeActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    private List<SyncInformation> syncInformationList;
    private RecyclerView recyclerView;
    private SyncInfoAdapter syncInfoAdapter;
    private TextView emptyRecyclerView;

    private OnRecyclerItemClickListener<Integer> onItemClick = (v, index) -> {
        Intent detailActivity = new Intent(HomeActivity.this, SyncInfoDetailActivity.class);
        detailActivity.putExtra(SyncInfoDetailActivity.EXTRA_SYNC_INFO_UUID, syncInformationList.get(index).getUuid());
        startActivity(detailActivity);

//        SyncInformation syncInfo = preferenceManager.getSyncInformation(syncInformationList.get(index).getUuid());
//
//        View dialogContent = getLayoutInflater().inflate(R.layout.dialog_credentials, null);
//
//        MaterialPreferenceText url = dialogContent.findViewById(R.id.url);
//        url.setSubtitle(syncInfo.getRemote().getServer().getUrl());
//
//        MaterialPreferenceText email = dialogContent.findViewById(R.id.email);
//        email.setSubtitle(syncInfo.getLocal().getSyncUser().getEmail());
//
//        MaterialPasswordView authkey = dialogContent.findViewById(R.id.authkey);
//        authkey.setPassword(syncInfo.getLocal().getSyncUser().getAuthkey());
//
//        MaterialPasswordView password = dialogContent.findViewById(R.id.password);
//        password.setPassword(syncInfo.getLocal().getSyncUser().getPassword());
//
//        new MaterialAlertDialogBuilder(HomeActivity.this)
//                .setTitle("Credentials")
//                .setMessage("These credentials are valid for the sync user on your partners MISP instance")
//                .setView(dialogContent)
//                .setPositiveButton(android.R.string.ok, null)
//                .show();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preferenceManager = PreferenceManager.getInstance(this);

        initViews();
        initRecyclerView();
        checkRequiredInformationAvailable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(HomeActivity.this, PreferenceActivity.class));
            return true;
        }

        if (item.getItemId() == R.id.menu_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshRecyclerView();
    }


    private void initViews() {
        emptyRecyclerView = findViewById(R.id.empty);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        FloatingActionButton syncFab = findViewById(R.id.home_fab);
        syncFab.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ExchangeActivity.class)));
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        syncInfoAdapter = new SyncInfoAdapter(HomeActivity.this);
        syncInfoAdapter.setOnRecyclerPositionClickListener(onItemClick);
        recyclerView.setAdapter(syncInfoAdapter);
    }

    private void refreshRecyclerView() {
        syncInformationList = preferenceManager.getSyncInformationList();

        if (syncInformationList.isEmpty()) {
            emptyRecyclerView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyRecyclerView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            syncInfoAdapter.setItems(syncInformationList);
        }
    }

    private void checkRequiredInformationAvailable() {
        if (preferenceManager.getRoles() == null || preferenceManager.getUserInfo() == null || preferenceManager.getUserOrganisation() == null) {

            Pair<String, String> credentials = preferenceManager.getUserCredentials();
            MispRestClient client = MispRestClient.getInstance(credentials.first, credentials.second);

            // get roles
            client.getRoles(new MispRestClient.AllRolesCallback() {
                @Override
                public void success(Role[] roles) {
                    preferenceManager.setRoles(roles);
                }

                @Override
                public void failure(String error) {
                    Snackbar.make(recyclerView, error, Snackbar.LENGTH_LONG).show();
                }
            });

            // get user and organisation
            client.getMyUser(new MispRestClient.UserCallback() {
                @Override
                public void success(User user) {
                    preferenceManager.setMyUser(user);

                    client.getOrganisation(user.getOrgId(), new MispRestClient.OrganisationCallback() {
                        @Override
                        public void success(Organisation organisation) {
                            preferenceManager.setMyOrganisation(organisation);
                        }
                        @Override
                        public void failure(String error) {
                            Snackbar.make(recyclerView, error, Snackbar.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void failure(String error) {
                    Snackbar.make(recyclerView, error, Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
