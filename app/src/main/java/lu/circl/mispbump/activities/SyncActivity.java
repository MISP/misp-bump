package lu.circl.mispbump.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.DialogManager;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.auxiliary.QrCodeGenerator;
import lu.circl.mispbump.auxiliary.RandomString;
import lu.circl.mispbump.cam.CameraFragment;
import lu.circl.mispbump.fragments.SyncOptionsFragment;
import lu.circl.mispbump.models.SyncInformation;
import lu.circl.mispbump.models.UploadInformation;
import lu.circl.mispbump.restful_client.MispRestClient;
import lu.circl.mispbump.restful_client.MispServer;
import lu.circl.mispbump.restful_client.Organisation;
import lu.circl.mispbump.restful_client.Server;
import lu.circl.mispbump.restful_client.User;
import lu.circl.mispbump.security.DiffieHellman;

/**
 * This class provides the sync functionality.
 * It collects the necessary information, guides through the process and finally completes with
 * the upload to the misp instance.
 */
public class SyncActivity extends AppCompatActivity {

    private static final String TAG = "SyncActivity";

    private CoordinatorLayout layout;
    private ImageView qrCodeView;
    private FloatingActionButton continueButton;

    private CameraFragment cameraFragment;
    private DiffieHellman diffieHellman;
    private MispRestClient restClient;

    private UploadInformation uploadInformation;

    private SyncState currentSyncState = SyncState.publicKeyExchange;

    private PreferenceManager preferenceManager;

    private enum SyncState {
        publicKeyExchange,
        dataExchange
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        Toolbar myToolbar = findViewById(R.id.appbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        layout = findViewById(R.id.layout);

        qrCodeView = findViewById(R.id.qrcode);
        continueButton = findViewById(R.id.continue_fab);
        continueButton.setOnClickListener(onContinueClicked);
        continueButton.hide();

        diffieHellman = DiffieHellman.getInstance();
        restClient = new MispRestClient(this);

        preferenceManager = PreferenceManager.getInstance(this);

        enableSyncOptionsFragment();
    }

    /**
     * This callback is called at the end of each sync step.
     */
    private View.OnClickListener onContinueClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            cameraFragment.setReadQrEnabled(false);

