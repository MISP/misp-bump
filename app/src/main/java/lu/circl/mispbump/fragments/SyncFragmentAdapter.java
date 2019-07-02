package lu.circl.mispbump.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SyncFragmentAdapter extends FragmentPagerAdapter {

    public CameraFragment cameraFragment_1, cameraFragment_2;
    private UploadSettingsFragment uploadSettingsFragment;

    public SyncFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    private CameraFragment.QrScanCallback scanCallback;

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (cameraFragment_1 == null) {
                    cameraFragment_1 = new CameraFragment();
                }

                if (scanCallback != null) {
                    cameraFragment_1.setOnQrAvailableListener(scanCallback);
                }

                return cameraFragment_1;

            case 1:
                if (cameraFragment_2 == null) {
                    cameraFragment_2 = new CameraFragment();
                }

                if (scanCallback != null) {
                    cameraFragment_1.setOnQrAvailableListener(scanCallback);
                }

                return cameraFragment_2;

            case 2:
                if (uploadSettingsFragment == null) {
                    uploadSettingsFragment = new UploadSettingsFragment();
                }

                return uploadSettingsFragment;

            default:
                return new CameraFragment();
        }
    }

    public void setQrReceivedCallback(CameraFragment.QrScanCallback qrScanCallback) {
        this.scanCallback = qrScanCallback;
    }

    public void disableCameraPreview() {
        if (cameraFragment_1 != null) {
//            cameraFragment_1.disablePreview();
        }

        if (cameraFragment_2 != null) {
//            cameraFragment_2.disablePreview();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
