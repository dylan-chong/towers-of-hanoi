---
title: NWEN303 Assignment 3
author: Dylan Chong - 300373593
geometry: margin=2.5cm
---

# Q1

## a

Given that weather forecasting is an extremely CPU intensive task, as much CPU
power should be used as possible. Distributed systems, due to the intrinsic
design of supporting large numbers of machines, will have the most CPU power at
their disposal. Therefore, distributed systems are the most suited for whether
forecasting.

## a2

The ideal system depends on the nature of the webpages been served and how data
is modified.

Suppose that all of the webpages are the same for each person, i.e. no one has
to log in, so the web server essentially does not have to do any processing on
the files. In this case, a parallel system on a single machine _may_ provide
enough processing power. The system can be made concurrent to support greater
numbers of simultaneous connections --- the system can switch to a different
thread while a long operation, such as filesystem access, is taking place.

Suppose that everyone has to login, and therefore special processing must be
done on each page that is served. There would be more processing power required
to do this, especially with a database which may be (and is probably) the
bottleneck to the performance of the system. In this case, a distributed system
with multiple databases and multiple web servers would be able to support the
high number of simultaneous requests.

## b

Given that the airline booking system allows users to check fares offered by
many different travel agents, the system may be intrinsically distributed. That
is, finding the cheapest fare involves retrieving data from many different
databases, each owned by different travel agents. Given this, if two people
were to book the same seat on the same plane at the same time, each travel
agent's system would handle it --- it is not a responsibility of our service to
make sure that these situations don't cause problems.

## c

- 'Latency' is the duration between the start and end of data transmission from
one point to another [1].
- 'Bandwidth' is the rate at which data can be transmitted (a possible unit of
measurement is bits per second) [2]. (Note: it was very hard finding a peer
reviewed articles that explicitly defines bandwidth --- they all seem to just
use the term without defining it.)

[1]
S. Bell and S. Walker,
"Futurescaping Infinite Bandwidth, Zero Latency,"
Futures, vol. 43, iss. 43, para. 11,
June 2011.
Available: ScienceDirect, https://www.sciencedirect.com/science/article/pii/S0016328711000474?via%3Dihub
[Accessed May 14, 2018].

<!-- M. T. Kimour and D. Meslati, “Deriving objects from use cases in real-time -->
<!-- embedded systems,” Information and Software Technology, vol. 47, no. 8, p. 533, -->
<!-- June 2005. [Abstract]. Ava ilable: ProQuest, http://www.umi.com/proquest/. -->
<!-- [Accessed November 12, 2007]. -->

[2]
J. Nicholson,
"Bandwidth - Oxford Reference,"
in The Concise Oxford Dictionary Of Mathematics (5 ed.).
Oxford University Press,
[online document],
2014.
Available: Oxford Reference,
http://www.oxfordreference.com/view/10.1093/acref/9780199679591.001.0001/acref-9780199679591-e-3121?rskey=nnsWqA&result=1
[Accessed May 14, 2018].

<!-- D. Ince, “Acoustic coupler,” in A Dictionary of the Internet. Oxford University -->
<!-- Press, [online document ], 2001. Available: Oxford Reference Online, -->
<!-- http://www.oxfordreference.com [Accessed: May 24, 2007]. -->

## d

`speedup = T(1) / T(10) = 1 / 2 = 0.5`. This means that the parallel program is
half the speed of the sequential program. The parallel program must be very
poorly designed for it to be slower than the sequential program. For example,
there could be a extremely large amount of work required to split up the task
into 10 independent subtasks for the 10 nodes --- work that is not required in
a sequential program. Another possibility for the parallel program being very
slow is that there are many points in the program where threads are waiting for
other threads to complete, and so the multiple cores/CPUs are not fully taken
advantage of.

# Question 2

SMP and NUMA systems are both types of shared memory systems. SMP systems have
uniform memory access, meaning that all of the processors have the same access
speed for all of the memory (ignoring cache). In NUMA systems, each processor
has its own memory which it has fast access to, and a memory request module. If
a processor needs to get access to memory that is owned by another processor,
it must request the data that across the network through the memory request
modules. Network requests make accessing other processors' memories much slower
than accessing the processor's own memory.

# Question 3

Each processor has fast access to its own memory, but to access other memory,
the data must be requested and transferred across the network from another
processing element's memory. Because of the distance and limited bandwidth of
the network, access to a process's own memory will be better faster. P1
accessing memory at location 100 is fast because location 100 is within its
local memory store, whereas P3 accessing memory at location 210 will be slow
because location to 10 is a different processor's memory.

# Question 4

I would suspect that there would be no significant difference between the time
required to read and write a request, give a simple scenario.

If we suppose that we also need to find a place in another processor's memory
to save the data, then we may also need a network request to tell it to
allocate the memory --- a request that won't be needed for reading. The
external processor may need to perform garbage collection to free up space to
write data, which may increase the time required to perform the request from
start to finish.

Another situation is where we have a read-write lock on some popular data in
the external processor. That is, either a single process can write to the data,
or zero or more processors can read from the data simultaneously. If the data
is being read by other processors, then we will be able to read as well without
delay, assuming that there is no other processor waiting to write. If we want
to write to the data, then we will have to request access to the write lock.
Getting access to the right lock involves waiting for all of the current read
requests to finish, which may take a long time if the data is massive. It would
also prevent other read requests from being executed, slowing down the rest of
the distributed program. Therefore, if we assume that the data is read from
more than it is written to, then writing will be a bit slower.

# Question 5

If rows are delivered to all of the workers first, then computation can only
begin after the first column has been delivered to the first worker. This is
because, before the first column is given to the first worker, none of the
workers have enough information to perform any calculations yet.

# Question 6

Given that the previous output matrix was a 3 x 3 matrix, and that the matrices
we use now are 6 x 3 and 3 x 3 respectively, the output matrix would be a 3 x 3
again. (Note: I am assuming a rows by columns format, not columns by rows.)

The only difference between the new and the old way, therefore, would be that
each row is six numbers long instead of three. That is, during the starting
phase where the rows are initially passed to all the workers, six numbers at a
time will have to be passed instead of three.
