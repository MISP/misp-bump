package de.korrelator.overview.mispauthv2.models;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

public class Organisation {

    private String name;
    private String description;
    private String nationality;
    private String sector;
    private String uuid;
    private boolean local;


    public Organisation(String name, boolean local, String description, String nationality, String sector, String uuid){
        this.name = name;
        this.description = description;
        this.nationality = nationality;
        this.sector = sector;
        this.uuid = uuid;
        this.local = local;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSector() {
        return sector;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isLocal() {
        return local;
    }


    public JSONObject toJSON(){
        JSONObject organisation = new JSONObject();

        try {

            organisation.put("local", local);

            if(!name.equals("")){
                organisation.put("name", Uri.decode(name));
            }

            if(!description.equals("")){
                organisation.put("description", Uri.decode(description));
            }

            if(!nationality.equals("")){
                organisation.put("nationality", Uri.decode(nationality));
            }

            if(!sector.equals("")){
                organisation.put("sector", Uri.decode(sector));
            }

            if(!uuid.equals("")){
                organisation.put("uuid", Uri.decode(uuid));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return organisation;
    }

    public static Organisation fromJSON(JSONObject json){
        return new OrganisationBuilder()
                .name(json.optString("name", ""))
                .local(json.optBoolean("local", true))
                .description(json.optString("description", ""))
                .nationality(json.optString("nationality", ""))
                .sector(json.optString("sector", ""))
                .uuid(json.optString("uuid", ""))
                .build();
    }
}
