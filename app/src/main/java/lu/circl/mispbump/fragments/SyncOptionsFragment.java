package lu.circl.mispbump.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import lu.circl.mispbump.R;

public class SyncOptionsFragment extends Fragment {

    private Switch share, push, pull, cache;
    private OptionsReadyCallback readyCallback;

    public interface OptionsReadyCallback {
        void ready(boolean share_events, boolean push, boolean pull, boolean caching);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sync_options, container, false);

        share = v.findViewById(R.id.share_events_switch);
        push = v.findViewById(R.id.push_switch);
        pull = v.findViewById(R.id.pull_switch);
        cache = v.findViewById(R.id.cache_switch);

        FloatingActionButton fab = v.findViewById(R.id.sync_options_fab);
        fab.setOnClickListener(fabListener);

        return v;
    }

    private View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            readyCallback.ready(share.isChecked(), push.isChecked(), pull.isChecked(), cache.isChecked());
        }
    };

    public void setOnOptionsReadyCallback(OptionsReadyCallback callback) {
        readyCallback = callback;
    }

}