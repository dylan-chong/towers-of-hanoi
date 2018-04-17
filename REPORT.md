---
title: NWEN303 Project 1
author: Dylan Chong - 300373593
geometry: margin=2.5cm
---

# Task 1

## Part A

The critical section is a part of a program where we must be careful of
simultaneous write and read operations. Specifically, if two threads (both are
writing, or one is writing and the other is reading) access this critical
section at the same/overlapping time, then the program may read the
incomplete/incoherent data and end up in an unexpected state.

The critical section idea will be relevant to any multithreaded program that is
truly parallel (e.g. not Python). That is, the critical section is the shared
place where multiple threads can write data to, and can interfere with each
other. A critical section would not exist if multiple threads read from only
immutable data, as read operations will not interfere with each other. A
critical section would also not exist in a program where  multiple threads can
write data simultaneously to independent places.

If the critical sections in a program are not properly considered, ie not
properly synchronised or otherwise managed, then unexpected behaviour may occur.
For example, one thread could be trying to read a message is a list of strings:
`["We", "must", "protect", "Marco"]`. At the same time, another thread may be
modifying the list to say `["Let's", "go", "kill", "Dylan"]`. It is entirely
possible that the writing thread gets paused after writing the first three
words, and then the reading thread reads all four words. In this case, the
reading thread would see `["Let's", "go", "kill", "Marco"]` - an unexpected /
corrupted message that could cause unexpected chains of events to happen later.
If this list had been synchronised in some way, then the reading thread would
have to wait for the writing thread to finish, and so no data corruption even
would occur.

Learning about this problem influenced my belief about concurrent programming
and making me aware of the problem. Prior to learning about the critical section
in the past, I have written programs that appeared to work with some testing,
however had occasional unexpected behaviour. After learning about the critical
section, I know why the behaviour was unexpected and occasional, and how to make
the program correct.

## Part B


