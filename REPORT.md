---
title: NWEN303 Project 2 (Dylan Chong - 300373593)
geometry: margin=2.5cm
---

# Task 1

## Echo part

Below is a diff of what code was changed. For a coloured version, visit this
link:
https://github.com/dylan-chong/towers-of-hanoi/commit/fcc50f447b38ac8e61233681e59485fb9a9432da.

```diff
    diff --git a/src/main/java/Connection.java b/src/main/java/Connection.java
    index 410626d..c833524 100644
    --- a/src/main/java/Connection.java
    +++ b/src/main/java/Connection.java
    @@ -46,12 +46,15 @@ public class Connection extends Thread {
             if ((line == null) || line.equals("bye")) {
               break;
             }
    +
             if (line.equals("Knock, knock")) {
               response = "Who's there?";
    -        } else if (line.equals("Canoe")) {
    -          response = "Canoe who?";
    -        } else if (line.equals("Canoe do my homework?")) {
    +        } else if (line.equals("Owls say")) {
    +          response = "Owls say who?";
    +        } else if (line.equals("Yes, they do")) {
               response = "<<<<groan>>>>";
    +        } else if (line.equals("Very bad, I know")) {
    +          response = "You should leave or find better jokes";
             }
     
             // send the response plus a return and newlines (as expected by readLine)
    diff --git a/src/main/java/EchoClient.java b/src/main/java/EchoClient.java
    index 77e2355..af0bf73 100644
    --- a/src/main/java/EchoClient.java
    +++ b/src/main/java/EchoClient.java
    @@ -33,9 +33,11 @@ public class EchoClient {
           // send a sequence of messages and print the replies
           out.println("Knock, knock");
           System.out.println(in.readLine());
    -      out.println("Canoe");
    +      out.println("Owls say");
           System.out.println(in.readLine());
    -      out.println("Canoe do my homework?");
    +      out.println("Yes, they do");
    +      System.out.println(in.readLine());
    +      out.println("Very bad, I know");
           System.out.println(in.readLine());
           out.println("bye");
         } catch (IOException ioe) {
```

Server output:

```
    Waiting for connections on 55177
    Client 1: Knock, knock
    Server: Who's there?
    Client 1: Owls say
    Server: Owls say who?
    Client 1: Yes, they do
    Server: <<<<groan>>>>
    Client 1: Very bad, I know
    Server: You should leave or find better jokes
    Client 1: bye
```

Client output:

```
    Who's there?
    Owls say who?
    <<<<groan>>>>
    You should leave or find better jokes
```

## Search part

Below is a diff of what code was changed. For a coloured version, visit this
link:
https://github.com/dylan-chong/towers-of-hanoi/commit/cd6d1965d3d2629291e94a30be8a568841b1ba14.

```diff
    diff --git a/src/main/java/Search.java b/src/main/java/Search.java
    index 32e9439..d12175d 100644
    --- a/src/main/java/Search.java
    +++ b/src/main/java/Search.java
    @@ -16,6 +16,7 @@ public class Search {
        * @param args[0] key represented as a big integer value
        * @param args[1] key size
        * @param args[2] ciphertext encoded as base64 value
    +   * @param args[3] number of keys to check
        */
       public static void main(String[] args) throws Exception {
     
    @@ -25,11 +26,12 @@ public class Search {
         int keySize = Integer.parseInt(args[1]);
         byte[] key = Blowfish.asByteArray(bi, keySize);
         byte[] ciphertext = Blowfish.fromBase64(args[2]);
    +    int numberOfKeysToCheck = Integer.parseInt(args[3]);
     
         // Go into a loop where we try a range of keys starting at the given one
         String plaintext = null;
         // Search from the key that will give us our desired ciphertext
    -    for (int i = 0; i < 100; i++) {
    +    for (int i = 0; i < numberOfKeysToCheck; i++) {
           // tell user which key is being checked
           String keyStr = bi.toString();
           System.out.print(keyStr);
```

Console output:

```
    $ java Search 3185209670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng== 1000

    3185209670
    3185209671

    3185209672
    3185209673

    3185209674
    3185209675
    3185209676
    3185209677
    3185209678
    3185209679
    3185209680
    Plaintext found!
    May good flourish; Kia hua ko te pai
    key is (hex) BDDA7150 3185209680
```

# Task 2

## Outline

There will be two main types of nodes on the network --- the key manager, and
the clients. The user will have to manually start the key manager, read the
port of the key manager, and start the clients.

The key manager will have a thread that will continuously listen to any input
from clients on it's socket. The input will either be: 

1. A message from a client saying that it is ready to accept new instructions,
   along with it's IP address and port and desired chunk size
2. A result from a client stating the key range, and a valid key if it found
   one.

If the manager receives the first kind of message, it will dispatch a key range
to search to the given IP and port. The client will then search this range and
send the second kind of response back. The client will disconnect from the
manager after receiving the instructions, and then will reconnect to send the
response --- this is to avoid overloading the manager with too many
connections.

