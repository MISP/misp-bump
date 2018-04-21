package de.korrelator.overview.mispauthv2.models;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Server {

    private String url;
    private String name;
    private String organisationType;
    private String authKey;


    public Server(String url, String name, String organisationType, String authKey) {
        this.url = url;
        this.name = name;
        this.organisationType = organisationType;
        this.authKey = authKey;
    }


    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getOrganisationType() {
        return organisationType;
    }

    public String getAuthKey() {
        return authKey;
    }


    public JSONObject toJSON(){

        JSONObject server = new JSONObject();

        try {

            if(!url.equals("")){
                server.put("url", url);
            }

            if(!name.equals("")){
                server.put("name", name);
            }

            if(!organisationType.equals("")){
                server.put("organisation_type", organisationType);
            }

            if(!authKey.equals("")){
                server.put("authkey", authKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return server;
    }

    public static Server fromJSON(JSONObject json){
        return new Server(
                json.optString("url", ""),
                json.optString("name", ""),
                json.optString("organisation_type", ""),
                json.optString("authkey", ""));
    }
}
