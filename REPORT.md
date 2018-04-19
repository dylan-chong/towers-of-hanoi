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

More generally, it is important to consider the critical section when
operations on the critical section data are not atomic. This can happen when
the program can be parallelised, or when work is done asynchronously on a
single thread. For example, one part of the program can mess up the critical
section and then get paused for an I/O operation. Then a second part of the
program can try to read from the critical section --- but it is not in a
coherent state! Then the I/O operation finishes and the first part of the
program tidies up the critical section. By this time it is too late, the second
part of the program read the incoherent data.

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

Door locked, key in, no one inside (referred to as `livelock` from now on) is
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

Livelock is not possible. Suppose that it is, then both threads must be stuck
at the while loop. That is, both of the variables are set to true. This is not
possible because the threads' respective variables are set to false in their
finally blocks, and at the start. Therefore, livelock is not possible.

Starvation: Same as attempt 1

## Attempt 2b

Mutual exclusion does not hold. Suppose that the two variables are set to
(true, false), the first thread is about to enter the finally block, and the
second thread is looping at the while loop. The first thread sets `want1` to
false, and then pauses. The second thread finishes the while loop and then
pauses. The first thread continues, skips past the while loop because `want2 ==
false`. Now both threads can proceed and enter the critical section.

Livelock: Same as attempt 2

Starvation: Same as attempt 2

## Attempt 3

Mutual exclusion holds. Suppose not, then both of the variables must be set to
false while both threads are checking the while loops. This is not possible
because both the variables are set to true just before the while loop.

Livelock this possible if both threads run at the same time at more or less the
same speed. That is, both threads set their respective variables to true at the
same time, and then reach the while loop at the same time. They will both be
stuck because both the variables are set to true.

Starvation: Same as attempt 1

## Attempt 4

Mutual exclusion holds. Suppose not, then both variables must be false while
both threads are checking the while loop. This is not possible because,
firstly, both variables are set to true before the while loops. That is, at
least one of the variables will be set to true in both threads are checking the
while loop.

Livelock is not possible. Suppose that it is, then both variables must be set
to true and both threads are checking the while loop. Inside the while loops,
the variables are set to false and then true again. It is possible for the
scheduler to pause one of the threads directly after it has set its variable to
false, allowing the other thread to proceed. (Although, this may not be
guaranteed.) That is, the system just unlocked itself from the live lock.
Livelock implies a permanent stop. Therefore Livelock is not possible.

Starvation is possible. Suppose both of the threads are scheduled to run at
exactly the same speed at the same time (repeatedly for a long time). Both
threads will set the variables to true, enter the while loop, set the variables
to false and then true again, check the while loop, set the variables to false
then true again... It is possible that this can happen for a long time, and so
both threads are starved.

## Attempt 5

Mutual exclusion holds. See the explanation for attempt 4 - it is exactly the
same.

Live lock is also not possible. Suppose it is, then both threads must be stuck
in the while loop. This means that both of the `want` variables are true.
Suppose that turn is set to 1 (it could be said to 2 instead, but that is an
almost identical scenario). It is possible for one of the threads to exit the
while loop. For this possibility to happen, the second thread enters the if,
sets `want2=false` and then waits for turn to be set to 2. Now, the first
thread exits the while loop because `want2==false`, therefore exiting the live
lock. Live locks, by definition, cannot be exited. Therefore this above
situation is not a Livelock and therefore live locks are not possible.

Starvation is not possible. Suppose it is, then the second thread is waiting
for the first thread before being able to proceed (or the other way around.)
That is, the first thread is taking a very long time at some point (or threw an
exception). The only two points where the first thread can take a very long
time is in the critical section, and in the normal operations. We made the
assumption the work and the critical section will always terminate, and that it
will be impossible to avoid starvation if this were not true. Therefore, we
should ignore the case where the first thread is taking a long time in the
critical section. This leaves the first thread taking a long time in the normal
operations (or it threw an exception). Now suppose that the second thread has
finished work on the critical section and has exited the finally block. Now it
is the priority for the first thread to do work, and the second thread does not
want to do work (these statements refer to the values of the variables). The
second thread will want to work (i.e. `want2=true`). The first thread does not
want to work yet (`want1` is set to false at the end of the finally block, or
at the beginning of the program). The second thread is allowed to work because
the first thread does not want to work yet. This creates a contradiction
because we proposed that starvation is possible when the second thread is
waiting for the first thread to do it is very long work. However, the second
thread does not have to wait as it can work instead. Therefore starvation is
not possible

