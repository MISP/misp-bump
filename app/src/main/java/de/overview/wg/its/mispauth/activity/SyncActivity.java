package de.overview.wg.its.mispauth.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.VolleyError;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.auxiliary.PreferenceManager;
import de.overview.wg.its.mispauth.model.Organisation;
import de.overview.wg.its.mispauth.network.MispRequest;
import org.json.JSONObject;

public class SyncActivity extends AppCompatActivity {

	private PreferenceManager preferenceManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync);

		preferenceManager = PreferenceManager.Instance(this);
		uploadOrganisation(preferenceManager.getMyOrganisation());
	}

	private void uploadOrganisation(Organisation org) {
		MispRequest mispRequest = MispRequest.Instance(this);

//		mispRequest.addOrganisation(org, new MispRequest.OrganisationCallback() {
//			@Override
//			public void onResult(JSONObject organisationInformation) {
//
//			}
//
//			@Override
//			public void onError(VolleyError volleyError) {
//
//			}
//		});

		mispRequest.getServers(new MispRequest.ServerCallback() {
			@Override
			public void onResult(JSONObject servers) {

			}

			@Override
			public void onError(VolleyError volleyError) {

			}
		});
	}
}
