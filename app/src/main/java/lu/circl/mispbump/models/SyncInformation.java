package lu.circl.mispbump.models;


import lu.circl.mispbump.models.restModels.Organisation;


/**
 * A Class that holds the information needed synchronize two misp instances.
 * This class can be serialized and passed via QR code.
 */
public class SyncInformation {

    public Organisation organisation;
    public String syncUserEmail;
    public String syncUserPassword;
    public String syncUserAuthkey;
    public String baseUrl;

    public SyncInformation() {
    }

    @Override
    public String toString() {
        return "SyncInformation{" +
                "organisation=" + organisation +
                ", syncUserEmail='" + syncUserEmail + '\'' +
                ", syncUserPassword='" + syncUserPassword + '\'' +
                ", syncUserAuthkey='" + syncUserAuthkey + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                '}';
    }
}
