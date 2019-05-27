package lu.circl.mispbump.restful_client;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MispServer {

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