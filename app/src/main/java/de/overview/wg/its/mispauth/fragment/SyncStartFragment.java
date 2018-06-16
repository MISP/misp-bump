package de.overview.wg.its.mispauth.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.activity.SyncActivity;


public class SyncStartFragment extends Fragment {

	private static final String TAG = "DEBUG";
	private RadioGroup radioGroup;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_sync_start, null);
		radioGroup = v.findViewById(R.id.radioGroup);

		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				((SyncActivity)getActivity()).setPartnerChoice(checkedId % 2);
			}
		});

		return v;
	}
}
