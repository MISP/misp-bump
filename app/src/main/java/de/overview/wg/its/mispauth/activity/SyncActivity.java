package de.overview.wg.its.mispauth.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.android.volley.VolleyError;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.auxiliary.PreferenceManager;
import de.overview.wg.its.mispauth.fragment.ScanQrFragment;
import de.overview.wg.its.mispauth.fragment.ShowQrFragment;
import de.overview.wg.its.mispauth.fragment.SyncStartFragment;
import de.overview.wg.its.mispauth.fragment.UploadFragment;
import de.overview.wg.its.mispauth.model.Organisation;
import de.overview.wg.its.mispauth.network.MispRequest;
import de.overview.wg.its.mispauth.custom_viewpager.ExtendedViewPager;
import org.json.JSONArray;

public class SyncActivity extends AppCompatActivity {

	private PreferenceManager preferenceManager;

	private static final int PAGE_COUNT = 3;
	private ExtendedViewPager viewPager;
	private PagerAdapter pagerAdapter;
	private LinearLayout bottomLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync);

		preferenceManager = PreferenceManager.Instance(this);

		setupViewPager();
	}

	private void setupViewPager() {
		bottomLayout = findViewById(R.id.linearLayout);

		pagerAdapter = new SimplePagerAdapter(getSupportFragmentManager());

		viewPager = findViewById(R.id.viewPager);
		viewPager.setPagingEnabled(false);
		viewPager.setAdapter(pagerAdapter);

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					bottomLayout.setVisibility(View.GONE);
				} else {
					bottomLayout.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			// SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		Button next = findViewById(R.id.nextButton);
		Button back = findViewById(R.id.backButton);

		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
			}
		});

		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
			}
		});
	}

	private class SimplePagerAdapter extends FragmentStatePagerAdapter {

		public SimplePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			switch (position) {
				case 0:
					return new SyncStartFragment(); // start fragment

				case 1:
					return new ScanQrFragment(); // scan fragment

				case 2:
					return new ShowQrFragment(); // show QR fragment

				case 3:
					return new UploadFragment(); // show upload fragment

				default:
					return null; // This should not be happening
			}
		}

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}
	}

	private void uploadOrganisation(Organisation org) {
		MispRequest mispRequest = MispRequest.Instance(this);

		mispRequest.getOrganisations(new MispRequest.OrganisationsCallback() {
			@Override
			public void onResult(JSONArray organisations) {

			}

			@Override
			public void onError(VolleyError volleyError) {

			}
		});
	}
}
