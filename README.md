# MISPBump

With MISPBump it is easy to share events on your MISP instance with other instances. Instead of generating organisations, sync-users and sync-servers you scan only two QR-Codes and the job is done.

# Security

A key agreement is realized with Diffie Hellman (Elliptic Curve 256 Bit), sensible data is encrypted with AES.  

# How does it work?

1. Gather your organisation information from your MISP instance
![Gather Information](./Screenshots/sync-profile.png)

1. Scan your partners generated public key and at the same time share yours
![Scan Public Key](./Screenshots/scan-pub-key.png)

2. Validate the public key you scanned
![Public Key Received](./Screenshots/pub-key-received.png)

3. After another scan the information you need to synchronise is securely transmitted to your phone
![Secure Info Received](./Screenshots/org-info-received.png)

4. Upload the information to your own MISP instance
![Upload](./Screenshots/upload.png)

5. That's it! You are ready to share events across your instances
![Main Screen](./Screenshots/main.png)
