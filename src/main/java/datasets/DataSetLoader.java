package datasets;

import model.Model;
import model.Particle;

import java.util.Random;

public class DataSetLoader {
  public static Model getRegularGrid(int min, int max, int distance, boolean isParallel){
    Model result=new Model(isParallel);
    for(int i=min;i<max;i+=distance){
      for(int j=min;j<max;j+=distance){
        result.p.add(new Particle(0.5,0,0,i,j));
      }
    }
    return result;
  }
  public static Model getRandomGrid(int min, int max, int distance, boolean isParallel){
    Model result=new Model(isParallel);
    Random r=new Random(1);
    for(int i=min;i<max;i+=distance){
      for(int j=min;j<max;j+=distance){
        result.p.add(new Particle(0.5,0,0,i+0.5-r.nextDouble(),j+0.5-r.nextDouble()));
      }
    }
    return result;
  }
  public static Model getRandomSet(int min, int max, int size, boolean isParallel){
    Model result=new Model(isParallel);
    Random r=new Random(1);
    for(int i=0;i<size;i++){
      result.p.add(new Particle(0.5,0,0,min+r.nextInt(max-min)+0.5-r.nextDouble(),min+r.nextInt(max-min)+0.5-r.nextDouble()));
    }
    return result;
  }
  public static Model getRandomRotatingGrid(int min, int max, int distance, boolean isParallel){
    Model result=new Model(isParallel);
    Random r=new Random(1);
    for(int i=min;i<max;i+=distance){
      for(int j=min;j<max;j+=distance){
        result.p.add(new Particle(
          2.5,
          Math.signum(j-450)*r.nextDouble()/10d,
          Math.signum(450-i)*r.nextDouble()/10d,
          i+r.nextDouble(),
          j+r.nextDouble()
        ));
      }
    }
    return result;
  }

}
