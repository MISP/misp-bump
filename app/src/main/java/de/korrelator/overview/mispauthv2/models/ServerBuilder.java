package de.korrelator.overview.mispauthv2.models;

public class ServerBuilder {

    private String url = "";
    private String name = "";
    private String organisationType = "";
    private String authKey = "";

    public ServerBuilder() {}

    public Server build(){
        return new Server(url, name, organisationType, authKey);
    }

    public ServerBuilder url(String url){
        this.url = url;
        return this;
    }

    public ServerBuilder name(String name){
        this.name = name;
        return this;
    }

    public ServerBuilder organisationType(String organisationType){
        this.organisationType = organisationType;
        return this;
    }

    public ServerBuilder authKey(String authKey){
        this.authKey = authKey;
        return this;
    }
}
