package de.overview.wg.its.mispauth;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.VolleyError;
import de.overview.wg.its.mispauth.auxiliary.PreferenceManager;
import de.overview.wg.its.mispauth.auxiliary.ReadableError;
import de.overview.wg.its.mispauth.cam.DialogFactory;
import de.overview.wg.its.mispauth.model.Organisation;
import de.overview.wg.its.mispauth.model.User;
import de.overview.wg.its.mispauth.network.MispRequest;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("ConstantConditions")
@SuppressLint("SetTextI18n")
public class CredentialsActivity extends AppCompatActivity implements View.OnClickListener {

	private boolean changesMade;
	private boolean saveAuthkey, saveAuthkeyPrefSet;

	private TextInputLayout urlLayout, apiLayout;
	private TextView emptyView;
	private ViewGroup organisationView;
	private ProgressBar progressBar;

	private Organisation myOrganisation;
	private User myUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credentials);

		initializeViews();
		loadPreferences();
		addSaveChangesListener();
	}

	private void initializeViews() {

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		progressBar = findViewById(R.id.progressBar);

		urlLayout = findViewById(R.id.input_layout_server_url);
		apiLayout = findViewById(R.id.input_layout_api_key);

		FloatingActionButton fab = findViewById(R.id.fab_download_own_org_info);
		fab.setOnClickListener(this);

		emptyView = findViewById(R.id.empty);
		organisationView = findViewById(R.id.myOrganisationView);

	}

	private void loadPreferences() {

		PreferenceManager preferenceManager = PreferenceManager.Instance(this);

		saveAuthkeyPrefSet = preferenceManager.saveAuthkeyEnabledExists();
		saveAuthkey = preferenceManager.saveAuthkeyEnabled();

		urlLayout.getEditText().setText(preferenceManager.getMyServerUrl());
		apiLayout.getEditText().setText(preferenceManager.getMyServerApiKey());

		myOrganisation = preferenceManager.getMyOrganisation();

		if (myOrganisation == null) {

			emptyView.setVisibility(View.VISIBLE);
			organisationView.setVisibility(View.GONE);

		} else {

			emptyView.setVisibility(View.GONE);
			organisationView.setVisibility(View.VISIBLE);

			visualizeOrganisation();

		}
	}

	private void savePreferences() {

		PreferenceManager preferenceManager = PreferenceManager.Instance(this);

		preferenceManager.setMyServerUrl(urlLayout.getEditText().getText().toString());
		preferenceManager.setSaveAuthkeyEnabled(saveAuthkey);

		if (saveAuthkey) {
			preferenceManager.setMyServerApiKey(apiLayout.getEditText().getText().toString());
		} else {
			preferenceManager.setMyServerApiKey("");
		}

		if (myUser != null) {
			preferenceManager.setMyUser(myUser);
		}

		if (myOrganisation != null) {
			preferenceManager.setMyOrganisation(myOrganisation);
		}

		changesMade = false;
	}

	private void addSaveChangesListener() {
		urlLayout.getEditText().addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				changesMade = true;
			}
		});

		apiLayout.getEditText().addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				changesMade = true;
			}
		});
	}

	private void tryDownloadOrgInfo() {

		if (myOrganisation != null) {

			DialogInterface.OnClickListener pos = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					downloadOrgInfo();
				}
			};

			new DialogFactory(this).createOverrideDialog(pos, null).show();

		} else {

			downloadOrgInfo();

		}
	}

	private boolean validCredentials() {
		boolean inputError = false;
		String url = urlLayout.getEditText().getText().toString();
		String auth = apiLayout.getEditText().getText().toString();

		if (url.equals("")) {
			urlLayout.setError("Required");
			inputError = true;
		}

		if (auth.equals("")) {
			apiLayout.setError("Required");
			inputError = true;
		}

		if (inputError) {
			return false;
		}

		urlLayout.setError(null);
		apiLayout.setError(null);

		return true;
	}

	private void downloadOrgInfo() {

		if (!validCredentials()) {
			return;
		}

		apiLayout.clearFocus();
		urlLayout.clearFocus();

		progressBar.setVisibility(View.VISIBLE);
		emptyView.setVisibility(View.GONE);

		final MispRequest mispRequest = MispRequest.Instance(this);
		mispRequest.setServerCredentials(urlLayout.getEditText().getText().toString(), apiLayout.getEditText().getText().toString());

		mispRequest.getMyUser(new MispRequest.UserCallback() {
			@Override
			public void onResult(JSONObject jsonUser) {
				try {
					myUser = new User(jsonUser);
				} catch (JSONException e) {
					makeSnackBar("Could not interpret user format");
					return;
				}

				mispRequest.getOrganisation(myUser.getId(), new MispRequest.OrganisationCallback() {
					@Override
					public void onResult(JSONObject organisationInformation) {
						try {
							myOrganisation = new Organisation(organisationInformation);
							changesMade = true;
						} catch (JSONException e) {
							makeSnackBar("Could not interpret organisation format");
							return;
						}

						organisationView.setVisibility(View.VISIBLE);
						emptyView.setVisibility(View.GONE);

						progressBar.setVisibility(View.GONE);
						visualizeOrganisation();
					}

					@Override
					public void onError(VolleyError volleyError) {
						makeSnackBar(ReadableError.toReadable(volleyError));
						progressBar.setVisibility(View.GONE);
						organisationView.setVisibility(View.GONE);
						emptyView.setVisibility(View.VISIBLE);
					}
				});
			}

			@Override
			public void onError(VolleyError volleyError) {
				makeSnackBar(ReadableError.toReadable(volleyError));
				progressBar.setVisibility(View.GONE);
				organisationView.setVisibility(View.GONE);
				emptyView.setVisibility(View.VISIBLE);
			}
		});

	}

	private void visualizeOrganisation() {

		TextView title = organisationView.findViewById(R.id.organisation_title);
		title.setText(myOrganisation.getName());

		TextView uuid = organisationView.findViewById(R.id.organisation_uuid);
		uuid.setText(myOrganisation.getUuid());

		TextView description = organisationView.findViewById(R.id.organisation_description);
		description.setText(myOrganisation.getDescription());

		TextView nationality = organisationView.findViewById(R.id.organisation_nationality);
		nationality.setText(myOrganisation.getNationality());

		TextView sector = findViewById(R.id.organisation_sector);
		sector.setText(myOrganisation.getSector());

		TextView users = findViewById(R.id.organisation_user_count);

		users.setText("" + myOrganisation.getUserCount());

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
			case R.id.fab_download_own_org_info:
				tryDownloadOrgInfo();
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_credentials, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		switch (id) {
			case android.R.id.home:
				exitSafely();
				return true;

//			case R.id.menu_item_deleteData:
//				DialogInterface.OnClickListener pos = new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						PreferenceManager.Instance(getApplicationContext()).clearCredentialPreferences();
//						urlLayout.getEditText().setText("");
//						apiLayout.getEditText().setText("");
//						myOrganisation = null;
//						myUser = null;
//						emptyView.setVisibility(View.VISIBLE);
//						organisationView.setVisibility(View.GONE);
//					}
//				};
//
//				new DialogFactory(this).createDeleteDialog(pos, null).show();
//
//				break;

			case R.id.load_config:

				// MOTOROLA
				if (Build.VERSION.SDK_INT <= 25) {
					urlLayout.getEditText().setText("http://192.168.178.200");
					apiLayout.getEditText().setText("dcfgDrNy3SyASmo9WRqyJ4LhsN1xWJ7phfTjklFa");
				} else {
					urlLayout.getEditText().setText("http://192.168.178.201");
					apiLayout.getEditText().setText("5BGhMzdHIWvaxyrTUUVNk2NflDPzXJRZQvOa3CE2");
				}
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		exitSafely();
	}

	private void exitSafely() {

		if (changesMade || !saveAuthkeyPrefSet) {
			saveDialog(new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					savePreferences();
					finish();
				}
			}, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
		} else {
			finish();
		}
	}

	private void makeSnackBar(String message) {
		Snackbar.make(findViewById(R.id.coordinator), message, Snackbar.LENGTH_LONG).show();
	}

	private void saveDialog(DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);

		adb.setTitle(getResources().getString(R.string.unsaved_changes));
		adb.setMessage("\n" + getResources().getString(R.string.save_changes));

		@SuppressLint("InflateParams")
		View checkBoxView = getLayoutInflater().inflate(R.layout.dialog_save_authkey, null);
		CheckBox c = checkBoxView.findViewById(R.id.checkbox);
		c.setChecked(saveAuthkey);

		c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				saveAuthkey = isChecked;
			}
		});

		adb.setView(checkBoxView);

		adb.setPositiveButton(getResources().getString(R.string.save), positive);
		adb.setNegativeButton(getResources().getString(R.string.discard), negative);

		Dialog d = adb.create();
		d.setCancelable(false);
		d.getWindow().setWindowAnimations(R.style.DialogAnimation);
		d.show();
	}

}
