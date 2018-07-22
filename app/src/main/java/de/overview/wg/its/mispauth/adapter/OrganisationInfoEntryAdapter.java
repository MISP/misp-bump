package de.overview.wg.its.mispauth.adapter;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.model.StringPair;

import java.util.*;

public class OrganisationInfoEntryAdapter extends RecyclerView.Adapter<OrganisationInfoEntryAdapter.MyViewHolder> {

	private List<StringPair> list = new ArrayList<>();

	class MyViewHolder extends RecyclerView.ViewHolder {

		TextView title, value;

		private MyViewHolder(View view) {
			super(view);

			this.title = view.findViewById(R.id.title);
			this.value = view.findViewById(R.id.value);
		}
	}

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_org_info_entry, parent, false);
		return new OrganisationInfoEntryAdapter.MyViewHolder(row);
	}

	@Override
	public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		holder.title.setText(list.get(position).key);
		holder.value.setText(list.get(position).value);
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	public void setList(List<StringPair> list) {
		this.list = list;
		notifyDataSetChanged();
	}
}
