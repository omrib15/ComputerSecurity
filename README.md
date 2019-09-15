# Computer security project

The goal of the project was to create a "Drive"-like software, where clients can manage 
their files on a server in a secure manner. To do so, I wrote both a client and a server in Java. 
The client is a simple GUI using Swing, which allows users to interact with their files on the server (upload a file, download, delete, etc.). 
It is also in charge of encryption/decryption of the files using the AES algorithm, and message authentication using HMAC.
The server runs on tomcat 9.0, and it uses the Jersey framework to expose a RESTful api to the client. 
It persist the users' encrypted files, along with some credentials used for message authentication.
