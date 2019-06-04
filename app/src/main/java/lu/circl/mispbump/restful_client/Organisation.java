package lu.circl.mispbump.restful_client;

/**
 * Information gathered from Misp API about a organisation.
 */
public class Organisation {

    public Organisation() {
    }

    public Organisation(String name) {
        this.name = name;
    }

    public Organisation(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer id;
    public String name;
    public String date_created;
    public String date_modified;
    public String type;
    public String nationality;
    public String sector;
    public String contacts;
    public String description;
    public Boolean local;
    public String uuid;
    public String restricted_to_domain;
    public String created_by;
    public Integer user_count;

    public Organisation syncOrganisation() {
        Organisation organisation = new Organisation();
        organisation.local = true;
        organisation.name = name;
        organisation.uuid = uuid;
        organisation.description = description;
        organisation.nationality = nationality;
        organisation.sector = sector;
        organisation.type = "Sync organisation";
        organisation.contacts = contacts;

        return organisation;
    }

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
                ", restricted_to_domain='" + restricted_to_domain + '\'' +
                ", created_by='" + created_by + '\'' +
                ", user_count=" + user_count +
                '}';
    }
}
