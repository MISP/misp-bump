package de.overview.wg.its.mispauth.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.auxiliary.OrganisationDialog;
import de.overview.wg.its.mispauth.model.Organisation;

public class ExtOrgAdapter extends RecyclerView.Adapter<ExtOrgAdapter.ViewHolder> {

	private Context context;
    private Organisation[] dataSet;

    public ExtOrgAdapter(Context context, Organisation[] dataSet) {
    	this.context = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View extOrgView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_ext_org, parent, false);
        return new ViewHolder(extOrgView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.orgTitle.setText(dataSet[position].getName());
        holder.subTitle.setText(dataSet[position].getDescription());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
		        OrganisationDialog d = new OrganisationDialog(context);
		        d.createInfoDialog(dataSet[position]);
	        }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

		RelativeLayout parentLayout;
        TextView orgTitle;
        TextView subTitle;

        public ViewHolder(View v) {
            super(v);
            parentLayout = v.findViewById(R.id.parent_layout);
            orgTitle = v.findViewById(R.id.ext_org_title);
            subTitle = v.findViewById(R.id.ext_org_sub_title);
        }
    }
}
