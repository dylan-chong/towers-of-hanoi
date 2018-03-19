package ass1;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class TestPerformance {
  long timeOf(Runnable r,int warmUp,int runs) {
    System.gc();
    for(int i=0;i<warmUp;i++) {r.run();}
    long time0=System.currentTimeMillis();
    for(int i=0;i<runs;i++) {r.run();}
    long time1=System.currentTimeMillis();
    return time1-time0;
  }
  <T extends Comparable<? super T>>void msg(Sorter s,String name,T[][] dataset) {
    long time=timeOf(()->{
      for(T[]l:dataset){s.sort(Arrays.asList(l));}
      },20000,200);//realistically 20.000 to make the JIT do his job..
      System.out.println(name+" sort takes "+time/1000d+" seconds");
    }
  <T extends Comparable<? super T>>void msgAll(T[][] dataset) {
    //msg(new ISequentialSorter(),"Sequential insertion",TestBigInteger.dataset);//so slow
    //uncomment the former line to include performance of ISequentialSorter
    msg(new MSequentialSorter(),"Sequential merge sort",dataset);
    msg(new MParallelSorter1(),"Parallel merge sort (futures)",dataset);
    msg(new MParallelSorter2(),"Parallel merge sort (forkJoin)",dataset);
    }
  @Test
  void testBigInteger() {
    System.out.println("On the data type BigInteger");
    msgAll(TestBigInteger.dataset);
    }
  @Test
  void testFloat() {
    System.out.println("On the data type Float");
    msgAll(TestFloat.dataset);
    }
  @Test
  void testPoint() {
    System.out.println("On the data type Point");
    msgAll(TestPoint.dataset);
    }
  }