package de.overview.wg.its.mispauth.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import de.overview.wg.its.mispauth.R;
import net.glxn.qrgen.android.QRCode;

public class ShowQrFragment extends Fragment {

	private ImageView qrImageView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_sync_show, null);

		qrImageView = v.findViewById(R.id.image_view_qr);
		setQr("Hallo hier steht leide nur scheiße, aber ansonsten hat alles geklappt!  (Y)");
		return v;
	}

	public void setQr(String msg) {
		qrImageView.setImageBitmap(QRCode.from(msg).bitmap());
	}
}
