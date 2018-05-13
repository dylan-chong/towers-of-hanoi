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
