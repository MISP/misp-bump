package de.overview.wg.its.misp_authentificator.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import de.overview.wg.its.misp_authentificator.PreferenceManager;
import de.overview.wg.its.misp_authentificator.R;
import de.overview.wg.its.misp_authentificator.adapter.ExtOrgAdapter;

public class MainActivity extends AppCompatActivity {

    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        preferenceManager = PreferenceManager.Instance(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView orgRecyclerView = findViewById(R.id.orgRecyclerView);
        orgRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager orgLayoutManager = new LinearLayoutManager(this);
        orgRecyclerView.setLayoutManager(orgLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(orgRecyclerView.getContext(), 1);
        orgRecyclerView.addItemDecoration(dividerItemDecoration);

        String[] dataSet = {};
        RecyclerView.Adapter orgAdapter = new ExtOrgAdapter(dataSet);
        orgRecyclerView.setAdapter(orgAdapter);

        if(dataSet.length == 0){
            orgRecyclerView.setVisibility(View.GONE);
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        } else {
            orgRecyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_view).setVisibility(View.GONE);
        }

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        final FloatingActionButton fabSync = findViewById(R.id.fab_sync);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fabSync.getVisibility() == View.GONE){
                    fabSync.setVisibility(View.VISIBLE);
                } else {
                    fabSync.setVisibility(View.GONE);
                }
            }
        });

        fabSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSyncActivity();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void startSyncActivity(){
        startActivity(new Intent(this, SyncActivity.class));
    }
}
