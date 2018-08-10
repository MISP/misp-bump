package de.overview.wg.its.mispbump.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.overview.wg.its.mispbump.R;
import de.overview.wg.its.mispbump.model.SyncedPartner;

import java.util.List;

public class SyncedPartnerAdapter extends RecyclerView.Adapter<SyncedPartnerAdapter.MyViewHolder> {

	private List<SyncedPartner> syncedPartnerList;
	private Context context;

	class MyViewHolder extends RecyclerView.ViewHolder {

	    CardView cardView;
	    TextView title, dateAdded, url;

		MyViewHolder(View view) {
			super(view);

			cardView = view.findViewById(R.id.card_synced_org);
			title = view.findViewById(R.id.title);
			dateAdded = view.findViewById(R.id.dateSynced);
			url = view.findViewById(R.id.url);
		}
    }

	public SyncedPartnerAdapter(Context context, List<SyncedPartner> syncedPartnerList) {
		this.syncedPartnerList = syncedPartnerList;
		this.context = context;
	}

	public void setSyncedPartnerList(List<SyncedPartner> syncedPartnerList) {
		this.syncedPartnerList = syncedPartnerList;
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_synced_organisation, parent, false);
		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
		final SyncedPartner syncedPartner = syncedPartnerList.get(position);

		holder.title.setText(syncedPartner.getName());
		holder.url.setText(syncedPartner.getUrl());
		holder.dateAdded.setText(syncedPartner.getSyncDate());

		holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder adb = new AlertDialog.Builder(context);

                adb.setTitle(context.getString(R.string.dialog_open_browser_title));
                adb.setMessage(context.getString(R.string.dialog_open_in_browser_msg, syncedPartner.getUrl()));

                adb.setPositiveButton(context.getString(R.string.open), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(syncedPartner.getUrl()));
                        context.startActivity(browser);
                    }
                });

                adb.setNegativeButton(context.getString(android.R.string.cancel), null);
                adb.create().show();
            }
        });
	}

	@Override
	public int getItemCount() {
		return syncedPartnerList.size();
	}
}
