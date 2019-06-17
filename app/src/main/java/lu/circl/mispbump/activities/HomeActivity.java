package lu.circl.mispbump.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.adapters.SyncAdapter;
import lu.circl.mispbump.auxiliary.DialogManager;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.interfaces.IOnItemClickListener;
import lu.circl.mispbump.models.UploadInformation;

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "Home";

    private CoordinatorLayout layout;
    private RecyclerView recyclerView;

    private PreferenceManager preferenceManager;

    private View.OnClickListener onFabClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent sync = new Intent(HomeActivity.this, SyncActivity.class);
            startActivity(sync);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preferenceManager = PreferenceManager.getInstance(this);

        initializeViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeRecyclerView();
        refreshSyncInformation();
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

        // invoke superclass to handle unrecognized item (eg. homeAsUp)
        return super.onOptionsItemSelected(item);
    }

    private void initializeViews() {
        layout = findViewById(R.id.rootLayout);

        // populate Toolbar (Actionbar)
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton sync_fab = findViewById(R.id.home_fab);
        sync_fab.setOnClickListener(onFabClicked);
    }

    private void refreshSyncInformation () {
        List<UploadInformation> uploadInformationList = preferenceManager.getUploadInformation();
        TextView empty = findViewById(R.id.emtpy);

        // no sync information available
        if (uploadInformationList == null) {
            empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        // sync information available
        empty.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        SyncAdapter adapter = (SyncAdapter) recyclerView.getAdapter();
        assert adapter != null;
        adapter.setUploadInformationList(uploadInformationList);
    }

    private void initializeRecyclerView() {
        SyncAdapter syncAdapter = new SyncAdapter(HomeActivity.this);
        syncAdapter.setOnDeleteClickListener(new IOnItemClickListener<UploadInformation>() {
            @Override
            public void onItemClick(final UploadInformation clickedObject) {
                DialogManager.deleteSyncInformationDialog(HomeActivity.this, new DialogManager.IDialogFeedback() {
                    @Override
                    public void positive() {
                        boolean status = preferenceManager.removeUploadInformation(clickedObject.getId());

                        if (status) {
                            Snackbar.make(layout, "Successfully deleted sync information", Snackbar.LENGTH_LONG).show();
                            refreshSyncInformation();
                        } else {
                            Snackbar.make(layout, "Failed to delete sync information", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void negative() { }
                });
            }
        });

        syncAdapter.setOnRetryClickListener(new IOnItemClickListener<UploadInformation>() {
            @Override
            public void onItemClick(UploadInformation clickedObject) {
                Intent upload = new Intent(HomeActivity.this, UploadActivity.class);
                upload.putExtra(UploadActivity.EXTRA_UPLOAD_INFO, new Gson().toJson(clickedObject));
                startActivity(upload);
            }
        });

        recyclerView.setAdapter(syncAdapter);
    }
}
