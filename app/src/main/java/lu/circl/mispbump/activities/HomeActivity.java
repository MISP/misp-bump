package lu.circl.mispbump.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.adapters.SyncAdapter;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.models.UploadInformation;
import lu.circl.mispbump.restful_client.MispRestClient;

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
        populateRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            return true;
        }

        if (item.getItemId() == R.id.menu_profile) {
            Intent profile = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(profile);
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

    private void populateRecyclerView() {
        List<UploadInformation> uploadInformationList = preferenceManager.getUploadInformation();

        TextView empty = findViewById(R.id.emtpy);

        if (uploadInformationList == null) {
            empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        empty.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        SyncAdapter syncAdapter = new SyncAdapter(uploadInformationList, HomeActivity.this);
        recyclerView.setAdapter(syncAdapter);
    }
}
