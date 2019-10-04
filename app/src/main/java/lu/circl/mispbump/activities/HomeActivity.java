package lu.circl.mispbump.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.adapters.SyncInfoAdapter;
import lu.circl.mispbump.auxiliary.MispRestClient;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.interfaces.OnRecyclerItemClickListener;
import lu.circl.mispbump.models.ExchangeInformation;
import lu.circl.mispbump.models.SyncInformation;
import lu.circl.mispbump.models.restModels.MispServer;
import lu.circl.mispbump.models.restModels.MispUser;
import lu.circl.mispbump.models.restModels.Organisation;
import lu.circl.mispbump.models.restModels.Role;
import lu.circl.mispbump.models.restModels.Server;
import lu.circl.mispbump.models.restModels.User;


public class HomeActivity extends AppCompatActivity {

    private List<SyncInformation> syncInformationList;
    private PreferenceManager preferenceManager;
    private MispRestClient restClient;

    private RecyclerView recyclerView;
    private SyncInfoAdapter syncInfoAdapter;
    private TextView emptyRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preferenceManager = PreferenceManager.getInstance(this);
        Pair<String, String> credentials = preferenceManager.getUserCredentials();
        restClient = MispRestClient.getInstance(credentials.first, credentials.second);

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

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            checkUnimportedSyncs();

            syncInfoAdapter.setItems(syncInformationList);
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        syncInfoAdapter = new SyncInfoAdapter(HomeActivity.this);
        syncInfoAdapter.setOnRecyclerPositionClickListener(onRecyclerItemClickListener());
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

    private void checkUnimportedSyncs() {
        restClient.getAllServers(new MispRestClient.AllRawServersCallback() {
            @Override
            public void success(List<MispServer> mispServers) {
                if (mispServers.size() < 1) {
                    return;
                }

                List<SyncInformation> syncInformationList = preferenceManager.getSyncInformationList();

                for (MispServer mispServer : mispServers) {

                    boolean existsOffline = false;

                    for (SyncInformation syncInformation : syncInformationList) {
                        int localServerId = syncInformation.getRemote().getServer().getId();
                        int remoteServerId = mispServer.getServer().getId();

                        if (remoteServerId == localServerId) {
                            existsOffline = true;
                            break;
                        }
                    }

                    if (!existsOffline) {
                        // mispServer is not locally available
                        SyncInformation syncInformation = new SyncInformation();

                        ExchangeInformation local = new ExchangeInformation();
                        local.setOrganisation(preferenceManager.getUserOrganisation().toSyncOrganisation());
                        User syncUser = preferenceManager.getUserInfo().toSyncUser();
                        syncUser.setAuthkey("Could not be recovered");
                        syncUser.setPassword("Could not be recovered");
                        local.setSyncUser(syncUser);
                        local.setServer(new Server(preferenceManager.getUserCredentials().first));

                        ExchangeInformation remote = new ExchangeInformation();
                        remote.setServer(mispServer.getServer());

                        restClient.getOrganisation(mispServer.getRemoteOrganisation().getId(), new MispRestClient.OrganisationCallback() {
                            @Override
                            public void success(Organisation organisation) {
                                remote.setOrganisation(organisation);

                                restClient.getAllUsers(new MispRestClient.AllMispUsersCallback() {
                                    @Override
                                    public void success(List<MispUser> users) {
                                        for (MispUser mispUser : users) {

                                            boolean isSyncUserRole = false;

                                            Role[] roles = preferenceManager.getRoles();

                                            for (Role role : roles) {
                                                if (role.getId().equals(mispUser.getRole().getId())) {
                                                    isSyncUserRole = role.isSyncUserRole();
                                                    break;
                                                }
                                            }

                                            if (mispUser.getOrganisation().getId().equals(organisation.getId()) && isSyncUserRole) {
                                                remote.setSyncUser(mispUser.getUser());

                                                syncInformation.setLocal(local);
                                                syncInformation.setRemote(remote);

                                                preferenceManager.addSyncInformation(syncInformation);
                                                refreshRecyclerView();
                                            }
                                        }
                                    }
                                    @Override
                                    public void failure(String error) {
                                        swipeRefreshLayout.setRefreshing(false);
                                        Snackbar.make(recyclerView, error, Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void failure(String error) {
                                swipeRefreshLayout.setRefreshing(false);
                                Snackbar.make(recyclerView, error, Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(String error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private OnRecyclerItemClickListener<Integer> onRecyclerItemClickListener() {
        return (v, index) -> {
            Intent i = new Intent(HomeActivity.this, SyncInfoDetailActivity.class);
            i.putExtra(SyncInfoDetailActivity.EXTRA_SYNC_INFO_UUID, syncInformationList.get(index).getUuid());

            ActivityOptionsCompat options = ActivityOptionsCompat.makeClipRevealAnimation(v.findViewById(R.id.rootLayout), (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight());
            startActivity(i, options.toBundle());
        };
    }
}
