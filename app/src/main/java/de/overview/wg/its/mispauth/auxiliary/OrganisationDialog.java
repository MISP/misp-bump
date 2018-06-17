package de.overview.wg.its.mispauth.auxiliary;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.model.Organisation;

public class OrganisationDialog {

	private AlertDialog.Builder dialogBuilder;
	private LayoutInflater inflater;

	public OrganisationDialog(Context context) {
		dialogBuilder = new AlertDialog.Builder(context);
		inflater = ((Activity)context).getLayoutInflater();
	}

	public void createInfoDialog(Organisation org) {

		View dialogContent = inflater.inflate(R.layout.view_holder_organisation, null);
		dialogBuilder.setView(dialogContent);

		TextView title = dialogContent.findViewById(R.id.organisation_title);
		title.setText(org.getName());

		TextView uuid = dialogContent.findViewById(R.id.organisation_uuid);
		uuid.setText(org.getUuid());

		TextView description = dialogContent.findViewById(R.id.organisation_description);
		description.setText(org.getDescription());

		TextView sector = dialogContent.findViewById(R.id.organisation_sector);
		sector.setText(org.getSector());

		TextView nationality = dialogContent.findViewById(R.id.organisation_nationality);
		nationality.setText(org.getNationality());

		TextView userCount = dialogContent.findViewById(R.id.organisation_user_count);
		userCount.setText("" + org.getUserCount());

		dialogBuilder.setPositiveButton("OK", null);
		dialogBuilder.setCancelable(true);
		dialogBuilder.create().show();
	}

	public void createAcceptDialog(Organisation org, final DialogCallback callback) {
		View dialogContent = inflater.inflate(R.layout.view_holder_organisation, null);
		dialogBuilder.setView(dialogContent);

		TextView title = dialogContent.findViewById(R.id.organisation_title);
		title.setText(org.getName());

		TextView uuid = dialogContent.findViewById(R.id.organisation_uuid);
		uuid.setText(org.getUuid());

		TextView description = dialogContent.findViewById(R.id.organisation_description);
		description.setText(org.getDescription());

		TextView sector = dialogContent.findViewById(R.id.organisation_sector);
		sector.setText(org.getSector());

		TextView nationality = dialogContent.findViewById(R.id.organisation_nationality);
		nationality.setText(org.getNationality());

		TextView userCount = dialogContent.findViewById(R.id.organisation_user_count);
		userCount.setText("" + org.getUserCount());

		dialogBuilder.setNegativeButton("REJECT", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.onReject();
			}
		});
		dialogBuilder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.onAccept();
			}
		});

		dialogBuilder.setCancelable(false);
		dialogBuilder.create().show();
	}

	public interface DialogCallback {
		void onAccept();
		void onReject();
	}
}
