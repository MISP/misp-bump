package de.overview.wg.its.mispauth.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Server {

	public static final String ROOT_KEY = "Server";

	private static final String URL_KEY = "url";
	private static final String NAME_KEY = "name";
	private static final String REMOTE_ORG_ID_KEY = "remote_org_id";
	private static final String AUTHKEY_KEY = "authkey";
	private static final String PUSH_KEY = "push";
	private static final String PULL_KEY = "pull";

	private String url;
	private String name;
	private int remoteOrgId;
	private String authkey;
	private boolean push, pull;

	public Server() { }
	public Server(JSONObject json) throws JSONException {
		fromJSON(json);
	}

	public void fromJSON(JSONObject server) throws JSONException {
		url = server.optString(URL_KEY);
		name = server.optString(NAME_KEY);
		remoteOrgId = server.optInt(REMOTE_ORG_ID_KEY, -1);
		authkey = server.optString(AUTHKEY_KEY);
		push = server.optBoolean(PUSH_KEY, false);
		pull = server.optBoolean(PULL_KEY, false);
	}

	public JSONObject toJSON() {
		return toJSON(false);
	}
	public JSONObject toJSON(boolean minimal) {

		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.putOpt(URL_KEY, url);
			jsonObject.putOpt(NAME_KEY, name);
			jsonObject.putOpt(AUTHKEY_KEY, authkey);

			if (!minimal) {
				jsonObject.putOpt(REMOTE_ORG_ID_KEY, remoteOrgId);
				jsonObject.putOpt(PUSH_KEY, push);
				jsonObject.putOpt(PULL_KEY, pull);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}


	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getRemoteOrgId() {
		return remoteOrgId;
	}
	public void setRemoteOrgId(int remoteOrgId) {
		this.remoteOrgId = remoteOrgId;
	}

	public String getAuthkey() {
		return authkey;
	}
	public void setAuthkey(String authkey) {
		this.authkey = authkey;
	}

	public boolean isPush() {
		return push;
	}
	public void setPush(boolean push) {
		this.push = push;
	}

	public boolean isPull() {
		return pull;
	}
	public void setPull(boolean pull) {
		this.pull = pull;
	}

}
