# NWEN303 Project 1 (Dylan Chong - 300373593)

## Task 1

I have added thorough java doc for the class and fields and methods inside
`src/main/java/model/Model.java`. These contain the explanations for:

- details on variables, algorithms, objects etc
- the meaning of the four main actions in Model.step()
- how the merging of particles work

Note 1: There originally was no java doc comments in this class, so all of the
java doc comments have been added by myself. I have made it clear where i added
inline comments.

Note 2: I intentionally added the java doc comments instead of typing here
because it is much easier to annotate the code for the purposes of explanation.

## Task 2

The `scheduler` thread pool has two threads. 

One of the threads is used to run the `MainLoop`, which tells the model to step
stepsPerFrame times for every frame. It will then sleep for the remaining amount
of milliseconds per frame, then proceed with more steps. ejIt sleeps so that the
frame rate will be consistent no matter how long the machine takes to compute
the steps (up to a point). 

The remaining thread is used to trigger a repaint of the UI, every 25 ms. It
does this by calling the J frame's repaint method on the swing event dispatch
thread. The repaint triggers the paint method on the canvas instance (there is a
canvas that is a child of the J frame). The canvas looks at the collection of
drawable particles from the model, and draws all of them onto the graphics
object. Note that the thread in the pool does not actually do much work, it is
only responsible for scheduling the repaint on the event dispatch thread.

The main loop thread and painting thread (whatever thread swing decides to use
for the painting) work mostly independently --- they work on mostly independent
tasks. There is however one point where the threads must share memory. The
painting thread must somehow access objects from the model to know what to draw. 

The painting thread reads from the volatile field `model.pDraw` (in the canvas
class), but does not write to the field or modify the objects in that
collection. At every step the model, in the `updateGraphicalRepresentation()`
method which is called by `step()`, data for each particle is copied from
`model.p` into a collection which, when the copying is done, is atomically
assigned to `model.pDraw`. This way the model is free to modify particle
collection `model.p` without having to worry about whether such modifications
would affect the painting. Conversely, the canvas/painting code does not have to
worry about reading from a collection that is modified at the same time. 

Each new collection assigned to `model.pDraw` is only read, never modified, once
the assignment has taken place. This ensures that there are no race conditions
caused by sharing memory between the two threads. There are no locks involved,
so it is impossible to get a deadlock or a livelock. Starvation does not occur
in the `scheduler` thread pool, because it contains two threads exactly, and
only two different tasks (the main loop, scheduling repainting) need to be done
in parallel.

## Task 3

### a) How to add parallelism

In the model's step method, the most expensive line of code is:

```java
for(Particle p:this.p){p.interact(this);}
```

I have changed this to:

```java
(isParallel ? p.parallelStream() : p.stream())
  .forEach((part) -> part.interact(this));
```

which, when using a sequential stream, does exactly the same as it did before
with the for loop. When using a parallel stream, this line of code will run
faster. The above line of code is the only place where I introduced parallelism.

The particle `interact` method modifies to fields `speedX` and `speedY`. To make
sure that the new values for these fields will be available on the main loop
thread, for the model to use in the other parts of the `step` method, I have
made the above-mentioned fields volatile. That is, the volatile keyword will
prevent any CPU caching issues. 

Volatile does not use the CPU cache, reading and writing to the field will be
much slower (actually it makes the program four times slower). To get around
this problem, I read the speed fields into local variables at the start of the
`interact` method, and write the local variables back to the fields at the end
of the method. This completely gets rid of the (noticeable) volatile speed
problem, as proven by my benchmarking.

### b) Why is it going to help insinuating particle moving, attracting each other and merging?

The most expensive work in the model is calling the `interact` method on the
particles. I have proven this by timing how much time the program spends in each
line of the `model.step` method for a 10 second run of the simulation. The run
uses the model configuration defined in the `Gui.main` method.

