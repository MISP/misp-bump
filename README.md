![MispBump Logo](./images/mispbump.svg?sanitize=true)

# MISPBump

With MISPBump it is easy to synchronise events on different MISP instances. Instead of generating organisations, sync-users and sync-servers you only have to scan two QR-Codes and you are ready for syncing.

# Security

A key agreement is realized with Diffie Hellman (Elliptic Curve 256 Bit), sensible data is encrypted with AES.  

TODO: how are credentials stored in app, keystore?


# How does it work?

1. Gather your organisation information from your MISP instance
![Gather Information](./images/Screenshots/sync-profile.png)

1. Scan your partners generated public key and at the same time share yours
![Scan Public Key](./images/Screenshots/scan-pub-key.png)

2. Validate the public key you scanned
![Public Key Received](./images/Screenshots/pub-key-received.png)

3. After another scan the information you need to synchronise is securely transmitted to your phone
![Secure Info Received](./images/Screenshots/org-info-received.png)

4. Upload the information to your own MISP instance
![Upload](./images/Screenshots/upload.png)

5. That's it! You are ready to share events across your instances
![Main Screen](./images/Screenshots/main.png)
