package model;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Contains all of the simulation data, and performs operations on the data to
 * advance the simulation (see {@link Model#step()}).
 */
public class Model {
  /**
   * The width and height of the canvas in pixels.
   */
  public static final double size=900;
  /**
   * Defines how strong gravity is.
   */
  public static final double gravitationalConstant=0.002;
  public static final double lightSpeed=10;//the smaller, the larger is the chunk of universe we simulate
  /**
   * The amount of time that should pass for one single step.
   */
  public static final double timeFrame=20;//the bigger, the shorter is the time of a step

  /**
   * Decides whether to use a parallel stream or a sequential stream.
   */
  public final boolean isParallel;

  /**
   * A collection of all of the particles in the universe
   */
  public List<Particle> p=new ArrayList<Particle>();
  /**
   * A list of objects that represent the particles in the universe.
   * The canvas reads this list every single frame.
   *
   * Conceptually, this list needs to be changed whenever {@link Model#p} is
   * changed. However, any given list that is assigned to this field is never
   * actually mutated by this class. When this list needs to be changed, this
   * class creates another list, and when it is done being created, this field
   * is assigned to it. This makes the UI's data update atomic, and therefore
   * not prone to race conditions and concurrent modification exceptions.
   * See {@link Model#updateGraphicalRepresentation()}.
   */
  public volatile List<DrawableParticle> pDraw=new ArrayList<DrawableParticle>();

  public Model(boolean isParallel) {
    this.isParallel = isParallel;
  }

  /**
   * Advances the simulation by one step in time. This method has steps that
   * have been labelled by the benchmarks methods below.
   *
   * * step 1: Tells each particle to figure out how it should behave. That is,
   * figure out the new speed based on the old speed, and the masses and
   * distances to the other particles. It should also figure out what particles
   * it should merge with (see {@link Particle#impacting}.
   *
   * * step 2: Merge the particles that are impacting each other based on what
   * we figured out in step one (see {@link Particle#impacting}). See {@link
   * Model#mergeParticles()} for more information.
   *
   * * step 3: ﻿The particles that were not merged in step two, as well as the
   * new merged particles, should now step forward based on their current speed
   * (see Particle.speedX and speedY) and how long one step should take (see
   * Model.timeFrame). This is simply translating each particle's X and Y
   * positions.
   *
   * * step 4: ﻿See the description for {@link Model#pDraw}.
   */
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

  /**
   * See the description for {@link Model#pDraw}
   */
  private void updateGraphicalRepresentation() {
    ArrayList<DrawableParticle> d=new ArrayList<DrawableParticle>();
    Color c=Color.ORANGE;
    for(Particle p:this.p){
      d.add(new DrawableParticle((int)p.x, (int)p.y, (int)Math.sqrt(p.mass),c ));
    }
    this.pDraw=d;//atomic update
  }

  /**
   * This method merges particles that are colliding with each other into a
   * bigger particle. This method assumes that the {@link
   * Particle#interact(Model)} method has already been called, ie each particle
   * already knows what other particles they are close and should merge with.
   *
   * I have added new comments below (starting with // **) that explain how this
   * method works.
   */
  public void mergeParticles(){
    Stack<Particle> deadPs=new Stack<Particle>();
    for(Particle p:this.p){ if(!p.impacting.isEmpty()){deadPs.add(p);}; }
    this.p.removeAll(deadPs);
    //** All particles that will impact another particle will now be considered
    //** dead (no longer needed). The dead particles are removed from the
    //** universe.

    while(!deadPs.isEmpty()){
      //** For each particle that is now considered dead
      Particle current=deadPs.pop();
      //** Get the clump of particles that the current particle belongs to
      Set<Particle> ps=getSingleChunck(current);
      // ** Prevent creating duplicate merged particles
      deadPs.removeAll(ps);
      // ** 'Commit' the new merged particle to the universe
      this.p.add(mergeParticles(ps));
    }
  }

  /**
   * Get all of the particles the current particle should merge with (impacting),
   * including all of the particles that should merge with the ones that the
   * current particles should merge with.
   *
   * In essence, return the clump of particles that this current particle
   * belongs to.
   */
  private Set<Particle> getSingleChunck(Particle current) {
    Set<Particle> impacting=new HashSet<Particle>();
    impacting.add(current);
    while(true){
      Set<Particle> tmp=new HashSet<Particle>();
      for(Particle pi:impacting){tmp.addAll(pi.impacting);}
      boolean changed=impacting.addAll(tmp);
      if(!changed){break;}
      }
    //now impacting have all the chunk of collapsing particles
    return impacting;
  }

  /**
   * Combine the given particles into one big one, preserving overall mass and
   * momentum, and centre of mass.
   */
  public Particle mergeParticles(Set<Particle> ps){
    double speedX=0;
    double speedY=0;
    double x=0;
    double y=0;
    double mass=0;
    for(Particle p:ps){  mass+=p.mass; }
    for(Particle p:ps){
      x+=p.x*p.mass;
      y+=p.y*p.mass;
      speedX+=p.speedX*p.mass;
      speedY+=p.speedY*p.mass;
    }
    x/=mass;
    y/=mass;
    speedX/=mass;
    speedY/=mass;
    return new Particle(mass,speedX,speedY,x,y);
  }
}
