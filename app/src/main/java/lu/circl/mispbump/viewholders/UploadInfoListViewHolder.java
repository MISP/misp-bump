package lu.circl.mispbump.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lu.circl.mispbump.R;

public class UploadInfoListViewHolder extends RecyclerView.ViewHolder {

    public View rootView;
    public ImageView syncStatus;
    public TextView orgName, date;

    public UploadInfoListViewHolder(@NonNull View itemView) {
        super(itemView);

        rootView = itemView;

        orgName = itemView.findViewById(R.id.orgName);
        date = itemView.findViewById(R.id.date);
        syncStatus = itemView.findViewById(R.id.syncStatus);
    }
}