            switch (currentSyncState) {
                case publicKeyExchange:
                    DialogManager.confirmProceedDialog(SyncActivity.this,
                            new DialogManager.IDialogFeedback() {
                                @Override
                                public void positive() {
                                    currentSyncState = SyncState.dataExchange;
                                    continueButton.hide();
                                    cameraFragment.setReadQrEnabled(true);
                                    showInformationQr();
                                }

                                @Override
                                public void negative() {
                                }
                            });
                    break;

                case dataExchange:
                    DialogManager.confirmProceedDialog(SyncActivity.this, new DialogManager.IDialogFeedback() {
                        @Override
                        public void positive() {
                            startUpload();
                        }

                        @Override
                        public void negative() {
                        }
                    });
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
            cameraFragment.setReadQrEnabled(false);

            switch (currentSyncState) {
                case publicKeyExchange:
                    try {
                        final PublicKey pk = DiffieHellman.publicKeyFromString(qrData);
                        diffieHellman.setForeignPublicKey(pk);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                continueButton.show();

                                Snackbar sb = Snackbar.make(continueButton, "Public key received", Snackbar.LENGTH_LONG);
                                sb.setAction("Details", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DialogManager.publicKeyDialog(pk, SyncActivity.this, null);
                                    }
                                });
                                sb.show();
                            }
                        });
                    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                        Snackbar.make(layout, "Invalid key", Snackbar.LENGTH_SHORT).show();
                    }
                    break;

                case dataExchange:
                    cameraFragment.setReadQrEnabled(false);

                    final SyncInformation remoteSyncInfo = new Gson().fromJson(diffieHellman.decrypt(qrData), SyncInformation.class);

                    DialogManager.syncInformationDialog(remoteSyncInfo,
                            SyncActivity.this,
                            new DialogManager.IDialogFeedback() {
                                @Override
                                public void positive() {
                                    uploadInformation.remote = remoteSyncInfo;
                                    continueButton.show();
                                }

                                @Override
                                public void negative() {
                                    cameraFragment.setReadQrEnabled(true);
                                }
                            });

                    break;
            }
        }
    };

    private void startUpload() {
        // check if misp instance is available
        restClient.isAvailable(new MispRestClient.AvailableCallback() {
            @Override
            public void unavailable() {
                Snackbar sb = Snackbar.make(layout, "MISP instance not available", Snackbar.LENGTH_LONG);
                sb.setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startUpload();  // TODO check if this works
                    }
                });
                sb.show();
            }

            @Override
            public void available() {

                restClient.addOrganisation(uploadInformation.remote.organisation, new MispRestClient.OrganisationCallback() {
                    @Override
                    public void success(final Organisation organisation) {
                        // create syncUser object from syncInfo
                        User syncUser = new User();
                        syncUser.org_id = organisation.id;
                        syncUser.role_id = User.ROLE_SYNC_USER;

                        // syncuser_ORG@REMOTE_ORG_EMAIL_DOMAIN
                        String emailSaveOrgName = organisation.name.replace(" ", "").toLowerCase();
                        syncUser.email = "syncuser_" + emailSaveOrgName + "@misp.de";

                        syncUser.password = uploadInformation.remote.syncUserPassword;
                        syncUser.authkey = uploadInformation.remote.syncUserAuthkey;
                        syncUser.termsaccepted = true;

                        // add user to local organisation
                        restClient.addUser(syncUser, new MispRestClient.UserCallback() {
                            @Override
                            public void success(User user) {
                                Server server = new Server();
                                server.name = organisation.name + "'s Sync Server";
                                server.url = uploadInformation.remote.baseUrl;
                                server.remote_org_id = organisation.id;
                                server.authkey = uploadInformation.local.syncUserAuthkey;
                                server.self_signed = true;

                                restClient.addServer(server, new MispRestClient.ServerCallback() {
                                    @Override
                                    public void success(List<MispServer> servers) {
                                    }

                                    @Override
                                    public void success(MispServer server) {
                                    }

                                    @Override
                                    public void success(Server server) {
                                        uploadInformation.currentSyncStatus = UploadInformation.SyncStatus.COMPLETE;
                                        preferenceManager.setUploadInformation(uploadInformation);
                                        finish();
                                    }

                                    @Override
                                    public void failure(String error) {
                                        uploadInformation.currentSyncStatus = UploadInformation.SyncStatus.FAILURE;
                                        preferenceManager.setUploadInformation(uploadInformation);
                                        Snackbar.make(layout, error, Snackbar.LENGTH_LONG).show();
                                        Log.e(TAG, error);
                                    }
                                });
                            }

                            @Override
                            public void failure(String error) {
                                uploadInformation.currentSyncStatus = UploadInformation.SyncStatus.FAILURE;
                                preferenceManager.setUploadInformation(uploadInformation);
                                Snackbar.make(layout, error, Snackbar.LENGTH_LONG).show();
                                Log.e(TAG, error);
                            }
                        });
                    }

                    @Override
                    public void failure(String error) {
                        uploadInformation.currentSyncStatus = UploadInformation.SyncStatus.FAILURE;
                        preferenceManager.setUploadInformation(uploadInformation);
                        Snackbar.make(layout, error, Snackbar.LENGTH_LONG).show();
                        Log.e(TAG, error);
                    }
                });
            }
        });
    }

    /**
     * Creates the camera fragment used to scan the QR codes.
     * Automatically starts processing images (search QR codes).
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
     * Creates fragment to tweak sync options.
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
     * Display QR code that contains the public key .
     */
    private void showPublicKeyQr() {
        QrCodeGenerator qrCodeGenerator = new QrCodeGenerator(this);
        Bitmap bm = qrCodeGenerator.generateQrCode(DiffieHellman.publicKeyToString(diffieHellman.getPublicKey()));
        qrCodeView.setImageBitmap(bm);
        qrCodeView.setVisibility(View.VISIBLE);
    }

    /**
     * Display QR code that contains mandatory information for a sync.
     */
    private void showInformationQr() {
        PreferenceManager preferenceManager = PreferenceManager.getInstance(this);

        SyncInformation syncInformation = new SyncInformation();

        syncInformation.organisation = preferenceManager.getUserOrganisation().syncOrganisation();
        syncInformation.syncUserAuthkey = new RandomString(40).nextString();
        syncInformation.baseUrl = preferenceManager.getServerUrl();
        syncInformation.syncUserPassword = "abcdefghijklmnop";

        uploadInformation = new UploadInformation(syncInformation);

        // encrypt serialized content
        String encrypted = diffieHellman.encrypt(new Gson().toJson(syncInformation));

        // Generate QR code
        QrCodeGenerator qrCodeGenerator = new QrCodeGenerator(this);
        final Bitmap bm = qrCodeGenerator.generateQrCode(encrypted);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                qrCodeView.setImageBitmap(bm);
                qrCodeView.setVisibility(View.VISIBLE);
            }
        });
    }

//    /**
//     * Display toast on UI thread.
//     *
//     * @param message message to display
//     */
//    private void MakeToast(final String message) {
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//            }
//        });
//    }

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
