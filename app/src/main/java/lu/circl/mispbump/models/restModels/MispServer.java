package lu.circl.mispbump.models.restModels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class MispServer {

    public MispServer() {
    }

    public MispServer(Server server, Organisation organisation, Organisation remoteOrganisation) {
        this.server = server;
        this.organisation = organisation;
        this.remoteOrg = remoteOrganisation;
    }

    @SerializedName("Server")
    @Expose
    public Server server;
    @SerializedName("Organisation")
    @Expose
    public Organisation organisation;
    @SerializedName("RemoteOrg")
    @Expose
    public Organisation remoteOrg;
    @SerializedName("User")
    @Expose
    public List<User> user;

}
