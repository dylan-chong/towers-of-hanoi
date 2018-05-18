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


