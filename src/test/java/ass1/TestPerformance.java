package ass1;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test results:
 *
 * On the data type Float
 * Sequential merge sort sort takes 4.71 seconds
 * Parallel merge sort (futures) sort takes 6.626 seconds
 * Parallel merge sort (forkJoin) sort takes 2.321 seconds
 *
 * On the data type Point
 * Sequential merge sort sort takes 5.148 seconds
 * Parallel merge sort (futures) sort takes 7.002 seconds
 * Parallel merge sort (forkJoin) sort takes 2.83 seconds
 *
 * On the data type BigInteger
 * Sequential merge sort sort takes 4.937 seconds
 * Parallel merge sort (futures) sort takes 6.876 seconds
 * Parallel merge sort (forkJoin) sort takes 2.691 seconds
 *
 * On the data type Person
 * Sequential merge sort sort takes 2.09 seconds
 * Parallel merge sort (futures) sort takes 3.584 seconds
 * Parallel merge sort (forkJoin) sort takes 1.122 seconds
 *
 * ---
 *
 * As expected, the fork join beats the futures, probably due to the efficient
 * work stealing versus creating more threads.
 *
 * Surprisingly the parallel merge sort using futures, using a cached thread
 * pool is slower than sequential. The cached thread pool, when all threads
 * are waiting, creates another thread (very expensive). Using a forkjoin
 * thread pool for parallel merge sort 1 (which is dynamically sized) reaches
 * the thread limit and throws execution exception. Forkjoin pool with forkjoin
 * tasks uses 'helping' (a blocked worker can work on a task that it requires).
 */
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
    List<Person> baseList = new ArrayList<>();

    for (int i = 0; i < 1000; i++) {
      baseList.addAll(PersonDataset.INSTANCE.getBaseList());
    }

    msgAll(new Person[][]{
      baseList.toArray(new Person[baseList.size()])
    });
  }
}