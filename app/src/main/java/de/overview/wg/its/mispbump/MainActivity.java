package de.overview.wg.its.mispbump;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import de.overview.wg.its.mispbump.adapter.SyncedPartnerAdapter;
import de.overview.wg.its.mispbump.auxiliary.PreferenceManager;
import de.overview.wg.its.mispbump.model.SyncedPartner;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity {

	private List<SyncedPartner> syncedPartnerList = new ArrayList<>();
	private SyncedPartnerAdapter syncedPartnerAdapter;
	private TextView emptyPartnerListView;
	private RecyclerView syncedPartnerRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initializeViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

		// Make icon white (compat limitation in xml)
		Drawable drawable = menu.findItem(R.id.menu_item_credential_settings).getIcon();
		drawable = DrawableCompat.wrap(drawable);
		DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorWhite));
		menu.findItem(R.id.menu_item_credential_settings).setIcon(drawable);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		switch (id) {
			case R.id.menu_item_credential_settings:
				startCredentialsActivity();
				return true;

			case R.id.menu_item_delete_local_data:
				createSelectDeleteDialog();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}


	private void initializeViews() {

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(true);

		FloatingActionButton fab = findViewById(R.id.fab_continue_sync_info);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startSyncActivity();
			}
		});

		emptyPartnerListView = findViewById(R.id.empty);
		syncedPartnerRecyclerView = findViewById(R.id.recyclerView);

		syncedPartnerAdapter = new SyncedPartnerAdapter(this, syncedPartnerList);
		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
		syncedPartnerRecyclerView.setLayoutManager(mLayoutManager);
		syncedPartnerRecyclerView.setItemAnimator(new DefaultItemAnimator());
		syncedPartnerRecyclerView.setAdapter(syncedPartnerAdapter);

		refreshSyncedPartnerList();
	}

	private void createSelectDeleteDialog() {

		final PreferenceManager prefs = PreferenceManager.Instance(this);

		AlertDialog.Builder adb = new AlertDialog.Builder(this);

		adb.setTitle("Delete Local Data");
		adb.setMessage("(Checked items will be deleted)");

		@SuppressLint("InflateParams")
		View checkBoxView = getLayoutInflater().inflate(R.layout.dialog_select_delete_data, null);

		final CheckBox checkSyncedPartner = checkBoxView.findViewById(R.id.check_synced_partner_list);
		final CheckBox checkCredentials = checkBoxView.findViewById(R.id.check_credentials);
		final CheckBox checkUserData = checkBoxView.findViewById(R.id.check_user_preferences);

		adb.setView(checkBoxView);

		adb.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (checkSyncedPartner.isChecked()) {
					prefs.clearSyncedInformationPreferences();
				}

				if (checkCredentials.isChecked()) {
					prefs.clearCredentialPreferences();
				}

				if (checkUserData.isChecked()) {
					prefs.clearUserPreferences();
				}
			}
		});

		adb.setNegativeButton(getResources().getString(android.R.string.cancel), null);

		Dialog d = adb.create();
		d.getWindow().setWindowAnimations(R.style.DialogAnimation);
		d.show();
	}

	private void refreshSyncedPartnerList() {
//		syncedPartnerList = PreferenceManager.Instance(this).getSyncedPartnerList();

		SyncedPartner sp = new SyncedPartner("Main Organisation A", "http://192.168.178.200");
        sp.generateTimeStamp();
        syncedPartnerList.add(sp);

		if (syncedPartnerList == null) {
			emptyPartnerListView.setVisibility(View.VISIBLE);
			syncedPartnerRecyclerView.setVisibility(View.GONE);
		} else {
			emptyPartnerListView.setVisibility(View.GONE);
			syncedPartnerAdapter.setSyncedPartnerList(syncedPartnerList);
		}
	}

	private void startSyncActivity() {

		PreferenceManager preferenceManager = PreferenceManager.Instance(this);

		if (preferenceManager.getMyOrganisation() == null || preferenceManager.getMyUser() == null) {

			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(getResources().getString(R.string.missing_local_information_title));
			adb.setMessage(getResources().getString(R.string.missing_local_information_message));

			adb.setPositiveButton(getResources().getString(R.string.enter_credentials), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startCredentialsActivity();
				}
			});

			adb.setNegativeButton(android.R.string.cancel, null);
			adb.show();

		} else {
			startActivity(new Intent(this, QrSyncActivity.class));
		}

	}

	private void startCredentialsActivity() {
        startActivity(new Intent(this, MyOrganisationActivity.class));
	}
}
