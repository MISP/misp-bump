package de.overview.wg.its.mispauth.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Organisation {

	public static final String ROOT_KEY = "Organisation";

	private static String ID_KEY = "id";
	private static String NAME_KEY = "name";
	private static String DATE_CREATED_KEY = "date_created";
	private static String DATE_MODIFIED_KEY = "date_modified";
	private static String TYPE_KEY = "type";
	private static String NATIONALITY_KEY = "nationality";
	private static String SECTOR_KEY = "sector";
	private static String CONTACTS_KEY = "contacts";
	private static String DESCRIPTION_KEY = "description";
	private static String LOCAL_KEY = "local";
	private static String UUID_KEY = "uuid";
	private static String RESTRICTED_TO_DOMAIN_KEY = "restricted_to_domain";
	private static String CREATED_BY_KEY = "created_by";
	private static String USER_COUNT_KEY = "user_count";

	private int id;
	private String name;
	private String dateCreated, dateModified;
	private String type;
	private String nationality;
	private String sector;
	private String contacts;
	private String description;
	private boolean local;
	private String uuid;
	private String restrictedToDomain;
	private int createdBy;
	private int userCount;

	public Organisation() {}

	public Organisation(JSONObject json) throws JSONException {
		fromJSON(json);
	}
	public void fromJSON(JSONObject org) throws JSONException {

		id = org.optInt(ID_KEY, -1);
		dateCreated = org.optString(DATE_CREATED_KEY);
		dateModified = org.optString(DATE_MODIFIED_KEY);
		name = org.optString(NAME_KEY);
		type = org.optString(TYPE_KEY);
		nationality = org.optString(NATIONALITY_KEY);
		sector = org.optString(SECTOR_KEY);
		contacts = org.optString(CONTACTS_KEY);
		description = org.optString(DESCRIPTION_KEY);
		local = org.optBoolean(LOCAL_KEY, true);
		uuid = org.optString(UUID_KEY);
		restrictedToDomain = org.optString(RESTRICTED_TO_DOMAIN_KEY);
		createdBy = org.optInt(CREATED_BY_KEY, -1);
		userCount = org.optInt(USER_COUNT_KEY);

	}

	public JSONObject toJSON() {
		return toJSON(false);
	}
	public JSONObject toJSON(boolean minimal) {
		JSONObject org = new JSONObject();

		try {
			org.putOpt(NAME_KEY, name);
			org.putOpt(DESCRIPTION_KEY, description);
			org.putOpt(NATIONALITY_KEY, nationality);
			org.putOpt(SECTOR_KEY, sector);
			org.putOpt(USER_COUNT_KEY, userCount);

			if (!minimal) {
				org.putOpt(ID_KEY, id);
				org.putOpt(UUID_KEY, uuid);
				org.putOpt(TYPE_KEY, type);
				org.putOpt(CONTACTS_KEY, contacts);
				org.putOpt(DATE_CREATED_KEY, dateCreated);
				org.putOpt(DATE_MODIFIED_KEY, dateModified);
				org.putOpt(LOCAL_KEY, local);
				org.putOpt(RESTRICTED_TO_DOMAIN_KEY, restrictedToDomain);
				org.putOpt(CREATED_BY_KEY, createdBy);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return org;
	}


	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getContacts() {
		return contacts;
	}
	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public boolean isLocal() {
		return local;
	}
	public void setLocal(boolean local) {
		this.local = local;
	}

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getRestrictedToDomain() {
		return restrictedToDomain;
	}
	public void setRestrictedToDomain(String restrictedToDomain) {
		this.restrictedToDomain = restrictedToDomain;
	}

	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getUserCount() {
		return userCount;
	}
	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}
}
