package lu.circl.mispbump.restful_client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MispUser {

    @SerializedName("User")
    @Expose
    public User user;

}