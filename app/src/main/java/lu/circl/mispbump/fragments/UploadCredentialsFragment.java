package lu.circl.mispbump.fragments;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import lu.circl.mispbump.R;
import lu.circl.mispbump.customViews.MaterialPasswordView;
import lu.circl.mispbump.customViews.MaterialPreferenceText;
import lu.circl.mispbump.models.UploadInformation;


public class UploadCredentialsFragment extends Fragment {

    private View rootLayout;
    private UploadInformation uploadInformation;

    public UploadCredentialsFragment() {
    }

    public UploadCredentialsFragment(UploadInformation uploadInformation) {
        this.uploadInformation = uploadInformation;
    }

    private MaterialPasswordView.OnCopyClickListener onCopyClickListener = new MaterialPasswordView.OnCopyClickListener() {
        @Override
        public void onClick(String title, String password) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(title, password);
            clipboard.setPrimaryClip(clip);
            Snackbar.make(rootLayout, "Copied to clipboard", Snackbar.LENGTH_LONG).show();
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upload_credentials, container, false);

        rootLayout = v.findViewById(R.id.rootLayout);

        MaterialPreferenceText baseUrl = v.findViewById(R.id.baseUrl);
        baseUrl.setSubtitle(uploadInformation.getRemote().baseUrl);

        MaterialPreferenceText email = v.findViewById(R.id.email);
        email.setSubtitle(uploadInformation.getLocal().syncUserEmail);

        MaterialPasswordView authkey = v.findViewById(R.id.authkey);
        authkey.setPassword(uploadInformation.getRemote().syncUserAuthkey);
        authkey.addOnCopyClickedListener(onCopyClickListener);

        MaterialPasswordView password = v.findViewById(R.id.password);
        password.setPassword(uploadInformation.getRemote().syncUserPassword);
        password.addOnCopyClickedListener(onCopyClickListener);

        return v;
    }

}