When the manager receives the second kind of message and the key is found, the
manager will print out a success message to standard output and exit, leaving
the clients to finish their work and timeout sending a message to the manager.
A response back to the client is not required because the clients will request
more work after having sent the second kind of message to the key manager.

Note: I have intentionally made the key manager single threaded because the
implementation is simpler. Even on a slow network, having only one possible
simultaneous connection barely has any effect on the performance --- as long as
the chunk size for the clients is decently large, for example 100,000.

## Requirements

The above outline describes a design that follows the requirements described in
the `Project2.pdf`:

1. Clients only need to be aware of the location of the key manager --- the
   above outline only requires connections between each client and the manager.
2. Clients can join or leave but will complete the work they have been
   requested --- clients will be able to join at any time, and leave at any
   time by simply not asking for more work to do.
3. Clients request work from the key manager and return results to it --- the
   client lets the key manager know when it can do some work and how large of a
   range it wants to do, even though the key manager decides what work it
   should do.
4. Connections between clients and the Masters only exist long enough to
   request work or to return results --- the clients disconnect from the
   manager after receiving instructions, although they reconnect to send the
   response.
5. When the key is found, the key manager will shutdown --- the key manager
   will halt when it receives a message that the key is found, as described in
   the outline.

# Task 3

## Testing process

To test the key managers and clients together, I run the following code.
Appropriate inputs and outputs should be displayed in each of the terminals.
One of the clients should reach the correct key (3185209680) within roughly 30
seconds, and then exit. The key manager should then display the correct key and
exit.

```bash
    # Run this on the first terminal
    ./gradlew compileJava
    cd build/classes/java/main/
    java KeyManager 3184309670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng== 57842 true

    # Run this in 3 new terminals
    java Client localhost 57842 100000
```

Example output (`KeyManager`):

```
    > java KeyManager 3184309670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng== 57843 true
    Waiting for connections on 57843
    Input: ASK 127.0.0.1 60326 100000
    Output: 3184309670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    Input: ASK 127.0.0.1 60327 100000
    Output: 3184409670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    Input: ASK 127.0.0.1 60328 100000
    Output: 3184509670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    Input: RESPONSE 127.0.0.1 60329
    Input: ASK 127.0.0.1 60330 100000
    Output: 3184609670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    Input: RESPONSE 127.0.0.1 60331
    Input: ASK 127.0.0.1 60332 100000
    Output: 3184709670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    Input: RESPONSE 127.0.0.1 60333
    Input: ASK 127.0.0.1 60334 100000
    Output: 3184809670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    Input: RESPONSE 127.0.0.1 60335
    Input: ASK 127.0.0.1 60336 100000
    Output: 3184909670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    Input: RESPONSE 127.0.0.1 60337
    Input: ASK 127.0.0.1 60338 100000
    Output: 3185009670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    Input: RESPONSE 127.0.0.1 60339
    Input: ASK 127.0.0.1 60340 100000
    Output: 3185109670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    Input: RESPONSE 127.0.0.1 60341
    Input: ASK 127.0.0.1 60342 100000
    Output: 3185209670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    Input: RESPONSE 127.0.0.1 60343 3185209680
    Correct key: 3185209680
```

Example output (one of the 3 `Client`s):

```
    > java Client localhost 57843 100000 && sleep 3
    Output: ASK 127.0.0.1 60696 100000
    Input: 3184309670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    ....................................................................................................
    Output: RESPONSE 127.0.0.1 60699
    Output: ASK 127.0.0.1 60700 100000
    Input: 3184609670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    ....................................................................................................
    Output: RESPONSE 127.0.0.1 60706
    Output: ASK 127.0.0.1 60707 100000
    Input: 3184909670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    ....................................................................................................
    Output: RESPONSE 127.0.0.1 60713
    Output: ASK 127.0.0.1 60714 100000
    Input: 3185209670 4 +UHC88LxQEgKq6BmdGo31UtE5HqTimlZssAZMXqSXXXT7NJLc52Fng==
    .
    Plaintext found!
    May good flourish; Kia hua ko te pai
    key is (hex) BDDA7150 3185209680
    Output: RESPONSE 127.0.0.1 60715 3185209680
```

The output of the other two clients are not shown here for brevity. Note that
these clients show an IO exception for not being able to connect to the key
manager after it has received the correct key. This is because the manager
shuts down, and the clients do not maintain a connection with the manager (as
per the requirements) so there is no way for the remaining clients to know that
the key manager has successfully been given a solution.

The code was also inspected to make sure it is readable, understandable, and
follows the requirements.

# Task 5

NOTE: I ran this on a dual core, hyper-threaded CPU (i5-5257U) using the script
`./run-benchmarks.sh`.

| Chunk Size   | Number Of Clients   | --- | Average Run Time (of 30 runs)   | Standard Deviation   |
| ------------ | ------------------- | --- | ------------------------------- | -------------------- |
| 1000         | 3                   |     | 7784.0                          | 507.59012336070265   |
| 10000        | 3                   |     |
| 100000       | 3                   |     |
| 1000         | 2                   |     |
| 10000        | 2                   |     |
| 100000       | 2                   |     |
