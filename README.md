# MISPbump
Simple and secure synchronisation of MISP instances

# What is MISPbump?
With MISPbump admins can easily synchronize MISP instances by exchanging relevant information via encrypted QR codes.

Note: only **use case 1** from the [documentation](https://www.circl.lu/doc/misp/sharing/) is supported.

# How does MISPbump work?
First of all: MISP admins login by providing the base URL of their instance and their authkey (automationkey).

On a successfull login the users profile and the linked organisation information will be downloaded automatically.
This information can be updated at any time from the profile view.

From the main screen you can start a synchronisation process by pressing the dedicated button.

The synchronisation process consists of 3 steps:
1. Key Exchange (unencrypted QR code)
1. Synchronisation Information Exchange (with shared secret encrypted QR code)
1. Upload information to own MISP instance

#### 1. Key Exchange
[Diffie–Hellman key exchange](https://en.wikipedia.org/wiki/Diffie%E2%80%93Hellman_key_exchange) ([Elliptic Curve](https://en.wikipedia.org/wiki/Elliptic-curve_Diffie%E2%80%93Hellman)), where the public part is exchanged via a QR code.
The result is a shared secret which will be used to encrypt the information passed via QR code in step 2.

#### 2. Synchronisation Information Exchange
Local information like Organisation name, UUID, description and User information is encrypted with a from step 1 derived key.
The information can now be securely exchanged via QR code.

#### 3. Upload information to MISP instance
Uploading the information to the MISP instance is accomplished with MISP's REST API.

Uploading consists of the following steps:
1. Create organisation
1. Create Sync User & add to organisation
1. Create Sync Server & populate with information above

After that the two MISP instances are connected.

# Dependencies
+ [Retrofit](https://github.com/square/retrofit)
+ [ZXing](https://github.com/zxing/zxing)