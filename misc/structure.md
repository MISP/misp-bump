# Information in encrypted QR code (on MISP A)
## Organisation
+ Identifier (Organisation B on MISP A)
+ UUID (of organisation B)
+ description
+ Nationality
+ Sector
+ type (freetext)
+ (contacts?)

## SyncUser
+ email (orgb.syncuser@mispa.test)
+ password (abcdefghijklmnop) (16 chars but depends on settings)

## SyncServer
+ base url
+ instance name
+ authkey from syncUser


## Steps

1. each instance generates a foreign organisation
2. each instance generates a syncuser
3. each instance generates a sync server