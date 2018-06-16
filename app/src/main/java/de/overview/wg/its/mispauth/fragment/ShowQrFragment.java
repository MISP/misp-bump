package de.overview.wg.its.mispauth.fragment;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.auxiliary.PreferenceManager;
import net.glxn.qrgen.android.QRCode;

public class ShowQrFragment extends Fragment {

	private ImageView qrImageView;
	private int screenWidth, screenHeight;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_sync_show, null);

//		DisplayMetrics metrics = new DisplayMetrics();
//		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//
//		screenWidth = (int)(metrics.widthPixels * metrics.density);
//		screenHeight = (int)(metrics.heightPixels * metrics.density);

//		Display display = getActivity().getWindowManager().getDefaultDisplay();
//		Point size = new Point();
//		display.getSize(size);
//		screenWidth = size.x;
//		screenHeight = size.y;

		screenHeight = getResources().getDisplayMetrics().heightPixels;
		screenWidth = getResources().getDisplayMetrics().widthPixels;

		qrImageView = v.findViewById(R.id.image_view_qr);

		PreferenceManager preferenceManager = PreferenceManager.Instance(getActivity());
		setQr(preferenceManager.getMyOrganisation().toJSON().toString());

		return v;
	}

	private void setQr(String msg) {
		qrImageView.setImageBitmap(QRCode.from(msg)
				.withSize(screenHeight, screenHeight)
				.bitmap());
	}
}
