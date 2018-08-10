package de.overview.wg.its.mispbump.model;

import org.json.JSONArray;
import org.json.JSONException;

public class SyncInformationQr {

	private Organisation organisation;
	private Server server;
	private User user;

	public SyncInformationQr(Organisation organisation, Server server, User user) {
		this.organisation = organisation;
		this.server = server;
		this.user = user;
	}
	public SyncInformationQr(String stringArray) throws JSONException {
		fromJSON(new JSONArray(stringArray));
	}

	private void fromJSON(JSONArray array) throws JSONException {
		int length = array.length();

		if (length == 3) {
			organisation = new Organisation(array.getJSONObject(0));
			server = new Server(array.getJSONObject(1));
			user = new User(array.getJSONObject(2));
		}
	}

	public JSONArray toJSON() {
		JSONArray array = new JSONArray();

		array.put(organisation.toJSON(true));
		array.put(server.toJSON(true));
		array.put(user.toJSON(true));

		return array;
	}

	public Organisation getOrganisation() {
		return organisation;
	}
	public Server getServer() {
		return  server;
	}
	public User getUser() {
		return user;
	}
}
