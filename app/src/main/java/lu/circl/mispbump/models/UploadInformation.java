package lu.circl.mispbump.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UploadInformation {

    public enum SyncStatus {
        COMPLETE,
        FAILURE,
        PENDING
    }

    private SyncStatus currentSyncStatus = SyncStatus.PENDING;

    private SyncInformation local;
    private SyncInformation remote;

    private Date date;

    public UploadInformation() {
        this(null, null);
    }

    public UploadInformation(SyncInformation local) {
        this(local, null);
    }

    public UploadInformation(SyncInformation local, SyncInformation remote) {
        date = Calendar.getInstance().getTime();

        this.local = local;
        this.remote = remote;

        date = Calendar.getInstance().getTime();
    }

    // getter and setter

    public void setCurrentSyncStatus(SyncStatus status) {
        currentSyncStatus = status;
    }
    public SyncStatus getCurrentSyncStatus() {
        return currentSyncStatus;
    }

    public void setLocal(SyncInformation local) {
        this.local = local;
    }
    public SyncInformation getLocal() {
        return local;
    }

    public void setRemote(SyncInformation remote) {
        this.remote = remote;
    }
    public SyncInformation getRemote() {
        return remote;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public Date getDate() {
        return date;
    }
    public String getDateString() {
        SimpleDateFormat df = new SimpleDateFormat("dd.mm.yyyy", Locale.getDefault());
        return df.format(date);
    }

}
