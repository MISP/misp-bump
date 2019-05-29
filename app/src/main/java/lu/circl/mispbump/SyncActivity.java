package lu.circl.mispbump;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.auxiliary.QrCodeGenerator;
import lu.circl.mispbump.auxiliary.RandomString;
import lu.circl.mispbump.cam.CameraFragment;
import lu.circl.mispbump.restful_client.MispRestClient;
import lu.circl.mispbump.restful_client.MispServer;
import lu.circl.mispbump.restful_client.MispUser;
import lu.circl.mispbump.restful_client.Organisation;
import lu.circl.mispbump.restful_client.Server;
import lu.circl.mispbump.restful_client.User;
import lu.circl.mispbump.security.AESSecurity;

/**
 * Step 1: Add partner org as local org (uuid must be the same)
 * Step 2: Add SyncUser to local partner org
 * Step 3: Add SyncServer with SyncUser's authkey
 * <p>
 * What do we need to transmit?
 * 1. Own organisation details
 * 2. Authkey of SyncUser
 * 3. Server url
 */
public class SyncActivity extends AppCompatActivity {

    private static final String TAG = "SyncActivity";

    private AESSecurity aesSecurity;
    private MispRestClient restClient;
    private CameraFragment cameraFragment;
    private ImageView qrCodeView;
    private FloatingActionButton continueButton;

    private SyncState currentSyncState = SyncState.publicKeyExchange;

    private enum SyncState {
        publicKeyExchange,
        dataExchange
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        qrCodeView = findViewById(R.id.qrcode);
        continueButton = findViewById(R.id.continue_fab);
        continueButton.setOnClickListener(onContinueClicked);
        continueButton.hide();

        aesSecurity = AESSecurity.getInstance();
        restClient = new MispRestClient(this);

        enableSyncOptionsFragment();
    }

    private View.OnClickListener onContinueClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (currentSyncState) {
                case publicKeyExchange:
                    currentSyncState = SyncState.dataExchange;
                    showInformationQr();
                    continueButton.hide();
                    cameraFragment.setReadQrEnabled(true);
                    break;

                case dataExchange:
                    // TODO upload
                    break;
            }
        }
    };

    /**
     * Callback for the camera fragment.
     * Delivers the content of a scanned QR code.
     */
    private CameraFragment.QrScanCallback onQrCodeScanned = new CameraFragment.QrScanCallback() {
        @Override
        public void qrScanResult(String qrData) {
            switch (currentSyncState) {
                case publicKeyExchange:
                    try {
                        aesSecurity.setForeignPublicKey(AESSecurity.publicKeyFromString(qrData));
                        cameraFragment.setReadQrEnabled(false);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                continueButton.show();
                            }
                        });
                    } catch (NoSuchAlgorithmException e) {
                        MakeToast("Something gone wrong while parsing the key (no such algorithm)");
                    } catch (InvalidKeySpecException e) {
                        MakeToast("Something gone wrong while parsing the key (invalid key spec)");
                    }

                    break;

                case dataExchange:
                    // disable qr read
                    cameraFragment.setReadQrEnabled(false);

                    String data = aesSecurity.decrypt(qrData);
                    SyncInformation info = new Gson().fromJson(data, SyncInformation.class);

                    Log.i(TAG, info.organisation.toString());
                    Log.i(TAG, info.user.toString());

                    break;
            }
        }
    };

    /**
     * Creates the camera fragment used to scan the QR codes.
     */
    private void enableCameraFragment() {
        cameraFragment = new CameraFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.sync_fragment_container, cameraFragment, cameraFragment.getClass().getSimpleName());
        transaction.commit();

        cameraFragment.setReadQrEnabled(true);
        cameraFragment.setOnQrAvailableListener(onQrCodeScanned);
    }

    /**
     * options for this particular sync
     */
    private void enableSyncOptionsFragment() {
        SyncOptionsFragment syncOptionsFragment = new SyncOptionsFragment();

        syncOptionsFragment.setOnOptionsReadyCallback(new SyncOptionsFragment.OptionsReadyCallback() {
            @Override
            public void ready(boolean share_events, boolean push, boolean pull, boolean caching) {
                showPublicKeyQr();
                enableCameraFragment();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.sync_fragment_container, syncOptionsFragment, syncOptionsFragment.getClass().getSimpleName());
        transaction.commit();
    }

    /**
     * Display public key QR code.
     */
    private void showPublicKeyQr() {
        QrCodeGenerator qrCodeGenerator = new QrCodeGenerator(this);
        Bitmap bm = qrCodeGenerator.generateQrCode(AESSecurity.publicKeyToString(aesSecurity.getPublicKey()));
        qrCodeView.setImageBitmap(bm);
        qrCodeView.setVisibility(View.VISIBLE);
    }

    /**
     * Display sync info QR code.
     */
    private void showInformationQr() {
        PreferenceManager preferenceManager = PreferenceManager.getInstance(this);
        QrCodeGenerator qrCodeGenerator = new QrCodeGenerator(this);
        Gson gson = new Gson();

        // my organisation
        Organisation org = preferenceManager.getUserOrganisation();
        User user = preferenceManager.getUserInfo();
        MispUser mispUser = new MispUser(user);

        Server server = new Server(
                "SyncServer for " + org.name,
                preferenceManager.getServerUrl(),
                new RandomString(40).nextString(),
                -1
        );

        MispServer mispServer = new MispServer(server, org, null);

        SyncInformation syncInformation = new SyncInformation(user, org, server);
        String encrypted = aesSecurity.encrypt(gson.toJson(syncInformation));

        final Bitmap bm = qrCodeGenerator.generateQrCode(encrypted);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                qrCodeView.setImageBitmap(bm);
                qrCodeView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addPartnerOrg(Organisation organisation) {
        restClient.addOrganisation(organisation, new MispRestClient.OrganisationCallback() {
            @Override
            public void success(Organisation organisation) {

            }

            @Override
            public void failure(String error) {

            }
        });
    }

    private void addSyncUser(User user) {
        restClient.addUser(user, new MispRestClient.UserCallback() {
            @Override
            public void success(User user) {

            }

            @Override
            public void failure(String error) {

            }
        });
    }

    private void addServer(MispServer server) {
        restClient.addServer(server, new MispRestClient.ServerCallback() {
            @Override
            public void success(List<MispServer> servers) {

            }

            @Override
            public void success(MispServer server) {

            }

            @Override
            public void failure(String error) {

            }
        });
    }

    private void MakeToast(final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

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
