package lu.circl.mispbump.models.restModels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MispUser {

    @SerializedName("User")
    @Expose
    public User user;

    public MispUser(User user) {
        this.user = user;
    }
}
