package de.overview.wg.its.mispauth.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.model.UploadState;

import java.util.ArrayList;
import java.util.List;

public class UploadStateAdapter extends RecyclerView.Adapter<UploadStateAdapter.MyViewHolder> {

	private List<UploadState> stateList = new ArrayList<>();

	class MyViewHolder extends RecyclerView.ViewHolder {

		private TextView title, error;
		private ImageView pendingIcon, errorIcon, doneIcon, inProgressIcon;

		private MyViewHolder(View view) {

			super(view);

			title = view.findViewById(R.id.title);
			error = view.findViewById(R.id.state_error_text);

			pendingIcon = view.findViewById(R.id.state_pending);
			errorIcon = view.findViewById(R.id.state_error);
			doneIcon = view.findViewById(R.id.state_done);
			inProgressIcon = view.findViewById(R.id.state_in_progress);
		}

		private void setState(UploadState.State state) {

			error.setVisibility(View.GONE);
			errorIcon.setVisibility(View.GONE);
			pendingIcon.setVisibility(View.GONE);
			doneIcon.setVisibility(View.GONE);
			inProgressIcon.setVisibility(View.GONE);

			switch (state) {
				case PENDING:
					pendingIcon.setVisibility(View.VISIBLE);
					break;

				case IN_PROGRESS:
					inProgressIcon.setVisibility(View.VISIBLE);
					break;

				case DONE:
					doneIcon.setVisibility(View.VISIBLE);
					break;

				case ERROR:
					errorIcon.setVisibility(View.VISIBLE);
					error.setVisibility(View.VISIBLE);
					break;
			}
		}
	}

	public void setStateList(List<UploadState> stateList) {
		this.stateList = stateList;
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public UploadStateAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_upload_state, parent, false);
		return new UploadStateAdapter.MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull UploadStateAdapter.MyViewHolder holder, int position) {
		UploadState state = stateList.get(position);

		holder.title.setText(state.getTitle());
		holder.error.setText(state.getError());
		holder.setState(stateList.get(position).getCurrentState());
	}

	@Override
	public int getItemCount() {
		return stateList.size();
	}

}
