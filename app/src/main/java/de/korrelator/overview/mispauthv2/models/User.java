package de.korrelator.overview.mispauthv2.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private String email;
    private int orgID;
    private int roleType;
    private String authKey;


    public User(String email, int orgID, int roleType, String authKey) {
        this.email = email;
        this.orgID = orgID;
        this.roleType = roleType;
        this.authKey = authKey;
    }


    public String getEmail() {
        return email;
    }

    public int getOrgID() {
        return orgID;
    }

    public int getRoleType() {
        return roleType;
    }

    public String getAuthKey() {
        return authKey;
    }


    public JSONObject toJSON(){
        JSONObject user = new JSONObject();

        try {

            if(!email.equals("")){
                user.put("email", email);
            }

            if(orgID > -1){
                user.put("org_id", orgID);
            }

            if(roleType > -1){
                user.put("role_id", roleType);
            }

            if(!authKey.equals("")){
                user.put("authkey", authKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static User fromJSON(JSONObject json){
        return new User(
                json.optString("name", ""),
                json.optInt("org_id", -1),
                json.optInt("role_id", -1),
                json.optString("authkey", ""));
    }


    public interface RoleType{
        int ADMIN = 0;
        int SYNC_USER = 5;
    }
}
