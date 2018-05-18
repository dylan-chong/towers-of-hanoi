Matt's README
14/05/18


There are a lot of files provided for this project. This should allow you to 
focused on the distributed nature of the work rather than using your valuable 
time on parts that do not directly relate to lectures. So we have supplied the 
following to get your distributed system started and to enable you to encrypt/
decrypt a phrase. This thumbnail description is mine and intended only to help 
you get into the code and initially use it.

Unpack the jar file, first read:
 - Project2.pdf 	// outlines the project requirements and what to submit
 - First tests.pdf 	// provides a couple of example test outputs
 - README           // will get you up and started.

The remaining java files are:

// Communication files - for the initial knock-knock joke 
// which you will modify for your distributed algorithm 
EchoClient.java	// takes a server IP/port and starts the knock-knock Joke
EchoServer.java	// listens for a client connection and calls Connection class
Connection.java	// respond to a connection (initially set up for knock-knock joke)

 ____________                  ____________               ________________
|            |                |            |             |                |
| EchoClient | <------------> | EchoServer | ---calls--- |Connection.java |
|____________|                |____________|             |________________|


// password files - use these to encrypt/decrypt your phrase.
Blowfish.java	// utility program to simplify the use of the Blowfish algorithm
Keygen.java	    // generates a key and encrypts a hard coded phrase
Decrypt.java	// takes a key and cyphertext and generates an output (garbage if the key is incorrect)
Search.java	    // Similar to Decrypt.java but if not successful, will try the next 99 keys as well.

The author of the Blowfish algorithm is Bruce Schneier. Today he is an advocate and commentator on security and privacy issues. https://www.schneier.com/academic/blowfish/
