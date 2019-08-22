package lu.circl.mispbump.activities;


import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.MispRestClient;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.interfaces.MispService;
import lu.circl.mispbump.models.SyncInformation;
import lu.circl.mispbump.models.restModels.MispOrganisation;
import lu.circl.mispbump.models.restModels.MispServer;
import lu.circl.mispbump.models.restModels.Organisation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NetworkTestActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private MispService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_test);

        preferenceManager = PreferenceManager.getInstance(NetworkTestActivity.this);
        Pair<String, String> credentials = preferenceManager.getUserCredentials();
        MispRestClient restClient = MispRestClient.getInstance(credentials.first, credentials.second);
        service = restClient.getService();

        loadAllSyncs();
    }

    private void boundSyncInfoToServer() {
        List<SyncInformation> syncInformationList = preferenceManager.getSyncInformationList();

        for (SyncInformation syncInfo : syncInformationList) {
            String authkey = syncInfo.getRemote().getServer().getAuthkey();
            String localUUID = syncInfo.getLocal().getOrganisation().getUuid();
            String foreignUUID = syncInfo.getRemote().getOrganisation().getUuid();


        }
    }

    private void loadAllSyncs() {
        Call<List<MispServer>> allServersCall =  service.getAllServers();

        allServersCall.enqueue(new Callback<List<MispServer>>() {
            @Override
            public void onResponse(Call<List<MispServer>> call, Response<List<MispServer>> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                List<MispServer> allServers = response.body();

                assert allServers != null;

                for (MispServer mispServer : allServers) {
                    loadOrganisation(mispServer.getRemoteOrganisation().getId());
                }
            }
            @Override
            public void onFailure(Call<List<MispServer>> call, Throwable t) {

            }
        });
    }

    private void loadOrganisation(int id) {
        Call<MispOrganisation> organisationCall = service.getOrganisation(id);
        organisationCall.enqueue(new Callback<MispOrganisation>() {
            @Override
            public void onResponse(Call<MispOrganisation> call, Response<MispOrganisation> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                Organisation org = response.body().organisation;
                Log.d("DEBUG", org.toString());

            }
            @Override
            public void onFailure(Call<MispOrganisation> call, Throwable t) {

            }
        });
    }
}
