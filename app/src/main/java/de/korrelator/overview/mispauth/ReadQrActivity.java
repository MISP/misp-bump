package de.korrelator.overview.mispauth;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import android.view.TextureView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.Arrays;

public class ReadQrActivity extends AppCompatActivity {

    // CAMERA
    private static final int CAMERA_REQUEST_CODE = 1;

    private CameraManager cameraManager;
    private int cameraFacing;
    private String cameraId;
    private Size previewSize;

    private CameraDevice cameraDevice;
    private CameraDevice.StateCallback stateCallback;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest captureRequest;
    private CaptureRequest.Builder captureRequestBuilder;

    private TextureView texturePreviewView;
    private TextureView.SurfaceTextureListener surfaceTextureListener;

    private HandlerThread camBackgroundThread;
    private Handler camBackgroundHandler;

    private ImageReader previewImageReader;
    private ImageReader.OnImageAvailableListener previewImageListener;

    // BARCODE
    private BarcodeDetector barcodeDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_qr);

        Initialize();
    }

    private void Initialize(){

        texturePreviewView = findViewById(R.id.texture_view);

        setUpBarcodeDetector();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK;

        surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                setUpCamera();
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        };

        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice cameraDevice) {
                ReadQrActivity.this.cameraDevice = cameraDevice;
                createPreviewSession();
            }

            @Override
            public void onDisconnected(CameraDevice cameraDevice) {
                cameraDevice.close();
                ReadQrActivity.this.cameraDevice = null;
            }

            @Override
            public void onError(CameraDevice cameraDevice, int error) {
                cameraDevice.close();
                ReadQrActivity.this.cameraDevice = null;
            }
        };

        previewImageListener = new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {

                Image img = reader.acquireLatestImage();

                if(img == null){
                    return;
                }

                Bitmap bitmapImage = YUV_420_888_toRGBIntrinsics(img);

                if(bitmapImage != null){
                    Frame frame = new Frame.Builder().setBitmap(bitmapImage).build();
                    SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);

                    if(barcodes.size() > 0){
                        returnQrResult(barcodes.valueAt(0).displayValue);
                    }
                }

                img.close();
            }
        };
    }

    private void returnQrResult(String msg){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", msg);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void setUpBarcodeDetector(){
        barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();

        Log.i("QR", "Setting up BarCodeDetector!");

        if (!barcodeDetector.isOperational()) {
            Log.e("QR", "BARCODE DETECTOR IS NOT OPERATIONAL !!!!");
        }
    }


    private void setUpCamera() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {

                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);

                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == cameraFacing) {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
                    this.cameraId = cameraId;

                    // TEST
                    previewImageReader = ImageReader.newInstance(400, 600, ImageFormat.YUV_420_888, 2);
                    previewImageReader.setOnImageAvailableListener(previewImageListener, camBackgroundHandler);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraManager.openCamera(cameraId, stateCallback, camBackgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createPreviewSession() {
        try {

            SurfaceTexture surfaceTexture = texturePreviewView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

            Surface previewSurface = new Surface(surfaceTexture);

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            // This is the real surface used for preview
            captureRequestBuilder.addTarget(previewSurface);

            // preview Surface for postprocessing
            captureRequestBuilder.addTarget(previewImageReader.getSurface());

            cameraDevice.createCaptureSession(Arrays.asList(previewSurface, previewImageReader.getSurface()), new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if (cameraDevice == null) {
                        return;
                    }

                    try {
                        captureRequest = captureRequestBuilder.build();

                        ReadQrActivity.this.cameraCaptureSession = cameraCaptureSession;
                        ReadQrActivity.this.cameraCaptureSession.setRepeatingRequest(captureRequest, null, camBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            }, camBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void camOpenBackgroundThread() {
        camBackgroundThread = new HandlerThread("camera_background_thread");
        camBackgroundThread.start();
        camBackgroundHandler = new Handler(camBackgroundThread.getLooper());
    }

    private void closeCamera() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void closeBackgroundThreads() {
        if (camBackgroundHandler != null) {
            camBackgroundThread.quitSafely();
            camBackgroundThread = null;
            camBackgroundHandler = null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        camOpenBackgroundThread();

        if (texturePreviewView.isAvailable()) {
            setUpCamera();
            openCamera();
        } else {
            texturePreviewView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeCamera();
        closeBackgroundThreads();
    }


    private Bitmap YUV_420_888_toRGBIntrinsics(Image image) {

        if (image == null) return null;

        int W = image.getWidth();
        int H = image.getHeight();

        Image.Plane Y = image.getPlanes()[0];
        Image.Plane U = image.getPlanes()[1];
        Image.Plane V = image.getPlanes()[2];

        int Yb = Y.getBuffer().remaining();
        int Ub = U.getBuffer().remaining();
        int Vb = V.getBuffer().remaining();

        byte[] data = new byte[Yb + Ub + Vb];

        Y.getBuffer().get(data, 0, Yb);
        V.getBuffer().get(data, Yb, Vb);
        U.getBuffer().get(data, Yb + Vb, Ub);

        RenderScript rs = RenderScript.create(getApplicationContext());

        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(data.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(W).setY(H);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);


        final Bitmap bmpout = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);

        in.copyFromUnchecked(data);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        out.copyTo(bmpout);
        image.close();
        return bmpout ;
    }
}
