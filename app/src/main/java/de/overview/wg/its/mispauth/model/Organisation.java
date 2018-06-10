package de.overview.wg.its.mispauth.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Organisation {

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

	public void fromJSON(JSONObject org) {

		try {
			id = org.getInt(ID_KEY);
			dateCreated = org.getString(DATE_CREATED_KEY);
			dateModified = org.getString(DATE_MODIFIED_KEY);
			name = org.getString(NAME_KEY);
			type = org.getString(TYPE_KEY);
			nationality = org.getString(NATIONALITY_KEY);
			sector = org.getString(SECTOR_KEY);
			contacts = org.getString(CONTACTS_KEY);
			description = org.getString(DESCRIPTION_KEY);
			local = org.getBoolean(LOCAL_KEY);
			uuid = org.getString(UUID_KEY);
			restrictedToDomain = org.getString(RESTRICTED_TO_DOMAIN_KEY);
			createdBy = org.getInt(CREATED_BY_KEY);
			userCount = org.getInt(USER_COUNT_KEY);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public JSONObject toJSON() {
		JSONObject org = new JSONObject();

		try {
			org.put(ID_KEY, id);
			org.put(NAME_KEY, name);
			org.put(DATE_CREATED_KEY, dateCreated);
			org.put(DATE_MODIFIED_KEY, dateModified);
			org.put(TYPE_KEY, type);
			org.put(NATIONALITY_KEY, nationality);
			org.put(SECTOR_KEY, sector);
			org.put(CONTACTS_KEY, contacts);
			org.put(DESCRIPTION_KEY, description);
			org.put(LOCAL_KEY, local);
			org.put(UUID_KEY, uuid);
			org.put(RESTRICTED_TO_DOMAIN_KEY, restrictedToDomain);
			org.put(CREATED_BY_KEY, createdBy);
			org.put(USER_COUNT_KEY, userCount);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return org;
	}


	public void setName(String name) {
		this.name = name;
	}
	public String getName(){
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
