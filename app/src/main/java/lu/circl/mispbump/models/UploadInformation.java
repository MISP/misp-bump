package lu.circl.mispbump.models;

public class UploadInformation {

    public enum SyncStatus {
        COMPLETE,
        FAILURE,
        PENDING
    }

    public SyncStatus currentSyncStatus = SyncStatus.PENDING;
    public SyncInformation local;
    public SyncInformation remote;

    public UploadInformation() {
    }

    public UploadInformation(SyncInformation local) {
        this.local = local;
    }

    public UploadInformation(SyncInformation local, SyncInformation remote) {
        this.local = local;
        this.remote = remote;
    }

}
