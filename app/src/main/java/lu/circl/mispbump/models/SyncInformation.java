package lu.circl.mispbump.models;


import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import lu.circl.mispbump.models.restModels.Organisation;
import lu.circl.mispbump.models.restModels.Server;
import lu.circl.mispbump.models.restModels.User;


/**
 * Class that holds the information needed synchronize two misp instances.
 */
public class SyncInformation {

    private UUID uuid;
    private Date date, lastModified;

    @SerializedName("organisation")
    private Organisation remoteOrganisation;
    private User syncUser;
    private Server syncServer;
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

    public Organisation getRemoteOrganisation() {
        return remoteOrganisation;
    }
    public void setRemoteOrganisation(Organisation organisation) {
        this.remoteOrganisation = organisation;
    }

    public User getSyncUser() {
        return syncUser;
    }
    public void setSyncUser(User syncUser) {
        this.syncUser = syncUser;
    }

    public Server getSyncServer() {
        return syncServer;
    }
    public void setSyncServer(Server syncServer) {
        this.syncServer = syncServer;
    }

    public ExchangeInformation getLocal() {
        return local;
    }
    public void setLocal(ExchangeInformation local) {
        this.local = local;
    }


    public void populateRemoteExchangeInformation(ExchangeInformation exchangeInformation) {
        this.remoteOrganisation = exchangeInformation.getOrganisation();
        this.syncUser = exchangeInformation.getSyncUser();
        this.syncServer = exchangeInformation.getServer();
    }


    @NonNull
    @Override
    public String toString() {
        return "Sync Information: \n" +
                "UUID = " + uuid + "\n" +
                "Date = " + getSyncDateString() + "\n" +
                remoteOrganisation.toString() + "\n" +
                syncUser.toString() + "\n" +
                syncServer.toString() + "\n" +
                local.toString();
    }
}
