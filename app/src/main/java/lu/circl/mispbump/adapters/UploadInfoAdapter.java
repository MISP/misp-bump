package lu.circl.mispbump.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.interfaces.OnRecyclerItemClickListener;
import lu.circl.mispbump.models.UploadInformation;
import lu.circl.mispbump.viewholders.UploadInfoListViewHolder;

public class UploadInfoAdapter extends RecyclerView.Adapter<UploadInfoListViewHolder> {

    private Context context;
    private List<UploadInformation> items;

    private OnRecyclerItemClickListener<UploadInformation> onRecyclerItemClickListener;


    public UploadInfoAdapter(Context context) {
        this.context = context;
    }

    public UploadInfoAdapter(Context context, List<UploadInformation> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public UploadInfoListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_upload_information, viewGroup, false);
        return new UploadInfoListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final UploadInfoListViewHolder holder, int position) {

        final UploadInformation item = items.get(position);

        holder.date.setText(item.getDateString());
        holder.orgName.setText(item.getRemote().organisation.name);

        switch (item.getCurrentSyncStatus()) {
            case COMPLETE:
                ImageViewCompat.setImageTintList(holder.syncStatus, ColorStateList.valueOf(context.getColor(R.color.status_green)));
                holder.syncStatus.setImageResource(R.drawable.ic_check_outline);
                break;
            case FAILURE:
                ImageViewCompat.setImageTintList(holder.syncStatus, ColorStateList.valueOf(context.getColor(R.color.status_red)));
                holder.syncStatus.setImageResource(R.drawable.ic_error_outline);
                break;
            case PENDING:
                ImageViewCompat.setImageTintList(holder.syncStatus, ColorStateList.valueOf(context.getColor(R.color.status_amber)));
                holder.syncStatus.setImageResource(R.drawable.ic_info_outline);
                break;
        }

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecyclerItemClickListener.onClick(view, item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<UploadInformation> items) {
        this.items = items;
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener<UploadInformation> onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

}
