package lu.circl.mispbump.restful_client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Information gathered from Misp API about a organisation.
 */
public class Organisation {

    public Organisation(String name) {
        this.name = name;
    }

    public Organisation(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("date_created")
    @Expose
    public String date_created;
    @SerializedName("date_modified")
    @Expose
    public String date_modified;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("nationality")
    @Expose
    public String nationality;
    @SerializedName("sector")
    @Expose
    public String sector;
    @SerializedName("contacts")
    @Expose
    public String contacts;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("local")
    @Expose
    public Boolean local;
    @SerializedName("uuid")
    @Expose
    public String uuid;
    @SerializedName("restricted_to_domain")
    @Expose
    public String restricted_to_domain;
    @SerializedName("created_by")
    @Expose
    public String created_by;
    @SerializedName("user_count")
    @Expose
    public Integer user_count;

    @Override
    public String toString() {
        return "Organisation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date_created='" + date_created + '\'' +
                ", date_modified='" + date_modified + '\'' +
                ", type='" + type + '\'' +
                ", nationality='" + nationality + '\'' +
                ", sector='" + sector + '\'' +
                ", contacts='" + contacts + '\'' +
                ", description='" + description + '\'' +
                ", local=" + local +
                ", uuid='" + uuid + '\'' +
                ", restricted_to_domain='" + restricted_to_domain + '\'' +
                ", created_by='" + created_by + '\'' +
                ", user_count=" + user_count +
                '}';
    }
}
