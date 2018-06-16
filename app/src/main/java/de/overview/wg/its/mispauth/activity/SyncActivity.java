package de.overview.wg.its.mispauth.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.auxiliary.OrganisationDialog;
import de.overview.wg.its.mispauth.auxiliary.PreferenceManager;
import de.overview.wg.its.mispauth.fragment.ScanQrFragment;
import de.overview.wg.its.mispauth.fragment.ShowQrFragment;
import de.overview.wg.its.mispauth.fragment.SyncStartFragment;
import de.overview.wg.its.mispauth.fragment.UploadFragment;
import de.overview.wg.its.mispauth.model.Organisation;
import org.json.JSONException;
import org.json.JSONObject;

public class SyncActivity extends AppCompatActivity {

	private static final String TAG = "DEBUG";

	private PreferenceManager preferenceManager;

	private Button prevButton, nextButton;
	private int partnerChoice = -1;
	private String scannedQrString = "";

	private int currentFragmentPosition = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		preferenceManager = PreferenceManager.Instance(this);

		nextButton = findViewById(R.id.nextButton);
		prevButton = findViewById(R.id.backButton);

		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragment(currentFragmentPosition + 1, true);
			}
		});
		prevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragment(currentFragmentPosition - 1, true);
			}
		});

		getFragment(0, false);
	}

	private void getFragment(int position, boolean animate) {

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		if (animate) {
			if (position > currentFragmentPosition) {
				transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
			} else {
				transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
			}
		}

		currentFragmentPosition = position;

		switch (position) {

			case 0:
				prevButton.setEnabled(false);
				nextButton.setEnabled(false);
				transaction.replace(R.id.fragmentContainer, new SyncStartFragment());
				break;

			case 1:

				prevButton.setEnabled(true);

				if (partnerChoice == 1) {

					nextButton.setEnabled(false);
					transaction.replace(R.id.fragmentContainer, new ScanQrFragment(), "FRAGMENT_SCAN");

				} else {

					nextButton.setEnabled(true);
					transaction.replace(R.id.fragmentContainer, new ShowQrFragment(), "FRAGMENT_SHOW");

				}
				break;

			case 2:
				if (partnerChoice == 1) {

					prevButton.setEnabled(true);
					nextButton.setEnabled(true);
					transaction.replace(R.id.fragmentContainer, new ShowQrFragment(), "FRAGMENT_SHOW");

				} else {

					prevButton.setEnabled(true);
					nextButton.setEnabled(false);

					transaction.replace(R.id.fragmentContainer, new ScanQrFragment(), "FRAGMENT_SCAN");
				}
				break;

			case 3:

				nextButton.setText("Finish");
				nextButton.setEnabled(false);

				transaction.replace(R.id.fragmentContainer, new UploadFragment());

				break;

			default:
				break;
		}
		transaction.commit();
	}

	public void setPartnerChoice(int choice) {

		partnerChoice = choice;

		if (choice != -1) {
			nextButton.setEnabled(true);
		} else {
			nextButton.setEnabled(false);
		}
	}

	public void setScannedQr(String qr) {

		final FragmentManager manager = getSupportFragmentManager();
		final ScanQrFragment scanFragment = (ScanQrFragment) manager.findFragmentByTag("FRAGMENT_SCAN");

		scanFragment.setReadQr(false);
		try {
			OrganisationDialog d = new OrganisationDialog(this);
			Organisation o = new Organisation();
			o.fromJSON(new JSONObject(qr));
			d.createDialog(o);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void uploadReady() {
		nextButton.setEnabled(true);
	}
}
