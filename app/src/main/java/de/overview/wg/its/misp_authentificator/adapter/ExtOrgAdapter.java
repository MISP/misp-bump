package de.overview.wg.its.misp_authentificator.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.overview.wg.its.misp_authentificator.R;

public class ExtOrgAdapter extends RecyclerView.Adapter<ExtOrgAdapter.ViewHolder> {

    private String[] dataSet;

    public ExtOrgAdapter(String[] dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View extOrgView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder_ext_org, parent, false);

        return new ViewHolder(extOrgView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.orgTitle.setText(dataSet[position]);
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView orgTitle;

        public ViewHolder(View v) {
            super(v);
            orgTitle = v.findViewById(R.id.ext_org_title);
        }
    }
}
