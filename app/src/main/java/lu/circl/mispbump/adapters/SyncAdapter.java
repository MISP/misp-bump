package lu.circl.mispbump.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.custom_views.MaterialPreferenceText;
import lu.circl.mispbump.interfaces.IOnItemClickListener;
import lu.circl.mispbump.models.UploadInformation;

public class SyncAdapter extends RecyclerView.Adapter<SyncAdapter.SyncViewHolder> {

    private Context context;
    private List<UploadInformation> uploadInformationList;
    private IOnItemClickListener<UploadInformation> deleteListener, retryListener;

    static class SyncViewHolder extends RecyclerView.ViewHolder {
        MaterialPreferenceText baseUrl, email, password, authkey;
        TextView orgName, date;
        ImageView syncStatus;
        ImageButton retry, delete;

        ConstraintLayout collapsedContent, expandedContent;

        SyncViewHolder(View v) {
            super(v);

            expandedContent = v.findViewById(R.id.expandedContent);
            expandedContent.setVisibility(View.GONE);

            collapsedContent = v.findViewById(R.id.collapsedContent);
            collapsedContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandedContent.getVisibility() == View.GONE) {
                        expandedContent.setVisibility(View.VISIBLE);
                    } else {
                        expandedContent.setVisibility(View.GONE);
                    }
                }
            });

            orgName = v.findViewById(R.id.orgName);
            date = v.findViewById(R.id.date);

            baseUrl = v.findViewById(R.id.baseUrl);
            email = v.findViewById(R.id.email);
            password = v.findViewById(R.id.password);
            authkey = v.findViewById(R.id.authkey);

            syncStatus = v.findViewById(R.id.syncStatus);

            retry = v.findViewById(R.id.retryButton);
            delete = v.findViewById(R.id.deleteButton);
        }

        void bindDeleteListener(final UploadInformation item, final IOnItemClickListener<UploadInformation> listener) {
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        void bindRetryListener(final UploadInformation item, final IOnItemClickListener<UploadInformation> listener) {
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public SyncAdapter(Context context) {
        this.context = context;
    }

    public void setUploadInformationList(List<UploadInformation> uploadInformationList) {
        this.uploadInformationList = uploadInformationList;
        notifyDataSetChanged();
    }

    public void setOnDeleteClickListener(IOnItemClickListener<UploadInformation> listener) {
        deleteListener = listener;
    }

    public void setOnRetryClickListener(IOnItemClickListener<UploadInformation> listener) {
        retryListener = listener;
    }

    @NonNull
    @Override
    public SyncViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewholder_sync, viewGroup, false);
        return new SyncViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SyncViewHolder syncViewHolder, int i) {

        syncViewHolder.date.setText(uploadInformationList.get(i).getDateString());
        syncViewHolder.orgName.setText(uploadInformationList.get(i).getRemote().organisation.name);

        syncViewHolder.baseUrl.setSubText(uploadInformationList.get(i).getRemote().baseUrl);
        syncViewHolder.email.setSubText(uploadInformationList.get(i).getLocal().syncUserEmail);
        syncViewHolder.password.setSubText(uploadInformationList.get(i).getLocal().syncUserPassword);
        syncViewHolder.authkey.setSubText(uploadInformationList.get(i).getLocal().syncUserAuthkey);


        switch (uploadInformationList.get(i).getCurrentSyncStatus()) {
            case COMPLETE:
                ImageViewCompat.setImageTintList(syncViewHolder.syncStatus, ColorStateList.valueOf(context.getColor(R.color.status_green)));
                syncViewHolder.syncStatus.setImageResource(R.drawable.ic_check);
                syncViewHolder.retry.setVisibility(View.GONE);
                break;
            case FAILURE:
                ImageViewCompat.setImageTintList(syncViewHolder.syncStatus, ColorStateList.valueOf(context.getColor(R.color.status_red)));
                syncViewHolder.syncStatus.setImageResource(R.drawable.ic_error_outline);
                syncViewHolder.retry.setVisibility(View.VISIBLE);
                break;
            case PENDING:
                ImageViewCompat.setImageTintList(syncViewHolder.syncStatus, ColorStateList.valueOf(context.getColor(R.color.status_amber)));
                syncViewHolder.syncStatus.setImageResource(R.drawable.ic_info_outline);
                syncViewHolder.retry.setVisibility(View.VISIBLE);
                break;
        }

        syncViewHolder.bindDeleteListener(uploadInformationList.get(i), deleteListener);
        syncViewHolder.bindRetryListener(uploadInformationList.get(i), retryListener);
    }

    @Override
    public int getItemCount() {
        if (uploadInformationList == null) {
            return 0;
        }

        return uploadInformationList.size();
    }

}
