package lu.circl.mispbump.restful_client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_ORG_ADMIN = 2;
    public static final int ROLE_USER = 3;
    public static final int ROLE_PUBLISHER = 4;
    public static final int ROLE_SYNC_USER = 5;
    public static final int ROLE_READ_ONLY = 6;

    public User(Integer org_id, String email, Integer role_id) {
        this.org_id = org_id;
        this.email = email;
        this.role_id = role_id;
    }

    public User(Integer org_id, String email, Integer role_id, String password) {
        this.password = password;
        this.org_id = org_id;
        this.email = email;
        this.role_id = role_id;
    }

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("org_id")
    @Expose
    public Integer org_id;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("autoalert")
    @Expose
    public Boolean autoalert;
    @SerializedName("authkey")
    @Expose
    public String authkey;
    @SerializedName("invited_by")
    @Expose
    public String invited_by;
    @SerializedName("gpgkey")
    @Expose
    public Object gpgkey;
    @SerializedName("certif_public")
    @Expose
    public String certif_public;
    @SerializedName("nids_sid")
    @Expose
    public String nids_sid;
    @SerializedName("termsaccepted")
    @Expose
    public Boolean termsaccepted;
    @SerializedName("newsread")
    @Expose
    public String newsread;
    @SerializedName("role_id")
    @Expose
    public Integer role_id;
    @SerializedName("change_pw")
    @Expose
    public String change_pw;
    @SerializedName("contactalert")
    @Expose
    public Boolean contactalert;
    @SerializedName("disabled")
    @Expose
    public Boolean disabled;
    @SerializedName("expiration")
    @Expose
    public Object expiration;
    @SerializedName("current_login")
    @Expose
    public String current_login;
    @SerializedName("last_login")
    @Expose
    public String last_login;
    @SerializedName("force_logout")
    @Expose
    public Boolean force_logout;
    @SerializedName("date_created")
    @Expose
    public Object date_created;
    @SerializedName("date_modified")
    @Expose
    public String date_modified;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", org_id='" + org_id + '\'' +
                ", email='" + email + '\'' +
                ", autoalert=" + autoalert +
                ", authkey='" + authkey + '\'' +
                ", invited_by='" + invited_by + '\'' +
                ", gpgkey=" + gpgkey +
                ", certif_public='" + certif_public + '\'' +
                ", nids_sid='" + nids_sid + '\'' +
                ", termsaccepted=" + termsaccepted +
                ", newsread='" + newsread + '\'' +
                ", role_id='" + role_id + '\'' +
                ", change_pw='" + change_pw + '\'' +
                ", contactalert=" + contactalert +
                ", disabled=" + disabled +
                ", expiration=" + expiration +
                ", current_login='" + current_login + '\'' +
                ", last_login='" + last_login + '\'' +
                ", force_logout=" + force_logout +
                ", date_created=" + date_created +
                ", date_modified='" + date_modified + '\'' +
                '}';
    }
}