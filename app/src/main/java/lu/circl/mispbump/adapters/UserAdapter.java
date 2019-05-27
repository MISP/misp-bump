package lu.circl.mispbump.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.KeyValue;
import lu.circl.mispbump.restful_client.Organisation;
import lu.circl.mispbump.restful_client.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<KeyValue<String, String>> data = new ArrayList<>();

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;

        UserViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.viewholder_user_title);
            description = v.findViewById(R.id.viewholder_user_description);
        }
    }

    public UserAdapter(User user, Organisation organisation) {
        data.add(new KeyValue<>("UUID", organisation.uuid));
        data.add(new KeyValue<>("Name", organisation.name));
        data.add(new KeyValue<>("Description", organisation.description));
        data.add(new KeyValue<>("Nationality", organisation.nationality));
        data.add(new KeyValue<>("Email", user.email));
//        data.add(new KeyValue<>("ID", "" + user.value));
//        data.add(new KeyValue<>("Organisation ID", "" + user.org_id));
//        data.add(new KeyValue<>("Role ID", "" + user.role_id));
    }

    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.title.setText(data.get(position).key);
        holder.description.setText(data.get(position).value);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
