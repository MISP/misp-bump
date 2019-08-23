package lu.circl.mispbump.models;


import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


/**
 * Class that holds the information needed synchronize two misp instances.
 */
public class SyncInformation {

    private UUID uuid;
    private Date date, lastModified;

    private ExchangeInformation remote;
    private ExchangeInformation local;


    public SyncInformation() {
        uuid = UUID.randomUUID();
        setSyncDate();
    }


    public UUID getUuid() {
        return uuid;
    }
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Date getLastModified() {
        return lastModified;
    }
    public void setLastModified() {
        setLastModified(Calendar.getInstance().getTime());
    }
    public void setLastModified(Date date) {
        lastModified = date;
    }

    public void setSyncDate() {
        setSyncDate(Calendar.getInstance().getTime());
    }
    public void setSyncDate(Date date) {
        this.date = date;
        this.lastModified = date;
    }
    public Date getSyncDate() {
        return date;
    }
    private String getSyncDateString() {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return df.format(date);
    }


    public ExchangeInformation getRemote() {
        return remote;
    }
    public void setRemote(ExchangeInformation remote) {
        this.remote = remote;
    }

    public ExchangeInformation getLocal() {
        return local;
    }
    public void setLocal(ExchangeInformation local) {
        this.local = local;
    }


    @NonNull
    @Override
    public String toString() {
        return "Sync Information: \n" +
                "UUID = " + uuid + "\n" +
                "Sync Date = " + getSyncDateString() + "\n" +
                remote.toString() +
                local.toString();
    }
}
