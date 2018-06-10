package de.overview.wg.its.mispauth.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.VolleyError;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.model.Organisation;
import de.overview.wg.its.mispauth.network.MispRequest;
import org.json.JSONObject;

public class SyncActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync);
	}

	private void uploadOrganisation(Organisation org) {
		MispRequest mispRequest = MispRequest.Instance(this);

		mispRequest.addOrganisation(org, new MispRequest.OrganisationCallback() {
			@Override
			public void onResult(JSONObject organisationInformation) {

			}

			@Override
			public void onError(VolleyError volleyError) {

			}
		});
	}
}
