package de.overview.wg.its.mispauth.cam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.model.PublicKeyQr;
import de.overview.wg.its.mispauth.model.SyncInformationQr;

public class DialogFactory {

	private Context context;

	private AlertDialog.Builder adb;
	private LayoutInflater inflater;


	public DialogFactory(Context context) {
		this.context = context;
		adb = new AlertDialog.Builder(context);
		inflater = LayoutInflater.from(context);
	}


	public Dialog createKeyDialog(PublicKeyQr pkqr,
	                              DialogInterface.OnClickListener positiveListener,
	                              DialogInterface.OnClickListener negativeListener) {

		View title = inflater.inflate(R.layout.dialog_public_key, null);
		adb.setCustomTitle(title);

		adb.setMessage("\nOrganisation: " + pkqr.getOrganisation() + "\nEmail: " + pkqr.getEmail());

		adb.setPositiveButton(context.getResources().getString(R.string.accept), positiveListener);
		adb.setNegativeButton(context.getResources().getString(R.string.reject), negativeListener);

		adb.setCancelable(false);

		Dialog d = adb.create();
		d.getWindow().setWindowAnimations(R.style.DialogAnimation);
		d.getWindow().setDimAmount(0.8f);
		return d;
	}


	public Dialog createInformationDialog(SyncInformationQr siqr,
	                                      DialogInterface.OnClickListener positiv,
	                                      DialogInterface.OnClickListener negativ) {

		View title = inflater.inflate(R.layout.dialog_sync_info, null);
		adb.setCustomTitle(title);

		View orgView = inflater.inflate(R.layout.view_organisation, null);

		TextView orgTitle = orgView.findViewById(R.id.organisation_title);
		orgTitle.setText(siqr.getOrganisation().getName());

		TextView orgUuid = orgView.findViewById(R.id.organisation_uuid);
		orgUuid.setText(siqr.getOrganisation().getUuid());

		TextView orgDesc = orgView.findViewById(R.id.organisation_description);
		orgDesc.setText(siqr.getOrganisation().getDescription());

		TextView orgNat = orgView.findViewById(R.id.organisation_nationality);
		orgNat.setText(siqr.getOrganisation().getNationality());

		TextView orgSec = orgView.findViewById(R.id.organisation_sector);
		orgSec.setText(siqr.getOrganisation().getSector());

		TextView orgUser = orgView.findViewById(R.id.organisation_user_count);
		orgUser.setText("" + siqr.getOrganisation().getUserCount());

		adb.setView(orgView);

		adb.setPositiveButton(context.getResources().getString(R.string.accept), positiv);
		adb.setNegativeButton(context.getResources().getString(R.string.reject), negativ);

		Dialog d = adb.create();
		d.getWindow().setWindowAnimations(R.style.DialogAnimation);
		d.getWindow().setDimAmount(0.8f);
		return d;
	}


	public Dialog createOverrideDialog(DialogInterface.OnClickListener pos,
	                                   DialogInterface.OnClickListener neg) {

		adb.setTitle(context.getResources().getString(R.string.override_local_data));
		adb.setMessage(context.getResources().getString(R.string.override_local_data_msg));

		adb.setPositiveButton(context.getResources().getString(R.string.override), pos);
		adb.setNegativeButton(android.R.string.cancel, null);

		Dialog d = adb.create();

		d.setCancelable(false);
		d.getWindow().setWindowAnimations(R.style.DialogAnimation);

		return d;
	}


}
