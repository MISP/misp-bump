package lu.circl.mispbump.models;


import androidx.annotation.NonNull;

import java.util.UUID;

import lu.circl.mispbump.auxiliary.MispRestClient;
import lu.circl.mispbump.models.restModels.Organisation;
import lu.circl.mispbump.models.restModels.Server;


public class SyncModel {

    private UUID uuid;

    private Server server;
    private Organisation organisation;
    private Organisation remoteOrganisation;


    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Organisation getRemoteOrganisation() {
        return remoteOrganisation;
    }

    public void setRemoteOrganisation(Organisation remoteOrganisation) {
        this.remoteOrganisation = remoteOrganisation;
    }


    public static void createFromServer(MispRestClient mispRestClient, Server server, InitializeWithServerObject callback) {
        SyncModel syncModel = new SyncModel();

        syncModel.server = server;

        mispRestClient.getOrganisation(server.getOrg_id(), new MispRestClient.OrganisationCallback() {
            @Override
            public void success(Organisation organisation) {
                syncModel.organisation = organisation;

                mispRestClient.getOrganisation(server.getRemote_org_id(), new MispRestClient.OrganisationCallback() {
                    @Override
                    public void success(Organisation organisation) {
                        syncModel.remoteOrganisation = organisation;

                        callback.success(syncModel);
                    }

                    @Override
                    public void failure(String error) {
                        callback.failure(error);
                    }
                });
            }

            @Override
            public void failure(String error) {
                callback.failure(error);
            }
        });
    }


    @NonNull
    @Override
    public String toString() {
        return server.toString() + "\n" + organisation.toString() + "\n" + remoteOrganisation.toString();
    }

    public interface InitializeWithServerObject {
        void success(SyncModel syncModel);

        void failure(String error);
    }
}
