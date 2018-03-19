package ass1;

import java.util.Random;

import org.junit.jupiter.api.Test;


class Point implements Comparable<Point>{
  public Point(long x, long y) {
    super();
    this.x = x;
    this.y = y;
  }
  long x;
  long y;
  @Override
  public int compareTo(Point other) {
    return new Long(this.x*this.x+this.y*this.y).compareTo(other.x*other.x+other.y*other.y);
  }

}
public class TestPoint {

  static public Point[][] dataset={
    {new Point(10,10),new Point(20,30),new Point(30,30),new Point(40,40),new Point(50,50),new Point(60,60)},
    {new Point(110,10),new Point(120,3),new Point(130,3),new Point(140,140),new Point(-50,150),new Point(3260,-260)},
    {new Point(0,0),new Point(0,3),new Point(-130,3),new Point(-140,-140)},
    {},
    manyOrdered(10000),
    manyReverse(10000),
    manyRandom(10000)
  };
  static private Point[] manyRandom(int size) {
    Random r=new Random(0);
    Point[] result=new Point[size];
    for(int i=0;i<size;i++){result[i]=new Point(r.nextLong(),r.nextLong());}
    return result;
  }
  static private Point[] manyReverse(int size) {
    Point[] result=new Point[size];
    for(int i=0;i<size;i++){result[i]=new Point(size-i,size*3-i);}
    return result;
  }
  static private Point[] manyOrdered(int size) {
    Point[] result=new Point[size];
    for(int i=0;i<size;i++){result[i]=new Point(i*3,i*2);}
    return result;
  }

  @Test
  public void testISequentialSorter() {
    Sorter s=new ISequentialSorter();
    for(Point[]l:dataset){TestHelper.testData(l,s);}
  }
  @Test
  public void testMSequentialSorter() {
    Sorter s=new MSequentialSorter();
    for(Point[]l:dataset){TestHelper.testData(l,s);}
  }
  @Test
  public void testMParallelSorter1() {
    Sorter s=new MParallelSorter1();
    for(Point[]l:dataset){TestHelper.testData(l,s);}
  }
  @Test
  public void testMParallelSorter2() {
    Sorter s=new MParallelSorter2();
    for(Point[]l:dataset){TestHelper.testData(l,s);}
  }

}
