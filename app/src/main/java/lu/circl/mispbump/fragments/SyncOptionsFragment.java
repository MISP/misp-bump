package lu.circl.mispbump.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import lu.circl.mispbump.R;

public class SyncOptionsFragment extends Fragment {

    public Switch allowSelfSigned, push, pull, cache;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sync_options, container, false);

        allowSelfSigned = v.findViewById(R.id.self_signed_switch);
        push = v.findViewById(R.id.push_switch);
        pull = v.findViewById(R.id.pull_switch);
        cache = v.findViewById(R.id.cache_switch);

        return v;
    }
}