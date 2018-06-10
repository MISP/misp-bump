package de.overview.wg.its.mispauth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.android.volley.VolleyError;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.adapter.ExtOrgAdapter;
import de.overview.wg.its.mispauth.auxiliary.PreferenceManager;
import de.overview.wg.its.mispauth.auxiliary.ReadableError;
import de.overview.wg.its.mispauth.model.Organisation;
import de.overview.wg.its.mispauth.network.MispRequest;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

	private Organisation[] externalOrganisations;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getExternalOrganisations();
		setUpRecyclerView();

		FloatingActionButton fabAdd = findViewById(R.id.fab_add);
		final FloatingActionButton fabSync = findViewById(R.id.fab_sync);

		fabAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (fabSync.getVisibility() == View.GONE) {
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

	private void setUpRecyclerView() {
		RecyclerView orgRecyclerView = findViewById(R.id.orgRecyclerView);
		orgRecyclerView.setHasFixedSize(true);

		RecyclerView.LayoutManager orgLayoutManager = new LinearLayoutManager(this);
		orgRecyclerView.setLayoutManager(orgLayoutManager);

		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(orgRecyclerView.getContext(), 1);
		orgRecyclerView.addItemDecoration(dividerItemDecoration);

		RecyclerView.Adapter orgAdapter = new ExtOrgAdapter(this, externalOrganisations);
		orgRecyclerView.setAdapter(orgAdapter);

		if (externalOrganisations.length == 0) {
			orgRecyclerView.setVisibility(View.GONE);
			findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
		} else {
			orgRecyclerView.setVisibility(View.VISIBLE);
			findViewById(R.id.empty_view).setVisibility(View.GONE);
		}

		final SwipeRefreshLayout refreshLayout = findViewById(R.id.recycler_refresh);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				// TODO do stuff
				// refreshLayout.setRefreshing(false);
			}
		});
	}

	private void getExternalOrganisations() {
		Organisation a = new Organisation();
		a.setName("Ferrari");
		a.setDescription("Ferrari has nothing to share");
		a.setSector("Fast cars");
		a.setNationality("Italy");
		a.setLocal(true);

		externalOrganisations = new Organisation[]{a};
	}

	private void startSyncActivity() {
		startActivity(new Intent(this, SyncActivity.class));
	}
}
