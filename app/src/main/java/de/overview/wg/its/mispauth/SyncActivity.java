package de.overview.wg.its.mispauth;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import com.google.gson.Gson;
import de.overview.wg.its.mispauth.auxiliary.AESSecurity;
import de.overview.wg.its.mispauth.auxiliary.PreferenceManager;
import de.overview.wg.its.mispauth.auxiliary.RandomString;
import de.overview.wg.its.mispauth.auxiliary.TempAuth;
import de.overview.wg.its.mispauth.cam.CameraFragment;
import de.overview.wg.its.mispauth.model.*;
import net.glxn.qrgen.android.QRCode;

public class SyncActivity extends AppCompatActivity implements View.OnClickListener {

	private static final String SCAN_PUB_KEY_FRAG_TAG = "scan_public_key_fragment_tag";
	private static final String SCAN_INFO_FRAG_TAG = "scan_info_fragment_tag";

	private AESSecurity aesSecurity;

	private Fragment currentFragment;
	private String currentFragmentTag;

	// Views for QR code
	private LinearLayout qrBackground;
	private ImageView qrImageView;
	private Button forwardButton;
	private TextView forwardDescription;

	private SyncInformationQr partnerInformation;

	private FragmentManager manager;
	private FragmentTransaction transaction;
	private PreferenceManager preferenceManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_public_key_exchange);

		manager = getSupportFragmentManager();
		preferenceManager = PreferenceManager.Instance(this);

		initializeViews();

		aesSecurity = AESSecurity.getInstance();

		setScanTypeFragment(0);
	}

	private void initializeViews() {

		ImageButton closeButton = findViewById(R.id.close);
		forwardButton = findViewById(R.id.forward);

		closeButton.setOnClickListener(this);

		forwardButton.setOnClickListener(this);
		forwardButton.setEnabled(false);
		forwardDescription = findViewById(R.id.forward_description);

		qrImageView = findViewById(R.id.qr_imageView);
		qrBackground = findViewById(R.id.qr_background);

		setContinueScreenEnabled(false);
	}

	private void setScanTypeFragment(int mode) {

		transaction = manager.beginTransaction();

		User myUser = preferenceManager.getMyUser();
		Organisation myOrg = preferenceManager.getMyOrganisation();

		switch (mode) {

			case 0:

				setContinueScreenEnabled(false);

				PublicKeyQr pkqr = new PublicKeyQr(myOrg.getName(),
						myUser.getEmail(),
						AESSecurity.publicKeyToString(aesSecurity.getPublicKey()));

				setQrContent(pkqr.toJSON().toString(), 0.6f);

				currentFragment = CameraFragment.newInstance(CameraFragment.ScanMode.PUBLIC_KEY);
				currentFragmentTag = SCAN_PUB_KEY_FRAG_TAG;

				break;

			case 1:

				setContinueScreenEnabled(false);

				TempAuth.TMP_AUTH_KEY = new RandomString(40).nextString();

				Server serverForMeOnOtherInstance = new Server();
				serverForMeOnOtherInstance.setAuthkey(TempAuth.TMP_AUTH_KEY);
				serverForMeOnOtherInstance.setName("SyncServer for " + myOrg.getName());
				serverForMeOnOtherInstance.setUrl(preferenceManager.getMyServerUrl());

				SyncInformationQr siqr = new SyncInformationQr(
						preferenceManager.getMyOrganisation(),
						serverForMeOnOtherInstance,
						preferenceManager.getMyUser());

				setQrContent(aesSecurity.encrypt(siqr.toJSON().toString()), 0.9f);

				currentFragment = CameraFragment.newInstance(CameraFragment.ScanMode.INFO);
				currentFragmentTag = SCAN_INFO_FRAG_TAG;

				break;

			default:

				transaction.remove(currentFragment);
				transaction.commit();

				currentFragment = null;

				finish();

				return;
		}

		transaction.replace(R.id.fragment_container, currentFragment, currentFragmentTag);
		transaction.commit();
	}

	private void setQrContent(String content, float qrToScreenRatio) {

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		int width = (int) (size.x * qrToScreenRatio);

		//noinspection SuspiciousNameCombination
		qrImageView.setImageBitmap(QRCode.from(content)
				.withColor(0xFF000000, 0x00FFFFFF)
				.withSize(width, width)
				.bitmap());

		circularReveal(qrBackground, true, 400);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.close:
				finish();
				break;

			case R.id.forward:

				if (currentFragmentTag.equals(SCAN_PUB_KEY_FRAG_TAG)) {
					setScanTypeFragment(1);
				} else if (currentFragmentTag.equals(SCAN_INFO_FRAG_TAG)) {
					startUploadActivity();
				}

				break;

		}
	}

	private void setContinueScreenEnabled(boolean enabled) {
		if (enabled) {
			forwardButton.setVisibility(View.VISIBLE);
			forwardDescription.setVisibility(View.VISIBLE);
		} else {
			forwardButton.setVisibility(View.INVISIBLE);
			forwardDescription.setVisibility(View.INVISIBLE);
		}

		forwardButton.setEnabled(enabled);
	}

	public void onPublicKeyResult(PublicKeyQr pkqr) {
		aesSecurity.setForeignPublicKey(AESSecurity.publicKeyFromString(pkqr.getKey()));

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setContinueScreenEnabled(true);
			}
		});
	}

	public void onSyncInfoResult(SyncInformationQr siqr) {
		partnerInformation = siqr;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setContinueScreenEnabled(true);
			}
		});
	}

	private void startUploadActivity() {

		Intent i = new Intent(this, UploadActivity.class);
		String partnerString = new Gson().toJson(partnerInformation);
		i.putExtra(UploadActivity.PARTNER_INFO_BUNDLE_KEY, partnerString);

		startActivity(i);
		finish();

	}

	private void circularReveal(final View v, final boolean open, final long duration) {

		v.post(new Runnable() {
			@Override
			public void run() {
				int cx = v.getWidth() / 2;
				int cy = v.getHeight() / 2;

				float finalRadius = (float) Math.hypot(cx, cy);

				Animator anim;


				if (open) {
					anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
					v.setVisibility(View.VISIBLE);
				} else {
					anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, finalRadius, 0);
				}

				anim.setInterpolator(new DecelerateInterpolator());

				anim.setDuration(duration);
				anim.addListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animation) {

					}

					@Override
					public void onAnimationEnd(Animator animation) {
						if (!open) {
							v.setVisibility(View.INVISIBLE);
						}
					}

					@Override
					public void onAnimationCancel(Animator animation) {

					}

					@Override
					public void onAnimationRepeat(Animator animation) {

					}
				});
				anim.start();
			}
		});

	}
}
