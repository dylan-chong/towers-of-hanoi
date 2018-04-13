package model;

import java.util.HashSet;
import java.util.Set;

public class Particle {
  public Particle(double mass, double speedX, double speedY, double x, double y) {
    this.mass = mass;
    this.speedX = speedX;
    this.speedY = speedY;
    this.x = x;
    this.y = y;
  }

  public Set<Particle> impacting = new HashSet<Particle>();
  public double mass;
  public double speedX;
  public double speedY;
  public volatile double tempSpeedX; // edit on another thread
  public volatile double tempSpeedY;
  public double x;
  public double y;

  public void move(Model m) {
    x += speedX / (Model.timeFrame);
    y += speedY / (Model.timeFrame);
    //uncomment the following to have particle bouncing on the boundary
    //if(this.x<0){this.speedX*=-1;}
    //if(this.y<0){this.speedY*=-1;}
    //if(this.x>Model.size){this.speedX*=-1;}
    //if(this.y>Model.size){this.speedY*=-1;}
  }

  public boolean isImpact(double dist, double otherMass) {
    if (Double.isNaN(dist)) {
      return true;
    }
    double distMass = Math.sqrt(mass) + Math.sqrt(otherMass);
    if (dist < distMass * distMass) {
      return true;
    }
    return false;
  }

  public boolean isImpact(Iterable<Particle> ps) {
    for (Particle p : ps) {
      if (this == p) {
        continue;
      }
      double dist = distance2(p);
      if (isImpact(dist, p.mass)) {
        return true;
      }
    }
    return false;
  }

  public double distance2(Particle p) {
    double distX = this.x - p.x;
    double distY = this.y - p.y;
    return distX * distX + distY * distY;
  }

  public void interact(Model m) {
    // So the 3 chunks of code below seem to contribute roughly equal amounts of time
//    Timer.INSTANCE.time(timerController -> {
      double speedX = this.speedX;
      double speedY = this.speedY;

      for (Particle p : m.p) {
        if (p == this) continue;
        //timerController.start("interact 1");
        double dirX = -Math.signum(this.x - p.x);
        double dirY = -Math.signum(this.y - p.y);
        double dist = distance2(p);
        if (isImpact(dist, p.mass)) {
          this.impacting.add(p);
          continue;
        }
        //timerController.stop();
        //timerController.start("interact 2");
        dirX = p.mass * Model.gravitationalConstant * dirX / dist;
        dirY = p.mass * Model.gravitationalConstant * dirY / dist;
        assert speedX <= Model.lightSpeed : speedX;
        assert speedY <= Model.lightSpeed : speedY;
        //timerController.stop();
        //timerController.start("interact 3");
        double newSpeedX = speedX + dirX;
        newSpeedX /= (1 + (speedX * dirX) / Model.lightSpeed);
        double newSpeedY = speedY + dirY;
        newSpeedY /= (1 + (speedY * dirY) / Model.lightSpeed);
        if (!Double.isNaN(dirX)) {
          speedX = newSpeedX;
        }
        if (!Double.isNaN(dirY)) {
          speedY = newSpeedY;
        }
        //timerController.stop();
      }

      tempSpeedX = speedX;
      tempSpeedY = speedY;
//    });
  }

  public void updateSpeed() {
    speedX = tempSpeedX;
    speedY = tempSpeedY;
  }
}
