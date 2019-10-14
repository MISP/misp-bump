# MISPbump
Simple and secure synchronisation of MISP instances

# What is MISPbump?
With MISPbump admins can easily synchronize MISP instances by exchanging relevant information via encrypted QR codes.

> Note that only **use case 1** from the [documentation](https://www.circl.lu/doc/misp/sharing/) is supported.

# How does MISPbump work?
MISP admins log in by providing the **base URL** of their instance and their **authkey**.

After a successfull login the admin's profile and the linked organisation information will be downloaded.  

In the main screen you can start a synchronisation process by pressing the dedicated button.

The synchronisation process consists of 3 steps:
1. **Key Exchange**  
    To provide a secure chanel for data exchange, the first step is to generate a shared secret with [Diffie–Hellman key exchange](https://en.wikipedia.org/wiki/Diffie%E2%80%93Hellman_key_exchange) ([Elliptic Curve](https://en.wikipedia.org/wiki/Elliptic-curve_Diffie%E2%80%93Hellman)).

    Public keys are exchanged via QR code.

1. **Synchronisation Information Exchange**  
    Contains the following information:
    + Own Organisation: Name, UUID, description, nationality, sector, type and contacts
    + Own User: Email
    + Own MISP instance: base URL
    + Generated: sync user authkey, sync user password  
        (your partner will create a sync user with these credentials for you)

    The synchronisation information is encrypted with AES using the shared secret (from step 1).

    The synchronisation process information will be saved securely on the device.


1. **Upload information to own MISP instance**  
    Uploading the information to the MISP instance is accomplished with MISP's REST API.

    Uploading consists of the following steps:
    1. Create organisation
    1. Create sync user & add to organisation
    1. Create sync server & populate with information above

After that the two MISP instances are able to share Events based on their permissions.

# Dependencies
+ [Retrofit](https://github.com/square/retrofit)
+ [ZXing](https://github.com/zxing/zxing)
