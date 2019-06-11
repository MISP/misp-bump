package lu.circl.mispbump.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.activities.HomeActivity;
import lu.circl.mispbump.activities.ProfileActivity;
import lu.circl.mispbump.models.UploadInformation;

public class SyncAdapter extends RecyclerView.Adapter<SyncAdapter.SyncViewHolder> {

    private Context context;
    private List<UploadInformation> uploadInformationList;

    static class SyncViewHolder extends RecyclerView.ViewHolder {
        TextView orgName, date;
        ImageView syncStatus;
        ImageButton retry, delete;

        SyncViewHolder(View v, final Context context) {
            super(v);

            orgName = v.findViewById(R.id.orgName);
            date = v.findViewById(R.id.date);

            syncStatus = v.findViewById(R.id.syncStatus);

            retry = v.findViewById(R.id.retryButton);
            delete = v.findViewById(R.id.deleteButton);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // context.startActivity(new Intent(context, ProfileActivity.class));
                }
            });
        }
    }

    public SyncAdapter(List<UploadInformation> uploadInformationList, Context context) {
        this.uploadInformationList = uploadInformationList;
        this.context = context;
    }

    @NonNull
    @Override
    public SyncViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_sync_card, viewGroup, false);
        return new SyncViewHolder(v, context);
    }

    @Override
    public void onBindViewHolder(@NonNull SyncViewHolder syncViewHolder, int i) {

        syncViewHolder.orgName.setText(uploadInformationList.get(i).getRemote().organisation.name);
        syncViewHolder.date.setText(uploadInformationList.get(i).getDateString());

        switch (uploadInformationList.get(i).getCurrentSyncStatus()) {
            case COMPLETE:
                syncViewHolder.syncStatus.setBackgroundColor(context.getColor(R.color.status_green));
                syncViewHolder.syncStatus.setImageResource(R.drawable.ic_check);
                syncViewHolder.retry.setVisibility(View.GONE);
                break;
            case FAILURE:
                syncViewHolder.syncStatus.setBackgroundColor(context.getColor(R.color.status_red));
                syncViewHolder.syncStatus.setImageResource(R.drawable.ic_error_outline);
                syncViewHolder.retry.setVisibility(View.VISIBLE);
                break;
            case PENDING:
                syncViewHolder.syncStatus.setBackgroundColor(context.getColor(R.color.status_green));
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
