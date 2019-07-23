package lu.circl.mispbump.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.interfaces.OnRecyclerItemClickListener;
import lu.circl.mispbump.models.UploadInformation;

public class UploadInfoAdapter extends RecyclerView.Adapter<UploadInfoAdapter.ViewHolder> {

    private Context context;
    private List<UploadInformation> items;

    private OnRecyclerItemClickListener<UploadInformation> onRecyclerItemClickListener;
    private OnRecyclerItemClickListener<Integer> onRecyclerPositionClickListener;


    public UploadInfoAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public UploadInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_upload_information, viewGroup, false);
        return new UploadInfoAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final UploadInfoAdapter.ViewHolder holder, final int position) {

        final UploadInformation item = items.get(position);

        holder.date.setText(item.getDateString());
        holder.orgName.setText(item.getRemote().organisation.getName());

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
                holder.syncStatus.setImageResource(R.drawable.ic_pending);
                break;
        }

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecyclerItemClickListener.onClick(view, item);
            }
        });

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecyclerPositionClickListener.onClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void setItems(List<UploadInformation> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    // callbacks

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener<UploadInformation> onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public void setOnRecyclerPositionClickListener(OnRecyclerItemClickListener<Integer> onRecyclerPositionClickListener) {
        this.onRecyclerPositionClickListener = onRecyclerPositionClickListener;
    }

    // viewHolder

    static class ViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        ImageView syncStatus;
        TextView orgName, date;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            orgName = itemView.findViewById(R.id.orgName);
            date = itemView.findViewById(R.id.date);
            syncStatus = itemView.findViewById(R.id.syncStatus);
        }
    }

}
