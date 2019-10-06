package lu.circl.mispbump.models.restModels;


import com.google.gson.annotations.SerializedName;


public class Role {
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("created")
    private String created;
    @SerializedName("modified")
    private String modified;
    @SerializedName("perm_add")
    private Boolean permAdd;
    @SerializedName("perm_modify")
    private Boolean permModify;
    @SerializedName("perm_modify_org")
    private Boolean permModifyOrg;
    @SerializedName("perm_publish")
    private Boolean permPublish;
    @SerializedName("perm_delegate")
    private Boolean permDelegate;
    @SerializedName("perm_sync")
    private Boolean permSync;
    @SerializedName("perm_admin")
    private Boolean permAdmin;
    @SerializedName("perm_audit")
    private Boolean permAudit;
    @SerializedName("perm_auth")
    private Boolean permAuth;
    @SerializedName("perm_site_admin")
    private Boolean permSiteAdmin;
    @SerializedName("perm_regexp_access")
    private Boolean permRegexpAccess;
    @SerializedName("perm_tagger")
    private Boolean permTagger;
    @SerializedName("perm_template")
    private Boolean permTemplate;
    @SerializedName("perm_sharing_group")
    private Boolean permSharingGroup;
    @SerializedName("perm_tag_editor")
    private Boolean permTagEditor;
    @SerializedName("perm_sighting")
    private Boolean permSighting;
    @SerializedName("perm_object_template")
    private Boolean permObjectTemplate;
    @SerializedName("default_role")
    private Boolean defaultRole;
    @SerializedName("memory_limit")
    private String memoryLimit;
    @SerializedName("max_execution_time")
    private String maxExecutionTime;
    @SerializedName("restricted_to_site_admin")
    private Boolean restrictedToSiteAdmin;
    @SerializedName("perm_publish_zmq")
    private Boolean permPublishZmq;
    @SerializedName("perm_publish_kafka")
    private Boolean permPublishKafka;
    @SerializedName("permission")
    private String permission;
    @SerializedName("permission_description")
    private String permissionDescription;


    public boolean isSyncUserRole() {
        return permSync && permAuth && permTagger && permTagEditor && permSharingGroup
                && permDelegate && permSighting && permPublishZmq && permPublishKafka;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public Boolean getPermAdd() {
        return permAdd;
    }

    public void setPermAdd(Boolean permAdd) {
        this.permAdd = permAdd;
    }

    public Boolean getPermModify() {
        return permModify;
    }

    public void setPermModify(Boolean permModify) {
        this.permModify = permModify;
    }

    public Boolean getPermModifyOrg() {
        return permModifyOrg;
    }

    public void setPermModifyOrg(Boolean permModifyOrg) {
        this.permModifyOrg = permModifyOrg;
    }

    public Boolean getPermPublish() {
        return permPublish;
    }

    public void setPermPublish(Boolean permPublish) {
        this.permPublish = permPublish;
    }

    public Boolean getPermDelegate() {
        return permDelegate;
    }

    public void setPermDelegate(Boolean permDelegate) {
        this.permDelegate = permDelegate;
    }

    public Boolean getPermSync() {
        return permSync;
    }

    public void setPermSync(Boolean permSync) {
        this.permSync = permSync;
    }

    public Boolean getPermAdmin() {
        return permAdmin;
    }

    public void setPermAdmin(Boolean permAdmin) {
        this.permAdmin = permAdmin;
    }

    public Boolean getPermAudit() {
        return permAudit;
    }

    public void setPermAudit(Boolean permAudit) {
        this.permAudit = permAudit;
    }

    public Boolean getPermAuth() {
        return permAuth;
    }

    public void setPermAuth(Boolean permAuth) {
        this.permAuth = permAuth;
    }

    public Boolean getPermSiteAdmin() {
        return permSiteAdmin;
    }

    public void setPermSiteAdmin(Boolean permSiteAdmin) {
        this.permSiteAdmin = permSiteAdmin;
    }

    public Boolean getPermRegexpAccess() {
        return permRegexpAccess;
    }

    public void setPermRegexpAccess(Boolean permRegexpAccess) {
        this.permRegexpAccess = permRegexpAccess;
    }

    public Boolean getPermTagger() {
        return permTagger;
    }

    public void setPermTagger(Boolean permTagger) {
        this.permTagger = permTagger;
    }

    public Boolean getPermTemplate() {
        return permTemplate;
    }

    public void setPermTemplate(Boolean permTemplate) {
        this.permTemplate = permTemplate;
    }

    public Boolean getPermSharingGroup() {
        return permSharingGroup;
    }

    public void setPermSharingGroup(Boolean permSharingGroup) {
        this.permSharingGroup = permSharingGroup;
    }

    public Boolean getPermTagEditor() {
        return permTagEditor;
    }

    public void setPermTagEditor(Boolean permTagEditor) {
        this.permTagEditor = permTagEditor;
    }

    public Boolean getPermSighting() {
        return permSighting;
    }

    public void setPermSighting(Boolean permSighting) {
        this.permSighting = permSighting;
    }

    public Boolean getPermObjectTemplate() {
        return permObjectTemplate;
    }

    public void setPermObjectTemplate(Boolean permObjectTemplate) {
        this.permObjectTemplate = permObjectTemplate;
    }

    public Boolean getDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(Boolean defaultRole) {
        this.defaultRole = defaultRole;
    }

    public String getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(String memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public String getMaxExecutionTime() {
        return maxExecutionTime;
    }

    public void setMaxExecutionTime(String maxExecutionTime) {
        this.maxExecutionTime = maxExecutionTime;
    }

    public Boolean getRestrictedToSiteAdmin() {
        return restrictedToSiteAdmin;
    }

    public void setRestrictedToSiteAdmin(Boolean restrictedToSiteAdmin) {
        this.restrictedToSiteAdmin = restrictedToSiteAdmin;
    }

    public Boolean getPermPublishZmq() {
        return permPublishZmq;
    }

    public void setPermPublishZmq(Boolean permPublishZmq) {
        this.permPublishZmq = permPublishZmq;
    }

    public Boolean getPermPublishKafka() {
        return permPublishKafka;
    }

    public void setPermPublishKafka(Boolean permPublishKafka) {
        this.permPublishKafka = permPublishKafka;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPermissionDescription() {
        return permissionDescription;
    }

    public void setPermissionDescription(String permissionDescription) {
        this.permissionDescription = permissionDescription;
    }
}
