package lu.circl.mispbump.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import lu.circl.mispbump.R;

public class UploadInfoFragment extends Fragment {

    public UploadInfoFragment () {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upload_info, container, false);

        return v;
    }

}
