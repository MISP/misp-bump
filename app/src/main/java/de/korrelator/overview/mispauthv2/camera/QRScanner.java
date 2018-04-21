package de.korrelator.overview.mispauthv2.camera;

import android.Manifest;
import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.support.v4.app.ActivityCompat;
import android.view.TextureView;

public class QRScanner {

    private static final int CAM_REQUEST_CODE = 1;

    private Context context;
    private TextureView textureView;

    private CameraManager cameraManager;
    private int cameraFacing;


    public QRScanner(Context context, TextureView textureView){
        this.context = context.getApplicationContext();
        this.textureView = textureView;

        Initialize();
    }

    private void Initialize(){
//        ActivityCompat.requestPermissions(, new String[]{Manifest.permission.CAMERA}, CAM_REQUEST_CODE);

//        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        cameraFacing = CameraCharacteristics.LENS_FACING_BACK;
    }
}
