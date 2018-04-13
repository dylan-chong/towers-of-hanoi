package model;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Model {
  public static final double size=900;
  public static final double gravitationalConstant=0.002;
  public static final double lightSpeed=10;//the smaller, the larger is the chunk of universe we simulate
  public static final double timeFrame=20;//the bigger, the shorter is the time of a step
  public List<Particle> p=new ArrayList<Particle>();
  public volatile List<DrawableParticle> pDraw=new ArrayList<DrawableParticle>();

  public void step() {
//    p = p.stream()
//      .map((part) -> part.interact(this))
//      .collect(Collectors.toList());

    // Timing these show that the interact() is the most expensive step by
    // two orders of magnitude
    //
    // Printed results for a single run of the benchmark (see time() method)
    // Name:      time spent here
    // step 1:         9096.42 ms
    // step 2:           33.37 ms
    // step 3:           15.56 ms
    // step 4:           43.68 ms
    Timer.INSTANCE.time(() -> {    p.parallelStream().forEach((part) -> part.interact(this));}, "step 1");
    Timer.INSTANCE.time(() -> {    p.forEach((part) -> part.updateSpeed());}, "step 1.1");
    Timer.INSTANCE.time(() -> {    mergeParticles();}, "step 2");
    Timer.INSTANCE.time(() -> {    p.forEach((part) -> part.move(this));}, "step 3");
    Timer.INSTANCE.time(() -> {    updateGraphicalRepresentation();}, "step 4");
  }
  private void updateGraphicalRepresentation() {
    Color c=Color.ORANGE;

//    this.pDraw = p
////      .parallelStream() // not any faster, not enough particles to be faster parallel
//      .stream()
//      .map(particle -> new DrawableParticle(
//        (int)particle.x, (int)particle.y, (int)Math.sqrt(particle.mass),c )
//      )
//      .collect(Collectors.toList());

    ArrayList<DrawableParticle> d=new ArrayList<DrawableParticle>();
    for(Particle p:this.p){
      d.add(new DrawableParticle((int)p.x, (int)p.y, (int)Math.sqrt(p.mass),c ));
    }
    this.pDraw=d;//atomic update
  }
  public void mergeParticles(){
    Stack<Particle> deadPs=new Stack<Particle>();
    for(Particle p:this.p){ if(!p.impacting.isEmpty()){deadPs.add(p);}; }
    this.p.removeAll(deadPs);
    while(!deadPs.isEmpty()){
      Particle current=deadPs.pop();
      Set<Particle> ps=getSingleChunck(current);
      deadPs.removeAll(ps);
      this.p.add(mergeParticles(ps));
    }
  }
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
