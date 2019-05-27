package lu.circl.mispbump;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lu.circl.mispbump.cam.CameraFragment;
import lu.circl.mispbump.restful_client.MispRestClient;
import lu.circl.mispbump.restful_client.MispServer;
import lu.circl.mispbump.restful_client.Organisation;
import lu.circl.mispbump.restful_client.Server;
import lu.circl.mispbump.restful_client.User;
import lu.circl.mispbump.security.AESSecurity;

public class SyncActivity extends AppCompatActivity {

    private static final String TAG = "SyncActivity";
    private CameraFragment cameraFragment;

    private MispRestClient restClient;
    private ImageView qrCodeView;

    private AESSecurity aesSecurity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        qrCodeView = findViewById(R.id.qrcode);
        aesSecurity = AESSecurity.getInstance();

        showPublicKeyQr();
        enableCameraFragment();
    }

    private void enableCameraFragment() {
        cameraFragment = new CameraFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.sync_fragment_container, cameraFragment, cameraFragment.getClass().getSimpleName());
        transaction.commit();

        cameraFragment.setReadQrEnabled(true);
        cameraFragment.setOnQrAvailableListener(resultCallback);
    }

    private void showPublicKeyQr() {
        Bitmap bm = generateQrCodeFromString(aesSecurity.getPublicKey().toString());
        qrCodeView.setImageBitmap(bm);
        qrCodeView.setVisibility(View.VISIBLE);
    }

    private CameraFragment.QrResultCallback resultCallback = new CameraFragment.QrResultCallback() {
        @Override
        public void qrDataResult(String qrData) {
            // TODO validate data
            cameraFragment.setReadQrEnabled(false);
            MakeToast(qrData);
        }
    };

    private void MakeToast(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private Bitmap generateQrCodeFromString(String content) {

        Point displaySize = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(displaySize);

        int size = displaySize.x;

        if (displaySize.x > displaySize.y) {
            size = displaySize.y;
        }

        size = (int)(size * 0.8);

        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

            Map<EncodeHintType, Integer> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 0);

            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            return createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Bitmap createBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? 0xFF000000 : 0x55FFFFFF;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

//    private View.OnClickListener onAddUser = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//            User user = new User(1, "felixpk@outlook.de", MispRestClient.roleId.SYNC_USER.value());
//
//            restClient.addUser(user, new MispRestClient.UserCallback() {
//                @Override
//                public void success(User user) {
//                    resultView.setText(user.toString());
//                }
//
//                @Override
//                public void failure(String error) {
//                    resultView.setText(error);
//                }
//            });
//        }
//    };
//
//    private View.OnClickListener onAddOrganisation = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Organisation organisation = new Organisation("API Organisation 2", "API Generated Organisation");
//            organisation.local = true;
//            organisation.nationality = "Buxdehude";
//
//            restClient.addOrganisation(organisation, new MispRestClient.OrganisationCallback() {
//                @Override
//                public void success(Organisation organisation) {
//                    resultView.setText(organisation.toString());
//                }
//
//                @Override
//                public void failure(String error) {
//                    resultView.setText(error);
//                }
//            });
//        }
//    };
//
//    private View.OnClickListener onAddServer = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//            Organisation organisation = new Organisation("", "");
//            Server server = new Server("API Remote Server", "https://127.0.0.1", "0000000000000000000000000000000000000000", 1);
//
//            MispServer mispServer = new MispServer(server, organisation, organisation);
//
//
//            restClient.addServer(mispServer, new MispRestClient.ServerCallback() {
//                @Override
//                public void success(List<MispServer> servers) {
//
//                }
//
//                @Override
//                public void success(MispServer server) {
//
//                }
//
//                @Override
//                public void failure(String error) {
//
//                }
//            });
//        }
//    };
//
//    private View.OnClickListener onGetServers = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            restClient.getServers(new MispRestClient.ServerCallback() {
//                @Override
//                public void success(List<MispServer> servers) {
//                    for (MispServer server : servers) {
//                        resultView.append(server.server.toString() + "\n\n");
//                        resultView.append(server.organisation.toString() + "\n\n");
//                        resultView.append(server.remoteOrg.toString());
//                    }
//                }
//
//                @Override
//                public void success(MispServer server) {
//
//                }
//
//                @Override
//                public void failure(String error) {
//                    resultView.setText(error);
//                }
//            });
//        }
//    };
}
