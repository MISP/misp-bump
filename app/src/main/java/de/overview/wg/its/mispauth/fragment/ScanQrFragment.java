package de.overview.wg.its.mispauth.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.renderscript.*;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Size;
import android.util.SparseArray;
import android.view.*;
import android.widget.Toast;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import de.overview.wg.its.mispauth.R;
import de.overview.wg.its.mispauth.activity.SyncActivity;

import java.util.Arrays;
import java.util.List;

public class ScanQrFragment extends Fragment {

	private static final int CAMERA_REQUEST_CODE = 0;

	private HandlerThread backgroundThread;
	private Handler backgroundHandler;

	private CameraManager cameraManager;
	private CameraDevice cameraDevice;
	private String cameraID;
	private CameraCaptureSession cameraCaptureSession;

	private SurfaceTexture previewSurfaceTexture;
	private Surface previewSurface, yuvSurface;
	private Size[] yuvSizes;

	private BarcodeDetector barcodeDetector;
	private TextureView previewView;

	private boolean readQr = true;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_sync_scan, null);

		previewView = v.findViewById(R.id.texture_scan_preview);

		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

			if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {

			} else {
				ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
			}

		} else {
			setUpBarcodeDetector();
			setUpPreviewTexture();
		}

		return v;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case CAMERA_REQUEST_CODE: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					setUpBarcodeDetector();
					setUpPreviewTexture();
				} else {
					Toast.makeText(getActivity(), "Camera permission needed!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		openBackgroundThread();
	}

	@Override
	public void onStop() {
		super.onStop();
		closeCamera();
		closeBackgroundThread();
	}


	private void returnResult(String qrData) {
		((SyncActivity) getActivity()).setScannedQr(qrData);
	}

	private void setUpPreviewTexture() {

		previewView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
			@Override
			public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
				surface.setDefaultBufferSize(width, height);
				previewSurfaceTexture = surface;
				setUpCamera();
			}

			@Override
			public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

			}

			@Override
			public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
				return false;
			}

			@Override
			public void onSurfaceTextureUpdated(SurfaceTexture surface) {

			}
		});

	}

	private void setUpCamera() {
		cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);

		try {
			for (String cameraId : cameraManager.getCameraIdList()) {

				CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
				Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);

				if (facing == CameraCharacteristics.LENS_FACING_BACK) {

					cameraID = cameraId;

					StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
					yuvSizes = streamConfigurationMap.getOutputSizes(ImageFormat.YUV_420_888);

					setUpImageReader();
				}
			}
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	private void setUpImageReader() {

		Size yuvSize = yuvSizes[yuvSizes.length - 6];

		ImageReader yuvImageReader = ImageReader.newInstance(yuvSize.getWidth(), yuvSize.getHeight(), ImageFormat.YUV_420_888, 5);
		ImageReader.OnImageAvailableListener yuvImageListener = new ImageReader.OnImageAvailableListener() {

			@Override
			public void onImageAvailable(ImageReader reader) {

				if (!readQr) {
					return;
				}

				Image lastImage = reader.acquireLatestImage();
				Bitmap bitmap = YUV2Bitmap(lastImage);

				if (bitmap != null) {

					Frame frame = new Frame.Builder().setBitmap(bitmap).build();
					SparseArray<Barcode> barcodes = barcodeDetector.detect(frame);

					if (barcodes.size() > 0) {
						returnResult(barcodes.valueAt(0).rawValue);
					}
				}

				if (lastImage != null) {
					lastImage.close();
				}
			}
		};

		yuvImageReader.setOnImageAvailableListener(yuvImageListener, backgroundHandler);

		previewSurface = new Surface(previewSurfaceTexture);
		yuvSurface = yuvImageReader.getSurface();

		openCamera();
	}

	public void openCamera() {

		CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
			@Override
			public void onOpened(@NonNull CameraDevice camera) {
				cameraDevice = camera;
				createCaptureSession();
			}

			@Override
			public void onDisconnected(@NonNull CameraDevice camera) {

			}

			@Override
			public void onError(@NonNull CameraDevice camera, int error) {

			}
		};

		try {
			if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
				cameraManager.openCamera(cameraID, stateCallback, backgroundHandler);
			}
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	private void createCaptureSession() {
		List<Surface> surfaces = Arrays.asList(previewSurface, yuvSurface);

		CameraCaptureSession.StateCallback captureStateCallback = new CameraCaptureSession.StateCallback() {
			@Override
			public void onConfigured(@NonNull CameraCaptureSession session) {
				cameraCaptureSession = session;
				createCaptureRequest();
			}

			@Override
			public void onConfigureFailed(@NonNull CameraCaptureSession session) {

			}
		};

		try {
			cameraDevice.createCaptureSession(surfaces, captureStateCallback, backgroundHandler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}

	}

	private void createCaptureRequest() {
		try {

			CaptureRequest.Builder requestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_ZERO_SHUTTER_LAG);
			requestBuilder.addTarget(previewSurface);
			requestBuilder.addTarget(yuvSurface);

			CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
				@Override
				public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
					super.onCaptureCompleted(session, request, result);
				}
			};

			cameraCaptureSession.setRepeatingRequest(requestBuilder.build(), captureCallback, backgroundHandler);

		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	private void openBackgroundThread() {
		backgroundThread = new HandlerThread("raw_image_available_listener_thread");
		backgroundThread.start();
		backgroundHandler = new Handler(backgroundThread.getLooper());
	}

	private void closeBackgroundThread() {
		backgroundThread.quitSafely();
		try {
			backgroundThread.join();
			backgroundThread = null;
			backgroundHandler = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void closeCamera() {
		if (cameraCaptureSession != null) {
			cameraCaptureSession.close();
			cameraCaptureSession = null;
		}

		if (cameraDevice != null) {
			cameraDevice.close();
			cameraDevice = null;
		}
	}

	private void setUpBarcodeDetector() {
		barcodeDetector = new BarcodeDetector.Builder(getActivity())
				.setBarcodeFormats(Barcode.QR_CODE)
				.build();

		if (!barcodeDetector.isOperational()) {
			Toast.makeText(getActivity(), "Could not setup QR-Code scanner!", Toast.LENGTH_SHORT).show();
		}
	}

	private Bitmap YUV2Bitmap(Image image) {

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

		RenderScript rs = RenderScript.create(getActivity().getApplicationContext());

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
		return bmpout;
	}

	public void setReadQr(boolean enabled) {
		readQr = enabled;
	}

}
