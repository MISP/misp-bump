package lu.circl.mispbump.models.restModels;


import androidx.annotation.NonNull;


public class User {

    private Integer id;
    private String password;
    private Integer org_id;
    private String email;
    private Boolean autoalert;
    private String authkey;
    private String invited_by;
    private Object gpgkey;
    private String certif_public;
    private String nids_sid;
    private Boolean termsaccepted;
    private String newsread;
    private Integer role_id;
    private String change_pw;
    private Boolean contactalert;
    private Boolean disabled;
    private Object expiration;
    private String current_login;
    private String last_login;
    private Boolean force_logout;
    private Object date_created;
    private String date_modified;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Integer org_id) {
        this.org_id = org_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAutoalert() {
        return autoalert;
    }

    public void setAutoalert(Boolean autoalert) {
        this.autoalert = autoalert;
    }

    public String getAuthkey() {
        return authkey;
    }

    public void setAuthkey(String authkey) {
        this.authkey = authkey;
    }

    public String getInvited_by() {
        return invited_by;
    }

    public void setInvited_by(String invited_by) {
        this.invited_by = invited_by;
    }

    public Object getGpgkey() {
        return gpgkey;
    }

    public void setGpgkey(Object gpgkey) {
        this.gpgkey = gpgkey;
    }

    public String getCertif_public() {
        return certif_public;
    }

    public void setCertif_public(String certif_public) {
        this.certif_public = certif_public;
    }

    public String getNids_sid() {
        return nids_sid;
    }

    public void setNids_sid(String nids_sid) {
        this.nids_sid = nids_sid;
    }

    public Boolean getTermsaccepted() {
        return termsaccepted;
    }

    public void setTermsaccepted(Boolean termsaccepted) {
        this.termsaccepted = termsaccepted;
    }

    public String getNewsread() {
        return newsread;
    }

    public void setNewsread(String newsread) {
        this.newsread = newsread;
    }

    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public String getChange_pw() {
        return change_pw;
    }

    public void setChange_pw(String change_pw) {
        this.change_pw = change_pw;
    }

    public Boolean getContactalert() {
        return contactalert;
    }

    public void setContactalert(Boolean contactalert) {
        this.contactalert = contactalert;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Object getExpiration() {
        return expiration;
    }

    public void setExpiration(Object expiration) {
        this.expiration = expiration;
    }

    public String getCurrent_login() {
        return current_login;
    }

    public void setCurrent_login(String current_login) {
        this.current_login = current_login;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public Boolean getForce_logout() {
        return force_logout;
    }

    public void setForce_logout(Boolean force_logout) {
        this.force_logout = force_logout;
    }

    public Object getDate_created() {
        return date_created;
    }

    public void setDate_created(Object date_created) {
        this.date_created = date_created;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }


    @NonNull
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
