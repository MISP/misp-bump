package lu.circl.mispbump.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.adapters.UploadInfoAdapter;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.interfaces.OnRecyclerItemClickListener;
import lu.circl.mispbump.models.UploadInformation;

public class HomeActivity extends AppCompatActivity {

    private View rootView;
    private PreferenceManager preferenceManager;

    private RecyclerView recyclerView;
    private UploadInfoAdapter uploadInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preferenceManager = PreferenceManager.getInstance(this);

        init();
        initRecyclerView();
    }


    private void init() {
        rootView = findViewById(R.id.rootLayout);

        // populate Toolbar (Actionbar)
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
        }

        FloatingActionButton sync_fab = findViewById(R.id.home_fab);
        sync_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SyncActivity.class));
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

        uploadInfoAdapter = new UploadInfoAdapter(HomeActivity.this);

        uploadInfoAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener<UploadInformation>() {
            @Override
            public void onClick(final View v, UploadInformation item) {
                Intent i = new Intent(HomeActivity.this, UploadInformationActivity.class);
                i.putExtra(UploadInformationActivity.EXTRA_UPLOAD_INFO_KEY, new Gson().toJson(item));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeClipRevealAnimation(v.findViewById(R.id.rootLayout), (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight());
                startActivity(i, options.toBundle());
            }
        });

        recyclerView.setAdapter(uploadInfoAdapter);
    }

    private void refreshRecyclerView() {
        List<UploadInformation> uploadInformationList = preferenceManager.getUploadInformation();
        TextView empty = findViewById(R.id.empty);

        // no sync information available
        if (uploadInformationList == null) {
            empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        // sync information available
        empty.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        uploadInfoAdapter.setItems(uploadInformationList);
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshRecyclerView();
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
}
