package de.overview.wg.its.mispbump.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import de.overview.wg.its.mispbump.model.StringPair;
import de.overview.wg.its.mispbump.R;

import java.util.ArrayList;
import java.util.List;

public class OrganisationInfoEntryAdapter extends RecyclerView.Adapter<OrganisationInfoEntryAdapter.MyViewHolder> {

    private Context context;
	private List<StringPair> list = new ArrayList<>();

	class MyViewHolder extends RecyclerView.ViewHolder {

	    View container;
		TextView title, value;

		private MyViewHolder(View view) {
			super(view);

			this.title = view.findViewById(R.id.title);
			this.value = view.findViewById(R.id.value);
			this.container = view.findViewById(R.id.container);
		}
	}

	public OrganisationInfoEntryAdapter(Context context) {
        this.context = context;
    }

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_org_info_entry, parent, false);
		return new OrganisationInfoEntryAdapter.MyViewHolder(row);
	}

	@Override
	public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
		holder.title.setText(list.get(position).key);
		holder.value.setText(list.get(position).value);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData data = ClipData.newPlainText(list.get(position).key, list.get(position).value);
                ClipboardManager m = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                m.setPrimaryClip(data);

                Toast.makeText(context, context.getText(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	public void setList(List<StringPair> list) {
		this.list = list;
		notifyDataSetChanged();
	}
}
