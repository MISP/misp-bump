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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import lu.circl.mispbump.R;
import lu.circl.mispbump.interfaces.OnRecyclerItemClickListener;
import lu.circl.mispbump.models.SyncInformation;


public class SyncInfoAdapter extends RecyclerView.Adapter<SyncInfoAdapter.ViewHolder> {

    private Context context;
    private List<SyncInformation> items;
    private OnRecyclerItemClickListener<Integer> onRecyclerPositionClickListener;


    public SyncInfoAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public SyncInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_upload_information, viewGroup, false);
        return new SyncInfoAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SyncInfoAdapter.ViewHolder holder, final int position) {
        final SyncInformation item = items.get(position);

        SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM", Locale.getDefault());
        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd", Locale.getDefault());

        holder.dateMonth.setText(monthFormatter.format(item.getSyncDate()));
        holder.dateDay.setText(dayFormatter.format(item.getSyncDate()));

        holder.orgName.setText(item.getRemote().getOrganisation().getName());

        if (item.isSyncedWithRemote()) {
            ImageViewCompat.setImageTintList(holder.syncStatus, ColorStateList.valueOf(context.getColor(R.color.status_green)));
            holder.syncStatus.setImageResource(R.drawable.ic_check_outline);
        } else {
            ImageViewCompat.setImageTintList(holder.syncStatus, ColorStateList.valueOf(context.getColor(R.color.status_amber)));
            holder.syncStatus.setImageResource(R.drawable.ic_error_outline);
        }

//        switch (item.getCurrentSyncStatus()) {
//            case COMPLETE:
//                ImageViewCompat.setImageTintList(holder.syncStatus, ColorStateList.valueOf(context.getColor(R.color.status_green)));
//                holder.syncStatus.setImageResource(R.drawable.ic_check_outline);
//                break;
//            case FAILURE:
//                ImageViewCompat.setImageTintList(holder.syncStatus, ColorStateList.valueOf(context.getColor(R.color.status_red)));
//                holder.syncStatus.setImageResource(R.drawable.ic_error_outline);
//                break;
//            case PENDING:
//                ImageViewCompat.setImageTintList(holder.syncStatus, ColorStateList.valueOf(context.getColor(R.color.status_amber)));
//                holder.syncStatus.setImageResource(R.drawable.ic_pending);
//                break;
//        }

        holder.rootView.setOnClickListener(view -> onRecyclerPositionClickListener.onClick(view, position));
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }

        return items.size();
    }


    public void setItems(List<SyncInformation> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnRecyclerPositionClickListener(OnRecyclerItemClickListener<Integer> onRecyclerPositionClickListener) {
        this.onRecyclerPositionClickListener = onRecyclerPositionClickListener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        ImageView syncStatus;
        TextView orgName, dateMonth, dateDay;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            rootView = itemView;

            orgName = itemView.findViewById(R.id.orgName);

            dateMonth = itemView.findViewById(R.id.date_month);
            dateDay = itemView.findViewById(R.id.date_day);

            syncStatus = itemView.findViewById(R.id.syncStatus);
        }
    }
}
