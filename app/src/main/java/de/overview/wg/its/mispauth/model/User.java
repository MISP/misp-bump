package de.overview.wg.its.mispauth.model;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

	// todo: must be configable? Roles can be edited on instance
	public interface RoleId {
		int ADMIN = 1;
		int ORG_ADMIN = 2;
		int USER = 3;
		int PUBLISHER = 4;
		int SYNC_USER = 5;
		int READ_ONLY = 6;
	}

	public static final String ROOT_KEY = "User";

	private static String ID_KEY = "id";
	private static String PASSWORD_KEY = "password";
	private static String ORG_ID_KEY = "org_id";
	private static String EMAIL_KEY = "email";
	private static String AUTOALERT_KEY = "autoalert";
	private static String AUTHKEY_KEY = "authkey";
	private static String INVITED_BY_KEY = "invited_by";
	private static String GPGKEY_KEY = "gpgkey";
	private static String CERTIF_PUBLIC = "certif_public";
	private static String NIDS_SID = "nids_sid";
	private static String TERMS_ACCEPTED_KEY = "termsaccepted";
	private static String NEWSREAD_KEY = "newsread";
	private static String ROLE_ID_KEY = "role_id";
	private static String CHANGE_PW_KEY = "change_pw";
	private static String CONTACT_ALERT_KEY = "contactalert";
	private static String DISABLED_KEY = "disabled";
	private static String EXPIRATION_KEY = "expiration";
	private static String CURRENT_LOGIN_KEY = "current_login";
	private static String LAST_LOGIN_KEY = "last_login";
	private static String FORCE_LOGOUT_KEY = "force_logout";
	private static String DATE_CREATED_KEY = "date_created";
	private static String DATE_MODIFIED_KEY = "date_modified";

	private int id;
	private String password;
	private int orgId;
	private String email;
	private boolean autoAlert;
	private String authkey;
	private int invitedBy;
	private String gpgKey;
	private String certifPublic;
	private int nidsSid;
	private boolean termsAccepted;
	private int newsRead; // Integer??
	private int roleId;
	private String changePw;
	private boolean contactAlert;
	private boolean disabled;
	private String expiration;
	private String currentLogin;
	private String lastLogin;
	private boolean forceLogout;
	private String dateCreated;
	private String dateModified;

	public User() {}

	public User(JSONObject user) throws JSONException {
		fromJSON(user);
	}

	public void fromJSON(JSONObject user) throws JSONException {

		id = user.optInt(ID_KEY, -1);
		password = user.optString(PASSWORD_KEY);
		orgId = user.optInt(ORG_ID_KEY, -1);
		email = user.optString(EMAIL_KEY);
		autoAlert = user.optBoolean(AUTOALERT_KEY);
		authkey = user.optString(AUTHKEY_KEY);
		invitedBy = user.optInt(INVITED_BY_KEY, -1);
		gpgKey = user.optString(GPGKEY_KEY);
		certifPublic = user.optString(CERTIF_PUBLIC);
		nidsSid = user.optInt(NIDS_SID);
		termsAccepted = user.optBoolean(TERMS_ACCEPTED_KEY, false);
		newsRead = user.optInt(NEWSREAD_KEY);
		roleId = user.optInt(ROLE_ID_KEY, -1);
		changePw = user.optString(CHANGE_PW_KEY);
		contactAlert = user.optBoolean(CONTACT_ALERT_KEY, true);
		disabled = user.optBoolean(DISABLED_KEY, false);
		expiration = user.optString(EXPIRATION_KEY);
		currentLogin = user.optString(CURRENT_LOGIN_KEY);
		lastLogin = user.optString(LAST_LOGIN_KEY);
		forceLogout = user.optBoolean(FORCE_LOGOUT_KEY);
		dateCreated = user.optString(DATE_CREATED_KEY);
		dateModified = user.optString(DATE_MODIFIED_KEY);

	}
	public JSONObject toJSON() {
		return toJSON(false);
	}
	public JSONObject toJSON(boolean forSyncQR) {
		JSONObject user = new JSONObject();

		try {

			user.putOpt(EMAIL_KEY, email);

			if (!forSyncQR) {

				user.putOpt(ID_KEY, id);
				user.putOpt(ORG_ID_KEY, orgId);
				user.putOpt(AUTHKEY_KEY, authkey);
				user.putOpt(ROLE_ID_KEY, roleId);
				user.putOpt(PASSWORD_KEY, password);
				user.putOpt(CHANGE_PW_KEY, changePw);
				user.putOpt(TERMS_ACCEPTED_KEY, termsAccepted);
				user.putOpt(CERTIF_PUBLIC, certifPublic);
				user.putOpt(GPGKEY_KEY, gpgKey);
				user.putOpt(AUTOALERT_KEY, autoAlert);
				user.putOpt(INVITED_BY_KEY, invitedBy);
				user.putOpt(NIDS_SID, nidsSid);
				user.putOpt(NEWSREAD_KEY, newsRead);
				user.putOpt(CONTACT_ALERT_KEY, contactAlert);
				user.putOpt(DISABLED_KEY, disabled);
				user.putOpt(EXPIRATION_KEY, expiration);
				user.putOpt(CURRENT_LOGIN_KEY, currentLogin);
				user.putOpt(LAST_LOGIN_KEY, lastLogin);
				user.putOpt(FORCE_LOGOUT_KEY, forceLogout);
				user.putOpt(DATE_CREATED_KEY, dateCreated);
				user.putOpt(DATE_MODIFIED_KEY, dateModified);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return user;
	}

	public void clearForStorage() {
		setAuthkey("");
		setGpgKey("");
		setCertifPublic("");
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public int getOrgId() {
		return orgId;
	}
	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isAutoAlert() {
		return autoAlert;
	}
	public void setAutoAlert(boolean autoAlert) {
		this.autoAlert = autoAlert;
	}

	public String getAuthkey() {
		return authkey;
	}
	public void setAuthkey(String authkey) {
		this.authkey = authkey;
	}

	public int getInvitedBy() {
		return invitedBy;
	}
	public void setInvitedBy(int invitedBy) {
		this.invitedBy = invitedBy;
	}

	public String getGpgKey() {
		return gpgKey;
	}
	public void setGpgKey(String gpgKey) {
		this.gpgKey = gpgKey;
	}

	public String getCertifPublic() {
		return certifPublic;
	}
	public void setCertifPublic(String certifPublic) {
		this.certifPublic = certifPublic;
	}

	public int getNidsSid() {
		return nidsSid;
	}
	public void setNidsSid(int nidsSid) {
		this.nidsSid = nidsSid;
	}

	public boolean isTermsAccepted() {
		return termsAccepted;
	}
	public void setTermsAccepted(boolean termsAccepted) {
		this.termsAccepted = termsAccepted;
	}

	public int getNewsRead() {
		return newsRead;
	}
	public void setNewsRead(int newsRead) {
		this.newsRead = newsRead;
	}

	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getChangePw() {
		return changePw;
	}
	public void setChangePw(String changePw) {
		this.changePw = changePw;
	}

	public boolean isContactAlert() {
		return contactAlert;
	}
	public void setContactAlert(boolean contactAlert) {
		this.contactAlert = contactAlert;
	}

	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getExpiration() {
		return expiration;
	}
	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}

	public String getCurrentLogin() {
		return currentLogin;
	}
	public void setCurrentLogin(String currentLogin) {
		this.currentLogin = currentLogin;
	}

	public String getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public boolean isForceLogout() {
		return forceLogout;
	}
	public void setForceLogout(boolean forceLogout) {
		this.forceLogout = forceLogout;
	}

	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getDateModified() {
		return dateModified;
	}
	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}
}
