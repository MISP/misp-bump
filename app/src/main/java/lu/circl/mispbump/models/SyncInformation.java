package lu.circl.mispbump.models;


import androidx.annotation.NonNull;

import lu.circl.mispbump.models.restModels.Organisation;


/**
 * A Class that holds the information needed synchronize two misp instances.
 */
public class SyncInformation {

    public Organisation organisation;
    public String syncUserEmail;
    public String syncUserPassword;
    public String syncUserAuthkey;
    public String baseUrl;

    @NonNull
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
