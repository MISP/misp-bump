package de.overview.wg.its.mispbump.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import de.overview.wg.its.mispbump.R;
import de.overview.wg.its.mispbump.model.UploadState;

import java.util.ArrayList;
import java.util.List;

public class UploadStateAdapter extends RecyclerView.Adapter<UploadStateAdapter.MyViewHolder> {

	private UploadState[] states;

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

	public void setStates(UploadState[] states) {
		this.states = states;
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
		UploadState state = states[position];

		holder.title.setText(state.getTitle());
		holder.error.setText(state.getErrorMessage());
		holder.setState(states[position].getCurrentState());
	}

	@Override
	public int getItemCount() {
		return states.length;
	}

}
