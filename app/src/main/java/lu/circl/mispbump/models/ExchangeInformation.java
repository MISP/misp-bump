package lu.circl.mispbump.models;


import androidx.annotation.NonNull;

import lu.circl.mispbump.models.restModels.Organisation;
import lu.circl.mispbump.models.restModels.Server;
import lu.circl.mispbump.models.restModels.User;


public class ExchangeInformation {

    private Organisation organisation;
    private User syncUser;
    private Server server;

    public Organisation getOrganisation() {
        return organisation;
    }
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public User getSyncUser() {
        return syncUser;
    }
    public void setSyncUser(User syncUser) {
        this.syncUser = syncUser;
    }

    public Server getServer() {
        return server;
    }
    public void setServer(Server server) {
        this.server = server;
    }


    @NonNull
    @Override
    public String toString() {
        return "Exchange Information: \n" + organisation.toString() + "\n" + syncUser.toString() + "\n" + server.toString();
    }
}
