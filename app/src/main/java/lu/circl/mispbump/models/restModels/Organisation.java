package lu.circl.mispbump.models.restModels;


import androidx.annotation.NonNull;


/**
 * Information gathered from Misp API about a organisation.
 */
public class Organisation {

    private Integer id;
    private String name;
    private String date_created;
    private String date_modified;
    private String type;
    private String nationality;
    private String sector;
    private String contacts;
    private String description;
    private Boolean local;
    private String uuid;
//    private String[] restricted_to_domain;
    private String created_by;
    private Integer user_count;

    public Organisation() {
    }

    public Organisation toSyncOrganisation() {
        Organisation organisation = new Organisation();
        organisation.local = true;  // TODO REMOVE FROME HERE!
        organisation.name = name;
        organisation.uuid = uuid;
        organisation.description = description;
        organisation.nationality = nationality;
        organisation.sector = sector;
        organisation.type = "Sync organisation";
        organisation.contacts = contacts;

        return organisation;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getLocal() {
        return local;
    }

    public void setLocal(Boolean local) {
        this.local = local;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

//    public String[] getRestricted_to_domain() {
//        return restricted_to_domain;
//    }

//    public void setRestricted_to_domain(String[] restricted_to_domain) {
//        this.restricted_to_domain = restricted_to_domain;
//    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Integer getUser_count() {
        return user_count;
    }

    public void setUser_count(Integer user_count) {
        this.user_count = user_count;
    }

    @NonNull
    @Override
    public String toString() {
        return "Organisation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date_created='" + date_created + '\'' +
                ", date_modified='" + date_modified + '\'' +
                ", type='" + type + '\'' +
                ", nationality='" + nationality + '\'' +
                ", sector='" + sector + '\'' +
                ", contacts='" + contacts + '\'' +
                ", description='" + description + '\'' +
                ", local=" + local +
                ", uuid='" + uuid + '\'' +
//                ", restricted_to_domain='" + Arrays.toString(restricted_to_domain) + '\'' +
                ", created_by='" + created_by + '\'' +
                ", user_count=" + user_count +
                '}';
    }
}
