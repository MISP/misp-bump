# Screenshots

## Login

![Login](./mispbump-login.png)

## Home

Actions: **Profile View** (Menubar) and **New Sync** (Floating Action Button)

![Home (Empty)](./mispbump-home-0.png)

## Profile
Organisation information loaded automatically from your MISP instance

Actions: **Delete and logout** (Menubar) and **Update Info** (Floating Action Button)

![Profile](./mispbump-profile.png)

## Sync

Core functionality of MISPbump.

**First Step:** Exchange keys to derive a shared secret

![Profile](./mispbump-sync-0.png)
![Profile](./mispbump-sync-1.png)

**Second Step:** Exchange encrypted sync information

![Profile](./mispbump-sync-2.png)
![Profile](./mispbump-sync-3.png)

## Sync information

After a successfull exchange an entry for this organisation will appear.

Actions: **Delete Sync information** (Menubar) and **Upload** (Floating Action Button in settings tab)

**Credentials:** With these credentials you will be able to log in on the other MISP instance (SyncUser)

![Profile](./mispbump-sync-info-credentials.png)

**Settings:** These are typical settings which are also available from the MISP web interface.

![Profile](./mispbump-sync-info-settings.png)

## Upload

Shows the status of the upload to your MISP instance.
If errors occure they will be displayed and the user can restart the process.

![Profile](./mispbump-upload-1.png)
![Profile](./mispbump-upload-2.png)

## Home with successfull sync

![Home (Synced)](./mispbump-home.png)