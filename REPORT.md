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
