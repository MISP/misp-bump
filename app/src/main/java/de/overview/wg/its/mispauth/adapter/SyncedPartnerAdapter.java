package de.overview.wg.its.mispauth.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.model.SyncedPartner;

import java.util.List;

public class SyncedPartnerAdapter extends RecyclerView.Adapter<SyncedPartnerAdapter.MyViewHolder> {

	private List<SyncedPartner> syncedPartnerList;

	public class MyViewHolder extends RecyclerView.ViewHolder {
		public TextView title, dateAdded, url;
		public MyViewHolder(View view) {
			super(view);
			title = view.findViewById(R.id.title);
			dateAdded = view.findViewById(R.id.dateSynced);
			url = view.findViewById(R.id.url);
		}
	}

	public SyncedPartnerAdapter(Context context, List<SyncedPartner> syncedPartnerList) {
		this.syncedPartnerList = syncedPartnerList;
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
	public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		SyncedPartner syncedPartner = syncedPartnerList.get(position);

		holder.title.setText(syncedPartner.getName());
		holder.url.setText(syncedPartner.getUrl());
		holder.dateAdded.setText(syncedPartner.getSyncDate());
	}

	@Override
	public int getItemCount() {
		return syncedPartnerList.size();
	}
}