```java
  public void step() {
    // Timing these show that the interact() is the most expensive step by
    // two orders of magnitude.
    //
    // Printed results for a single run of the benchmark (see time() method)
    // in sequential mode.
    //
    // Name:      time spent here
    // step 1:         9096.42 ms
    // step 2:           33.37 ms
    // step 3:           15.56 ms
    // step 4:           43.68 ms
    Timer.INSTANCE.time(() -> {
      (isParallel ? p.parallelStream() : p.stream())
        .forEach((part) -> part.interact(this));
    }, "step 1");
    Timer.INSTANCE.time(() -> {    mergeParticles();}, "step 2");
    Timer.INSTANCE.time(() -> {    for(Particle p:this.p){p.move(this);}}, "step 3");
    Timer.INSTANCE.time(() -> {    updateGraphicalRepresentation();}, "step 4");
  }
```

As can be seen above the program spends negligible amounts of time in steps 2 to
4. The program spends `9096/(9096+33+16+44) = 99%` of its time in the `step`
method performing the particle `interact` calls.

By performing the interactions in parallel, the speed of the step method can be
improved by a bit less than `n` times, where `n` is the number of cores
available on the machine.

Note that the particle `interact` method calculates the new speed for each
particle, and figures out what other particles to merge with. That is,
parallelising the loop that calls `interact` on all of the particles will speed
up the most time-consuming parts of moving, attracting, and merging.

### c) What kind of data contentions will you need to resolve?

Parallelising the interactions will require being careful about shared memory. 

- Firstly, deadlocks are impossible because I did not add any locks. 
- Secondly, live locks are not possible because the threads on the parallel
  stream should not interact with each other - the the interact method does not
  act in response to work done in another thread.
- Thirdly, starvation is not possible because because the threads in the
  parallel stream are not competing with each other to access a shared,
  synchronised resource - they work independently.
- Fourthly, race conditions have the potential to exist in the code. This is
  discussed below.

#### Volatility and caching

As mentioned in question `a)`, I added the volatile keyword to the speed fields
on the particle so that there are no CPU caching issues. An avoided CPU caching
issue would be, for example, one of the parallel stream's worker threads writing
the new speed to the CPU cache, but the new value is not available to the main
loop thread when it continues to work on the other parts of the `step` method in
the model.

<!-- TODO check impacting -->

#### Sharing memory between threads in the parallel stream's thread pool

One of the assumptions are made in the rest of this question `c)` is that the
threads in the parallel stream do not interact, or share memory that gets
modified by any of the other threads. We need to prove that the above is true in
order to make sure that the simulation is not prone to concurrency problems. 

The most important point to consider is that the only fields being modified in
the `particle.interact` method are `speedX`, `speedY`, and the `impacting`
collection. We need to prove that these three fields are only modified and read
by the current thread in the work that is done by the parallel stream. We don't
need to consider the other fields because they are not modified. 

The only work done by the parallel stream is calling the interact method on the
particles. Therefore, we only need to search in this code for modifications.
Note that I assumed that the concurrency changes have been made to the code. We
must also note that the interact method on each particle is only called once in
one call of the step method. 

By using the IDE's `find usages` command, we can see that the speed fields are
only modified at the start and end of the interact method, where they are read
and written to the field. These usages are okay because they are used on the
current thread, on the current particle object - other particle objects' speed
are not accessed, which means that the speed fields are not prone to race
conditions.

By using the `find usages` IDE command again, we can see that the `impacting`
collection is only accessed by the current particle object. This use of the
`impacting` collection is safe.

Hopefully the above explanations are sufficient to prove that there are no race
conditions possible related to the `interact` method.

### d) How are you sure that there is no hidden aliasing creating unpredicted data contentions?

One way to check that there are unpredicted data contentions is to run the
simulation, and check that there looks the same between sequential and parallel
modes. Visually, they appear to be the same. This of course is not a foolproof
way to check that there are no data contentions.

