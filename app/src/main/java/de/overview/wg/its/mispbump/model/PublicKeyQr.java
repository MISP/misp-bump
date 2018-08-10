package de.overview.wg.its.mispbump.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PublicKeyQr {

	private static final String KEY_ORG = "org";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_KEY = "key";

	private String organisation, email, key;

	public PublicKeyQr(JSONObject qr) throws JSONException {
		organisation = qr.getString(KEY_ORG);
		email = qr.getString(KEY_EMAIL);
		key = qr.getString(KEY_KEY);
	}
	public PublicKeyQr(String qr) throws JSONException {
		JSONObject json = new JSONObject(qr);

		organisation = json.getString(KEY_ORG);
		email = json.getString(KEY_EMAIL);
		key = json.getString(KEY_KEY);
	}
	public PublicKeyQr(String organisation, String email, String key) {
		this.organisation = organisation;
		this.email = email;
		this.key = key;
	}

	public JSONObject toJSON() {
		try {
			JSONObject json = new JSONObject();

			json.put(KEY_ORG, organisation);
			json.put(KEY_EMAIL, email);
			json.put(KEY_KEY, key);

			return json;

		} catch (JSONException e) {

			return null;
		}
	}

	public String getOrganisation() {
		return organisation;
	}
	public String getEmail() {
		return email;
	}
	public String getKey() {
		return key;
	}
}
