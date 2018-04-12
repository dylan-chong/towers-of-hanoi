package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class Model {
  public static final double size=900;
  public static final double gravitationalConstant=0.002;
  public static final double lightSpeed=10;//the smaller, the larger is the chunk of universe we simulate
  public static final double timeFrame=20;//the bigger, the shorter is the time of a step
  public List<Particle> p=new ArrayList<Particle>();
  public volatile List<DrawableParticle> pDraw=new ArrayList<DrawableParticle>();
  public void step() {
    for(Particle p:this.p){p.interact(this);}
    mergeParticles();
    for(Particle p:this.p){p.move(this);}
    updateGraphicalRepresentation();
  }
  private void updateGraphicalRepresentation() {
    ArrayList<DrawableParticle> d=new ArrayList<DrawableParticle>();
    Color c=Color.ORANGE;
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
