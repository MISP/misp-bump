package lu.circl.mispbump.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.models.UploadInformation;

public class SyncAdapter extends RecyclerView.Adapter<SyncAdapter.SyncViewHolder> {

    private List<UploadInformation> uploadInformationList;

    static class SyncViewHolder extends RecyclerView.ViewHolder {
        TextView title, status;
        ImageButton retry, delete;

        SyncViewHolder(View v) {
            super(v);

            title = v.findViewById(R.id.title);
            status = v.findViewById(R.id.syncStatus);

            retry = v.findViewById(R.id.retry_button);
            delete = v.findViewById(R.id.delete_button);
        }
    }

    public SyncAdapter(List<UploadInformation> uploadInformationList) {
        this.uploadInformationList = uploadInformationList;
    }

    @NonNull
    @Override
    public SyncViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_sync, viewGroup, false);
        return new SyncViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SyncViewHolder syncViewHolder, int i) {
        syncViewHolder.title.setText(uploadInformationList.get(i).remote.organisation.name);

        switch (uploadInformationList.get(i).currentSyncStatus) {
            case COMPLETE:
                syncViewHolder.status.setText("Synced");
                syncViewHolder.retry.setVisibility(View.GONE);
                break;
            case FAILURE:
                syncViewHolder.status.setText("Error");
                syncViewHolder.retry.setVisibility(View.VISIBLE);
                break;
            case PENDING:
                syncViewHolder.status.setText("Pending");
                syncViewHolder.retry.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (uploadInformationList == null) {
            return 0;
        }

        return uploadInformationList.size();
    }

}
