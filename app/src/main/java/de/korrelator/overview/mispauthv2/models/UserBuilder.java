package de.korrelator.overview.mispauthv2.models;

public class UserBuilder {

    private String email = "default@email.bar";
    private int orgID = -1;
    private int roleType = -1;
    private String authKey = "";

    public UserBuilder(){}

    public User build(){
        return new User(email, orgID, roleType, authKey);
    }

    public UserBuilder email(String email){
        this.email = email;
        return this;
    }

    public UserBuilder orgID(int orgID){
        this.orgID = orgID;
        return this;
    }

    public UserBuilder roleType(int roleType){
        this.roleType = roleType;
        return this;
    }

    public UserBuilder authKey(String authKey){
        this.authKey = authKey;
        return this;
    }
}
