package lu.circl.mispbump.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import lu.circl.mispbump.R;
import lu.circl.mispbump.customViews.MaterialPreferenceSwitch;
import lu.circl.mispbump.models.UploadInformation;

public class UploadSettingsFragment extends Fragment {

    private MaterialPreferenceSwitch allowSelfSigned, push, pull, cache;
    private UploadInformation uploadInformation;

    public UploadSettingsFragment() {
    }

    public UploadSettingsFragment(UploadInformation uploadInformation) {
        this.uploadInformation = uploadInformation;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upload_settings, container, false);

        allowSelfSigned = v.findViewById(R.id.self_signed_switch);
        push = v.findViewById(R.id.push_switch);
        pull = v.findViewById(R.id.pull_switch);
        cache = v.findViewById(R.id.cache_switch);

        populateContent();

        return v;
    }

    private void populateContent() {
        if (uploadInformation == null) {
            return;
        }

        allowSelfSigned.setChecked(uploadInformation.isAllowSelfSigned());
        push.setChecked(uploadInformation.isPush());
        pull.setChecked(uploadInformation.isPull());
        cache.setChecked(uploadInformation.isCached());
    }

    public void setUploadInfo(UploadInformation uploadInfo) {
        this.uploadInformation = uploadInfo;
    }

    public boolean getAllowSelfSigned() {
        return allowSelfSigned.isChecked();
    }

    public void setAllowSelfSigned(boolean allowSelfSigned) {
        this.allowSelfSigned.setChecked(allowSelfSigned);
    }

    public boolean getPush() {
        return push.isChecked();
    }

    public void setPush(boolean push) {
        this.push.setChecked(push);
    }

    public boolean getPull() {
        return pull.isChecked();
    }

    public void setPull(boolean pull) {
        this.pull.setChecked(pull);
    }

    public boolean getCache() {
        return cache.isChecked();
    }

    public void setCache(boolean cache) {
        this.cache.setChecked(cache);
    }
}