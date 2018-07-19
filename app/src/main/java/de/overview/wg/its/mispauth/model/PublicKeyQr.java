package de.overview.wg.its.mispauth.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PublicKeyQr {

	private static final String KEY_ORG = "org";
	private static final String KEY_USER = "user";
	private static final String KEY_KEY = "key";

	private String organisation, user, key;

	public PublicKeyQr(JSONObject qr) throws JSONException {
		organisation = qr.getString(KEY_ORG);
		user = qr.getString(KEY_USER);
		key = qr.getString(KEY_KEY);
	}

	public PublicKeyQr(String qr) throws JSONException{
		JSONObject json = new JSONObject(qr);

		organisation = json.getString(KEY_ORG);
		user = json.getString(KEY_USER);
		key = json.getString(KEY_KEY);
	}

	public PublicKeyQr(String organisation, String user, String key) {
		this.organisation = organisation;
		this.user = user;
		this.key = key;
	}

	public JSONObject toJSON() {
		try {
			JSONObject json = new JSONObject();

			json.put(KEY_ORG, organisation);
			json.put(KEY_USER, user);
			json.put(KEY_KEY, key);

			return json;

		} catch (JSONException e) {

			return null;
		}
	}

	public String getOrganisation() {
		return organisation;
	}

	public String getUser() {
		return user;
	}

	public String getKey() {
		return key;
	}
}
