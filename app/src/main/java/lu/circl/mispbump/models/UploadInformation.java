package lu.circl.mispbump.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class UploadInformation implements Serializable {

    public enum SyncStatus {
        COMPLETE,
        FAILURE,
        PENDING
    }

    private UUID id;

    private SyncStatus currentSyncStatus = SyncStatus.PENDING;

    private boolean allowSelfSigned, pull, push, cached;

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
        id = UUID.randomUUID();
        date = Calendar.getInstance().getTime();

        this.local = local;
        this.remote = remote;
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

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public void setDate() {
        setDate(Calendar.getInstance().getTime());
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

    public boolean isAllowSelfSigned() {
        return allowSelfSigned;
    }
    public void setAllowSelfSigned(boolean allowSelfSigned) {
        this.allowSelfSigned = allowSelfSigned;
    }

    public boolean isPull() {
        return pull;
    }
    public void setPull(boolean pull) {
        this.pull = pull;
    }

    public boolean isPush() {
        return push;
    }
    public void setPush(boolean push) {
        this.push = push;
    }

    public boolean isCached() {
        return cached;
    }
    public void setCached(boolean cached) {
        this.cached = cached;
    }

    @NonNull
    @Override
    public String toString() {
        return "UploadInformation{" +
                "currentSyncStatus=" + currentSyncStatus +
                ", local=" + local.toString() +
                ", remote=" + remote.toString() +
                ", date=" + date +
                '}';
    }
}
