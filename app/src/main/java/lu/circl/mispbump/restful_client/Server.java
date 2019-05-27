package lu.circl.mispbump.restful_client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Server {

    public Server(String name, String url, String authkey, Integer remote_org_id) {
        this.name = name;
        this.url = url;
        this.authkey = authkey;
        this.remote_org_id = remote_org_id;
    }

    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("url")
    @Expose
    public String url;

    @SerializedName("authkey")
    @Expose
    public String authkey;

    @SerializedName("org_id")
    @Expose
    public Integer org_id;

    @SerializedName("push")
    @Expose
    public Boolean push;

    @SerializedName("pull")
    @Expose
    public Boolean pull;

    @SerializedName("lastpulledid")
    @Expose
    public Object lastpulledid;

    @SerializedName("lastpushedid")
    @Expose
    public Object lastpushedid;

    @SerializedName("organization")
    @Expose
    public Object organization;

    @SerializedName("remote_org_id")
    @Expose
    public Integer remote_org_id;

    @SerializedName("publish_without_email")
    @Expose
    public Boolean publish_without_email;

    @SerializedName("unpublish_event")
    @Expose
    public Boolean unpublish_event;

    @SerializedName("self_signed")
    @Expose
    public Boolean self_signed;

    @SerializedName("pull_rules")
    @Expose
    public String pull_rules;

    @SerializedName("push_rules")
    @Expose
    public String push_rules;

    @SerializedName("cert_file")
    @Expose
    public Object cert_file;

    @SerializedName("client_cert_file")
    @Expose
    public Object client_cert_file;

    @SerializedName("internal")
    @Expose
    public Boolean internal;

    @SerializedName("skip_proxy")
    @Expose
    public Boolean skip_proxy;

    @SerializedName("caching_enabled")
    @Expose
    public Boolean caching_enabled;

    @SerializedName("cache_timestamp")
    @Expose
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
