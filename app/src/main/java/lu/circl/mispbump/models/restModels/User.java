package lu.circl.mispbump.models.restModels;


import androidx.annotation.NonNull;

import lu.circl.mispbump.auxiliary.RandomString;


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


    public User toSyncUser() {
        User user = new User();
        user.email = email;
        user.authkey = new RandomString(40).nextString();
        user.password = new RandomString(16).nextString();

        return user;
    }


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

    public Integer getOrgId() {
        return org_id;
    }

    public void setOrgId(Integer org_id) {
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

    public String getInvitedBy() {
        return invited_by;
    }

    public void setInvitedBy(String invited_by) {
        this.invited_by = invited_by;
    }

    public Object getGpgKey() {
        return gpgkey;
    }

    public void setGpgKey(Object gpgkey) {
        this.gpgkey = gpgkey;
    }

    public String getCertIfPublic() {
        return certif_public;
    }

    public void setCertIfPublic(String certif_public) {
        this.certif_public = certif_public;
    }

    public String getNidsSid() {
        return nids_sid;
    }

    public void setNidsSid(String nids_sid) {
        this.nids_sid = nids_sid;
    }

    public Boolean getTermsAccepted() {
        return termsaccepted;
    }

    public void setTermsAccepted(Boolean termsaccepted) {
        this.termsaccepted = termsaccepted;
    }

    public String getNewsRead() {
        return newsread;
    }

    public void setNewsRead(String newsread) {
        this.newsread = newsread;
    }

    public Integer getRoleId() {
        return role_id;
    }

    public void setRoleId(Integer role_id) {
        this.role_id = role_id;
    }

    public String getChangePw() {
        return change_pw;
    }

    public void setChangePw(String change_pw) {
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

    public String getCurrentLogin() {
        return current_login;
    }

    public void setCurrentLogin(String current_login) {
        this.current_login = current_login;
    }

    public String getLastLogin() {
        return last_login;
    }

    public void setLastLogin(String last_login) {
        this.last_login = last_login;
    }

    public Boolean getForceLogout() {
        return force_logout;
    }

    public void setForceLogout(Boolean force_logout) {
        this.force_logout = force_logout;
    }

    public Object getDateCreated() {
        return date_created;
    }

    public void setDateCreated(Object date_created) {
        this.date_created = date_created;
    }

    public String getDateModified() {
        return date_modified;
    }

    public void setDateModified(String date_modified) {
        this.date_modified = date_modified;
    }


    @NonNull
    @Override
    public String toString() {
        return "User: \n" +
                "\t id = " + id + '\n' +
                "\t password = " + password + '\n' +
                "\t org_id = " + org_id + '\n' +
                "\t email = " + email + '\n' +
                "\t autoalert = " + autoalert + '\n' +
                "\t authkey = " + authkey + '\n' +
                "\t invited_by = " + invited_by + '\n' +
                "\t gpgkey = " + gpgkey + '\n' +
                "\t certif_public = " + certif_public + '\n' +
                "\t nids_sid = " + nids_sid + '\n' +
                "\t termsaccepted = " + termsaccepted + '\n' +
                "\t newsread = " + newsread + '\n' +
                "\t role_id = " + role_id + '\n' +
                "\t change_pw = " + change_pw + '\n' +
                "\t contactalert = " + contactalert + '\n' +
                "\t disabled = " + disabled + '\n' +
                "\t expiration = " + expiration + '\n' +
                "\t current_login = " + current_login + '\n' +
                "\t last_login = " + last_login + '\n' +
                "\t force_logout = " + force_logout + '\n' +
                "\t date_created = " + date_created + '\n' +
                "\t date_modified = " + date_modified;
    }
}
