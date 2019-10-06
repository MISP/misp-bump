package lu.circl.mispbump.models.restModels;


import com.google.gson.annotations.SerializedName;


public class MispUser {

    @SerializedName("User")
    private User user;

    @SerializedName("Role")
    private Role role;

    @SerializedName("Organisation")
    private Organisation organisation;


    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public Organisation getOrganisation() {
        return organisation;
    }
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

}
