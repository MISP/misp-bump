package lu.circl.mispbump.models;

import lu.circl.mispbump.restful_client.Organisation;
import lu.circl.mispbump.restful_client.Server;
import lu.circl.mispbump.restful_client.User;

/**
 * A Class that holds the information needed synchronize two misp instances.
 * This class can be serialized and passed via QR code.
 */
public class SyncInformation {

    public User user;
    public Organisation organisation;
    public Server server;

    public SyncInformation(User user, Organisation organisation, Server server) {
        this.user = user;
        this.organisation = organisation;
        this.server = server;
    }
}