\newpage

# Task 2

~~~~ {.java .numberLines}
  static volatile int want1 =0;
  static volatile int want2 =0;
//first worker
...while(true){
    //normal operations, it can loop forever
    if(want2==-1)//pre-protocol
      {want1=-1;}//pre-protocol
    else
      {want1=1;}//pre-protocol
    while(want2==want1){}//pre-protocol
    try{
      //critical section operations,
      // termination assured
    }
    finally{want1=0;}//post-protocol
  }
//second worker
...while(true){
    //normal operations, it can loop forever
    if(want1==-1)//pre-protocol
      {want2=1;}//pre-protocol
    else
      {want2=-1;}//pre-protocol
    while(want1==-want2){}//pre-protocol
    try{
      //critical section operations,
      // termination assured
    }
    finally{want2=0;}//post-protocol
  }
~~~~

Notes: 

    WantX variables are like enum {
      WANT = -1
      NORMAL_OPERATIONS = 0 // don't want
      ALSO_WANT = 1
    }

Mutual exclusion holds. Suppose not, then the conditions for each workers'
while loop must be false while both workers are checking the while. This is
only possible when exactly one of the variables is equal to 0 (i.e the other
variable is set to 1 or -1).  This is not possible because both of the workers
will set their own variables to a nonzero value directly before the while loop.
Therefore, mutual exclusion holds.

Live lock is not possible. Suppose it is, then the conditions for each workers'
while loop must be true while both workers are checking the while loop. In
order for this to be true, both variables need to be set to 0. This is not
possible because both of the workers will set their own variables to a nonzero
value directly before the while loop. Therefore, live lock is not possible.

Starvation is not possible. Suppose it is, then one worker (say the first) is
waiting for a long time, while the other is doing work. The second worker must
be looping for a long time at line 19, which must also mean that `want2 == 0`.
If the first worker is waiting, then it must be stuck at line 10 because the
while loop condition is true. The condition cannot be true because `want2 == 0`
and `want1` must have been assigned to a nonzero value directly above the while
loop. Therefore, the first worker cannot starve.

Now suppose the symmetrical example is true --- the second worker is waiting
for a long time at line 24, and the first worker is looping at line 5. The
following must be true `want1 == 0` and `want1 == -want2` (stuck at line 24).
This must mean `want2 == 0` which is not possible because `want2` was said to a
nonzero value on line 21 or line 23. Therefore, the second worker cannot
starve, in addition to the first worker not being able to starve.

\newpage

## Task 3

~~~ {.java .numberLines}
static volatile boolean want1 =false;
static volatile boolean want2 =false;
static volatile int turn =1;
//first worker
while(true){
  //normal operations, it can loop forever
  want1=true;
  if(want2){//pre-protocol
    if(turn==2){
      want1=false;
      while(turn!=1){}//pre-protocol
      want1=true;
    }
    while(want2){}//pre-protocol
  }//pre-protocol
  try{
    //critical section operations,
    // termination assured
  }
  finally{//post-protocol
    want1=false;//post-protocol
    turn=2;//post-protocol
  }//post-protocol
}
//second worker
while(true){
  //normal operations, it can loop forever
  want2=true;
  if(want1){//pre-protocol
    if(turn==1){
      want2=false;
      while(turn!=2){}//pre-protocol
      want2=true;
    }
    while(want1){}//pre-protocol
  }//pre-protocol
  try{
    //critical section operations,
    // termination assured
  }
  finally{//post-protocol
    want2=false;//post-protocol
    turn=1;//post-protocol
  }//post-protocol
}
~~~~

## Mutual Exclusion

Mutual exclusion holds. Suppose not, then both workers entered the critical
section because `!want1 && !want2`. This is not possible. At lines 7 and 28,
both of the variables are set to true. If `turn == 1` then `want2` could have
been set to false on line 31, but `want1` could not have been set to false. A
symmetrical scenario occurs when `turn == 2`. Since both the variables could
not have been set to false, mutual exclusion must hold.

