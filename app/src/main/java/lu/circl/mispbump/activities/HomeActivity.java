package lu.circl.mispbump.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.adapters.UploadInfoAdapter;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.interfaces.OnRecyclerItemClickListener;
import lu.circl.mispbump.models.UploadInformation;


public class HomeActivity extends AppCompatActivity {

    private List<UploadInformation> uploadInformationList;
    private PreferenceManager preferenceManager;
    private RecyclerView recyclerView;
    private UploadInfoAdapter uploadInfoAdapter;
    private TextView emptyRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        preferenceManager = PreferenceManager.getInstance(this);

        initViews();
        initRecyclerView();
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

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("DEBUG", "onResume()");

        refreshRecyclerView();
    }


    private void initViews() {
        emptyRecyclerView = findViewById(R.id.empty);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        FloatingActionButton syncFab = findViewById(R.id.home_fab);
        syncFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ExchangeActivity.class));
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        uploadInfoAdapter = new UploadInfoAdapter(HomeActivity.this);
        uploadInfoAdapter.setOnRecyclerPositionClickListener(onRecyclerItemClickListener());
        recyclerView.setAdapter(uploadInfoAdapter);
    }

    private void refreshRecyclerView() {
        uploadInformationList = preferenceManager.getUploadInformationList();

        if (uploadInformationList.isEmpty()) {
            emptyRecyclerView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyRecyclerView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            uploadInfoAdapter.setItems(uploadInformationList);
        }
    }


    private OnRecyclerItemClickListener<Integer> onRecyclerItemClickListener() {
        return new OnRecyclerItemClickListener<Integer>() {
            @Override
            public void onClick(View v, Integer index) {
                Intent i = new Intent(HomeActivity.this, UploadInfoActivity.class);
                i.putExtra(UploadInfoActivity.EXTRA_UPLOAD_INFO_UUID, uploadInformationList.get(index).getUuid());

                ActivityOptionsCompat options = ActivityOptionsCompat.makeClipRevealAnimation(v.findViewById(R.id.rootLayout), (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight());
                startActivity(i, options.toBundle());
            }
        };
    }
}
