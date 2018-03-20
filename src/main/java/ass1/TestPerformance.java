package ass1;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class TestPerformance {

  /**
   * Gets the number of milliseconds required to run the given runnable runs
   * times.
   */
  long timeOf(Runnable r,int warmUp,int runs) {
    System.gc();
    for(int i=0;i<warmUp;i++) {r.run();}
    long time0=System.currentTimeMillis();
    for(int i=0;i<runs;i++) {r.run();}
    long time1=System.currentTimeMillis();
    return time1-time0;
  }

  /**
   * Prints this number of seconds required to sort the given data set with a
   * given sorter 200 times.
   */
  <T extends Comparable<? super T>>void msg(Sorter s,String name,T[][] dataset) {
    if (1>0)return; // todo dont commit
    long time=timeOf(()->{
      for(T[]l:dataset){s.sort(Arrays.asList(l));}
    },20000,200);//realistically 20.000 to make the JIT do his job..
    System.out.println(name+" sort takes "+time/1000d+" seconds");
  }

  /**
   * Same method as msg() but repeats with several different sorters
   */
  <T extends Comparable<? super T>>void msgAll(T[][] dataset) {
    //msg(new ISequentialSorter(),"Sequential insertion",TestBigInteger.dataset);//so slow
    //uncomment the former line to include performance of ISequentialSorter
    msg(new MSequentialSorter(),"Sequential merge sort",dataset);
    msg(new MParallelSorter1(),"Parallel merge sort (futures)",dataset);
    msg(new MParallelSorter2(),"Parallel merge sort (forkJoin)",dataset);
  }

  /**
   * Run the benchmarks on the big integer data set
   */
  @Test
  public void testBigInteger() {
    System.out.println("On the data type BigInteger");
    msgAll(TestBigInteger.dataset);
  }

  /**
   * Run the benchmarks on the float data set
   */
  @Test
  public void testFloat() {
    System.out.println("On the data type Float");
    msgAll(TestFloat.dataset);
  }

  /**
   * Run the benchmarks on the point data set
   */
  @Test
  public void testPoint() {
    System.out.println("On the data type Point");
    msgAll(TestPoint.dataset);
  }

  /**
   * Run the benchmarks on the point data set
   */
  @Test
  public void testPerson() {
    System.out.println("On the data type Person");
    List<Person> baseList = PersonDataset.baseList;
    msgAll(new Person[][]{
      baseList.toArray(new Person[baseList.size()])
    });
  }
}