## Live Lock

Livelock is not possible. Suppose it is, then both threads must be stuck at
while loops with a conditions are true. There are four possible scenarios:

### 1

The first worker is at line 11 and the second worker is at line 32. Both of
these conditions cannot be true at the same time because `turn` is only set to
one or two. We can eliminate this scenario.

### 2

The first worker is at line 11 and the second worker is at line 35. We can
reuse the proof at scenario number three.

### 3

The first worker is at line 14 and the second worker is at line 32. The first
worker is at line 14 and a second worker is at line 32. We can easily prove
this is not a live lock by contradiction. By assuming that both workers are
blocked at their respective lines, we know that `want2 == true && turn == 1`
--- this means that the while loop conditions will always be true. However, the
second worker just set `want2 = false` at line 31, the line above where it
currently is at now. This means that the first worker is not actually blocked.
Therefore it is not a live lock

### 4

The first worker is at line 14 and the second worker is at line 35. 

In order to block the first worker at line 14, `want1` must be set to true and
`turn` must have been set to 1 since before the first worker reaches line 9 or
while the first worker is at line 10 or 11. `turn` cannot be set to 2 while the
worker is at line 12 or 14, because the only way for `turn` to be set to 2 is
for the first worker to be at line 22.

However, if `turn == 1`, then the second worker would have entered that if
block at line 30 and got into line 32 (remembering that the first worker is at
line 14). This is scenario number three --- we have already proven that this is
not a live lock. That means that `turn == 2` when the second worker is at line
30. As stated earlier, if the second worker got to line 35 and `turn == 2`,
then either case is true: the first worker would have ended up at line 11 upon
entering that a statement at line 8, or the first worker set `turn = 2` after
the second worker finished line 30. The first of the two cases is the same as
scenario number 2, which we have proven is not a live lock, therefore the
second case must be true. Proceeding with the second case, the second worker
would now proceed to line 35 at some point, and the first worker is at line 23.
First worker would proceed to line 6, run line 7, enter the if statement at
line 8 (because `want2` was set to `true` at line 28), entered that if
statement at line 9 (because we recently set `turn = 2`), run line 10, and then
loop at line 11 (because `turn == 2`). Now, because the first worker is at line
11, and the second worker as at line 35, we are at scenario number 2 again.

Therefore, live lock is not possible in the proposed scenario (first worker at
line 14, and second worker at line 35) because the first worker it cannot be at
line 14 at the same time as the second worker is at line 35.

### Summary

Given that all of the above scenarios cannot result in live locks, and that the
above scenarios are the only possible ones for live locks, live locks are not
possible.

## Starvation

Starvation is not possible. In order for starvation to happen, one of the
workers must be busy outside the critical section (forever), while the second
worker is starving while waiting to do work. We will prove this for one of the
two symmetrical scenarios, and the same logic can be applied with the variables
and workers 'switched'.

Suppose the first worker is the one who is busy forever, outside the critical
section. The first worker must be at line 6 otherwise it would not be
considered busy. If it is waiting at a while loop, and the second worker is
waiting, then we have a live lock. (However, we proved that live lock is not
possible in the section above.)

If the second worker were to starve, then it would have to be waiting at line
32 or line 35. Because the first worker is at line 6, we know that `want1 ==
false`. The easy case is where the second worker is at line 35, then it will be
able to continue past line 35 because we said above that `want1 == false`. The
harder case is when the second worker is at line 32. If `turn == 2`, then the
second worker is able to proceed. Otherwise, `turn == 1`, and we have a more
complex scenario that we have to reverse engineer. We need to prove that it is
not possible for `want1 == true` and `turn` to be permanently set to 1. This
needs to be true to allow the second worker to get into the if statements
online 29 and 30 and starve at line 32. Since the first worker sets `turn = 2`
at line 22 and `want1` will always be false at line 6, we need to get the
second worker to set `turn = 1` after the first worker sets `turn = 2` at line
22\. However, since `want == false` after `turn` is set to 2 the first worker,
the second worker will not be able to enter the if statement at line 29.
Therefore, starvation cannot occur with the second worker at line 32.
