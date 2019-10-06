package lu.circl.mispbump.models.restModels;


import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class MispServer {

    @SerializedName("Server")
    private Server server;

    @SerializedName("Organisation")
    private Organisation organisation;

    @SerializedName("RemoteOrg")
    private Organisation remoteOrganisation;

    @SerializedName("User")
    private List<User> user;


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

    public List<User> getUser() {
        return user;
    }
    public void setUser(List<User> user) {
        this.user = user;
    }

    @NonNull
    @Override
    public String toString() {
        return server.toString() + "\n" + organisation.toString() + "\n" + remoteOrganisation.toString();
    }
}
