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

    class Example1Bad {
      private static volatile int count = 0;

      public static void main(String[] args) {
        Runnable runnable = () -> {
          count++;
          System.out.println(count);
        };
        new Thread(runnable).start();
        new Thread(runnable).start();
        // What gets printed:
        // Who knows!
      }
    }

In the above example we do not know what will get printed out. One possible
scenario is the correct one --- `1\n2` --- in the situation, the two new threads
do not run with overlapping times. 

The second possibility is that `1\n1` is printed. In this situation, both
threads may read the number `0`, and write back the number `1` at the same time.
Both threads will then read `count` and print number `1`.

The third possibility is that `2\n2` is printed. The first thread increments
`count`, then the second thread increments `count`, then both of threads print
the number `2`.

The last possibility is that `2\n1` is printed. The first thread increments
`count` to `1`, then reads count on the line with the `println`, then gets
paused. The second thread increments `count` to `2`, and then reads and prints
`2`. The first thread then continues and prints the value that it read before
`1`.

Three out of four of these scenarios are undesirable. It is also not possible to
predict which are the scenarios will happen. Therefore, we should introduce
synchronisation at this critical section.

    class Example1LessBad {
      private static volatile int count = 0;
      private static final Object LOCK = new Object();

      public static void main(String[] args) {
        Runnable runnable = () -> {
          synchronized (LOCK) {
            count++;
            System.out.println(count);
          }
        };
        new Thread(runnable).start();
        new Thread(runnable).start();
        // What gets printed:
        // 1
        // 2
      }
    }

In this scenario above, the work inside the runnable is synchronised, so only
one thread can do the work at any given time. The program will always print
`1\n2`.

## Part C

### Attempt 1

Mutual exclusion holds. Suppose that both threads are in the critical section.
That means, at some point turn has been set to 2 while the first thread is
still inside the try block (or set to 1 while the second thread is in the try
--- a symmetrical scenario). However, turn can only be set to 2 once the first
thread leaves the try block (similar reasoning for the symmetrical scenario).
This is a contradiction, so therefore mutual exclusion must hold.

Door locked, key in, no one inside (referred to as `deadlock` from now on) is
not possible. Suppose it were: this means that both threads will end up looping
at their while loops. Also, turn would have to be set to something other than
one or 2 to stop both threads from finishing the while loops. This is not
possible with the volatile variable in the scenario because it is set to only 1
or 2.

Starvation is possible. If for example, the first thread loops for ever in the
normal operations before the while loop (or throws an exception), then turn
will never be set to 2. In this case, the second thread will be stuck living at
the while loop for ever.

## Attempt 2

Mutual exclusion does not hold because both of the variables are set to false.
In the case where both threads run at the same time and at the same speed, both
threads will finish the while loops at the same time, therefore entering a
critical section at the same time.

Deadlock is not possible. Suppose that it is, then both threads must be stuck
at the while loop. That is, both of the variables are set to true. This is not
possible because the threads' respective variables are set to false in their
finally blocks, and at the start. Therefore, deadlock is not possible.

Starvation: Same as attempt 1

## Attempt 2b

Mutual exclusion does not hold. Suppose that the two variables are set to
(true, false), the first thread is about to enter the finally block, and the
second thread is looping at the while loop. The first thread sets `want1` to
false, and then pauses. The second thread finishes the while loop and then
pauses. The first thread continues, skips past the while loop because `want2 ==
false`. Now both threads can proceed and enter the critical section.

Deadlock: Same as attempt 2

Starvation: Same as attempt 2

## Attempt 3

Mutual exclusion holds. Suppose not, then both of the variables must be set to
false while both threads are checking the while loops. This is not possible
because both the variables are set to true just before the while loop.

Deadlock this possible if both threads run at the same time at more or less the
same speed. That is, both threads set their respective variables to true at the
same time, and then reach the while loop at the same time. They will both be
stuck because both the variables are set to true.

Starvation: Same as attempt 1
