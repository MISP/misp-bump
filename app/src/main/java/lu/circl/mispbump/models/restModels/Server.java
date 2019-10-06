package lu.circl.mispbump.models.restModels;


import androidx.annotation.NonNull;


public class Server {

    private Integer id;
    private String name;
    private String url;
    private String authkey;
    private Integer org_id;
    private Boolean push = false;
    private Boolean pull = false;
    private Object lastpulledid;
    private Object lastpushedid;
    private Object organization;
    private Integer remote_org_id;
    private Boolean publish_without_email = false;
    private Boolean unpublish_event = false;
    private Boolean self_signed = false;
    private String pull_rules;
    private String push_rules;
    private Object cert_file;
    private Object client_cert_file;
    private Boolean internal;
    private Boolean skip_proxy;
    private Boolean caching_enabled = false;
    private Boolean cache_timestamp;


    public Server(String url) {
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthkey() {
        return authkey;
    }

    public void setAuthkey(String authkey) {
        this.authkey = authkey;
    }

    public Integer getOrgId() {
        return org_id;
    }

    public void setOrgId(Integer org_id) {
        this.org_id = org_id;
    }

    public Boolean getPush() {
        return push;
    }

    public void setPush(Boolean push) {
        this.push = push;
    }

    public Boolean getPull() {
        return pull;
    }

    public void setPull(Boolean pull) {
        this.pull = pull;
    }

    public Object getLastpulledId() {
        return lastpulledid;
    }

    public void setLastpulledId(Object lastpulledid) {
        this.lastpulledid = lastpulledid;
    }

    public Object getLastpushedId() {
        return lastpushedid;
    }

    public void setLastpushedId(Object lastpushedid) {
        this.lastpushedid = lastpushedid;
    }

    public Object getOrganization() {
        return organization;
    }

    public void setOrganization(Object organization) {
        this.organization = organization;
    }

    public Integer getRemoteOrgId() {
        return remote_org_id;
    }

    public void setRemoteOrgId(Integer remote_org_id) {
        this.remote_org_id = remote_org_id;
    }

    public Boolean getPublishWithoutEmail() {
        return publish_without_email;
    }

    public void setPublishWithoutEmail(Boolean publish_without_email) {
        this.publish_without_email = publish_without_email;
    }

    public Boolean getUnpublishEvent() {
        return unpublish_event;
    }

    public void setUnpublishEvent(Boolean unpublish_event) {
        this.unpublish_event = unpublish_event;
    }

    public Boolean getSelfSigned() {
        return self_signed;
    }

    public void setSelfSigned(Boolean self_signed) {
        this.self_signed = self_signed;
    }

    public String getPullRules() {
        return pull_rules;
    }

    public void setPullRules(String pull_rules) {
        this.pull_rules = pull_rules;
    }

    public String getPushRules() {
        return push_rules;
    }

    public void setPushRules(String push_rules) {
        this.push_rules = push_rules;
    }

    public Object getCertFile() {
        return cert_file;
    }

    public void setCertFile(Object cert_file) {
        this.cert_file = cert_file;
    }

    public Object getClientCertFile() {
        return client_cert_file;
    }

    public void setClientCertFile(Object client_cert_file) {
        this.client_cert_file = client_cert_file;
    }

    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public Boolean getSkipProxy() {
        return skip_proxy;
    }

    public void setSkipProxy(Boolean skip_proxy) {
        this.skip_proxy = skip_proxy;
    }

    public Boolean getCachingEnabled() {
        return caching_enabled;
    }

    public void setCachingEnabled(Boolean caching_enabled) {
        this.caching_enabled = caching_enabled;
    }

    public Boolean getCacheTimestamp() {
        return cache_timestamp;
    }

    public void setCacheTimestamp(Boolean cache_timestamp) {
        this.cache_timestamp = cache_timestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "Server: \n" +
                "\t id = " + id + '\n' +
                "\t name = " + name + '\n' +
                "\t url = " + url + '\n' +
                "\t authkey = " + authkey + '\n' +
                "\t org_id = " + org_id + '\n' +
                "\t push = " + push + '\n' +
                "\t pull = " + pull + '\n' +
                "\t lastpulledid = " + lastpulledid + '\n' +
                "\t lastpushedid = " + lastpushedid + '\n' +
                "\t organization = " + organization + '\n' +
                "\t remote_org_id = " + remote_org_id + '\n' +
                "\t publish_without_email = " + publish_without_email + '\n' +
                "\t unpublish_event = " + unpublish_event + '\n' +
                "\t self_signed = " + self_signed + '\n' +
                "\t pull_rules = " + pull_rules + '\n' +
                "\t push_rules = " + push_rules + '\n' +
                "\t cert_file = " + cert_file + '\n' +
                "\t client_cert_file = " + client_cert_file + '\n' +
                "\t internal = " + internal + '\n' +
                "\t skip_proxy = " + skip_proxy + '\n' +
                "\t caching_enabled = " + caching_enabled + '\n' +
                "\t cache_timestamp = " + cache_timestamp;
    }
}
