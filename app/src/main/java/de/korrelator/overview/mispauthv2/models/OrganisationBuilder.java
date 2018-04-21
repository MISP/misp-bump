package de.korrelator.overview.mispauthv2.models;

public class OrganisationBuilder {

    private String name = "Default Org";
    private boolean local = true;
    private String description = "";
    private String nationality = "";
    private String sector = "";
    private String uuid = "";

    public OrganisationBuilder() {}

    public Organisation build(){
        return new Organisation(name, local, description, nationality, sector, uuid);
    }

    public OrganisationBuilder name(String name){
        this.name = name;
        return this;
    }

    public OrganisationBuilder local(boolean local){
        this.local = local;
        return this;
    }

    public OrganisationBuilder description(String description){
        this.description = description;
        return this;
    }

    public OrganisationBuilder nationality(String nationality){
        this.nationality = nationality;
        return this;
    }

    public OrganisationBuilder sector(String sector){
        this.sector = sector;
        return this;
    }

    public OrganisationBuilder uuid(String uuid){
        this.uuid = uuid;
        return this;
    }
}
