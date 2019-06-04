package lu.circl.mispbump.restful_client;

import com.google.gson.annotations.SerializedName;

public class Server {

    public Server() {}

    public Server(String name, String url, String authkey, Integer remote_org_id) {
        this.name = name;
        this.url = url;
        this.authkey = authkey;
        this.remote_org_id = remote_org_id;
    }

    @SerializedName("id")
    public Integer id;

    @SerializedName("name")
    public String name;

    @SerializedName("url")
    public String url;

    @SerializedName("authkey")
    public String authkey;

    @SerializedName("org_id")
    public Integer org_id;

    @SerializedName("push")
    public Boolean push;

    @SerializedName("pull")
    public Boolean pull;

    @SerializedName("lastpulledid")
    public Object lastpulledid;

    @SerializedName("lastpushedid")
    public Object lastpushedid;

    @SerializedName("organization")
    public Object organization;

    @SerializedName("remote_org_id")
    public Integer remote_org_id;

    @SerializedName("publish_without_email")
    public Boolean publish_without_email = false;

    @SerializedName("unpublish_event")
    public Boolean unpublish_event;

    @SerializedName("self_signed")
    public Boolean self_signed = false;

    @SerializedName("pull_rules")
    public String pull_rules;

    @SerializedName("push_rules")
    public String push_rules;

    @SerializedName("cert_file")
    public Object cert_file;

    @SerializedName("client_cert_file")
    public Object client_cert_file;

    @SerializedName("internal")
    public Boolean internal;

    @SerializedName("skip_proxy")
    public Boolean skip_proxy = false;

    @SerializedName("caching_enabled")
    public Boolean caching_enabled;

    @SerializedName("cache_timestamp")
    public Boolean cache_timestamp;

    @Override
    public String toString() {
        return "Server{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", authkey='" + authkey + '\'' +
                ", org_id=" + org_id +
                ", push=" + push +
                ", pull=" + pull +
                ", lastpulledid=" + lastpulledid +
                ", lastpushedid=" + lastpushedid +
                ", organization=" + organization +
                ", remote_org_id=" + remote_org_id +
                ", publish_without_email=" + publish_without_email +
                ", unpublish_event=" + unpublish_event +
                ", self_signed=" + self_signed +
                ", pull_rules='" + pull_rules + '\'' +
                ", push_rules='" + push_rules + '\'' +
                ", cert_file=" + cert_file +
                ", client_cert_file=" + client_cert_file +
                ", internal=" + internal +
                ", skip_proxy=" + skip_proxy +
                ", caching_enabled=" + caching_enabled +
                ", cache_timestamp=" + cache_timestamp +
                '}';
    }
}
