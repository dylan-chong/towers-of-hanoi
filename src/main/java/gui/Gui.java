package gui;

import datasets.DataSetLoader;
import main.MainKt;
import model.Model;
import model.Timer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class Gui extends JFrame implements Runnable{
  public static volatile int staticAvgTime;
  public static volatile Gui instance;

  private static int frameTime=10;//use a bigger or smaller number for faster/slower simulation
  private static int stepsForFrame=20;//use a bigger or smaller number for faster/slower simulation
  //it will attempt to do 4 steps every 20 milliseconds (less if the machine is too slow)

  public static volatile ScheduledExecutorService scheduler;

  Model m;

  Gui(Model m) {
    this.m = m;
    instance = this;
  }
  public void run() {
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    getRootPane().setLayout(new BorderLayout());
    JPanel p=new Canvas(m);
    getRootPane().add(p,BorderLayout.CENTER);
    pack();
    setVisible(true);
    scheduler.scheduleAtFixedRate(
      ()->SwingUtilities.invokeLater(()->repaint()),
      500,25, TimeUnit.MILLISECONDS
      );
    }

  /**
   * Hacky mc hack face
   */
  public void forceClose() {
    scheduler.shutdownNow();
    setVisible(false);
    dispose();
  }

  private static final class MainLoop implements Runnable {
    Model m;
    java.util.Queue<Long> times = new ArrayDeque<>(10);
    long startTime = System.currentTimeMillis();
    int stepsSinceStart = 0;

    MainLoop(Model m){this.m=m;}

    public void run() {
      try{
        while(!Thread.currentThread().isInterrupted()){
          long ut=System.currentTimeMillis();
          for (int i = 0; i < stepsForFrame; i++) {
            m.step();
            stepsSinceStart++;
          }
          ut=System.currentTimeMillis()-ut;//used time
          System.out.printf(
            "Particles: % 5d,\ttime: % 5d,\tstep: % 5d",
            m.p.size(),
            ut,
            stepsSinceStart
          );
          times.add(ut);
          if (times.size() > 15) times.remove();
          printAvgTime();
          System.out.println();

          Timer.INSTANCE.printResults();

          long sleepTime=frameTime-ut;
          if(sleepTime>1){
            Thread.sleep(sleepTime);
          }
        }//if the step was short enough, it wait to make it at least frameTime long.
      }
      catch(InterruptedException t) {
      }
      catch(Throwable t){//not a perfect solution, but
        t.printStackTrace();//makes sure you see the error and the program dies.
//        System.exit(0);//the "right" solution is much more involved
      }//and would require storing and passing the exception between different objects.
    }

    private void printAvgTime() {
      if (times.size() < 10) return;

      double avgTime = MainKt.avgTime(times);
      long timeSinceStart = (System.currentTimeMillis() - startTime) / 1000;
      System.out.printf(",\tAvg time: % 5d,\tTimeSinceStart: % 5d", (int) avgTime, timeSinceStart);
      if (stepsSinceStart >= 2000) {
        System.out.print(",\t<-- Use these measurements for testing");
        staticAvgTime = (int) avgTime;
      }
    }
  }
  public static void main(String[] args) {
    Timer.INSTANCE.reset();
    Gui.staticAvgTime = -1;

    boolean isParallel = false;
    if (Arrays.asList(args).contains("--parallel")) {
      isParallel = true;
    }

    Model m = createModel(isParallel);

    scheduler = Executors.newScheduledThreadPool(2);
    scheduler.schedule(new MainLoop(m), 500, TimeUnit.MILLISECONDS);
    SwingUtilities.invokeLater(new Gui(m));
  }

  public static Model createModel(boolean isParallel) {
    //Model m=DataSetLoader.getRegularGrid(100, 800, 40, isParallel);//Try those configurations
    Model m=DataSetLoader.getRandomRotatingGrid(100, 800, 32, isParallel);
//    Model m=DataSetLoader.getRandomRotatingGrid(100, 800, 40, isParallel);
    //Model m=DataSetLoader.getRandomSet(100, 800, 1//000, isParallel);
//    Model m=DataSetLoader.getRandomSet(100, 800, 100, isParallel);
//    Model m=DataSetLoader.getRandomSet(100, 800, 500, isParallel);
    //Model m=DataSetLoader.getRandomGrid(100, 800, 30, isParallel);
    return m;
  }
}