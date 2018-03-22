package ass1;

import java.util.Random;

import org.junit.jupiter.api.Test;


public class TestFloat {

  public static final Float[][] dataset={
    {1f,2f,3f,4f,5f,6f},
    {7f,6f,5f,4f,3f,2f},
    {-7f,6f,-5f,-4f,3f,-2f},
    {7f/0f,-6f/0f,5f,4f,3f,2f},
    {7f/0f,0f/0f,0f/0f,0f/0f,0f/0f,-5f/0f,4f,3f,2f},
    {},
    manyOrdered(10000),
    manyReverse(10000),
    manyRandom(10000)
  };
  static private Float[] manyRandom(int size) {
    Random r=new Random(0);
    Float[] result=new Float[size];
    for(int i=0;i<size;i++){result[i]=r.nextFloat();}
    return result;
  }
  static private Float[] manyReverse(int size) {
    Float[] result=new Float[size];
    for(int i=0;i<size;i++){result[i]=(size-i)+0.42f;}
    return result;
  }
  static private Float[] manyOrdered(int size) {
    Float[] result=new Float[size];
    for(int i=0;i<size;i++){result[i]=i+0.42f;}
    return result;
  }

  @Test
  public void testISequentialSorter() {
    Sorter s=new ISequentialSorter();
    for(Float[]l:dataset){TestHelper.testData(l,s);}
  }

  @Test
  public void testMSequentialSorter() {
    Sorter s=new MSequentialSorter();
    for(Float[]l:dataset){TestHelper.testData(l,s);}
  }
  @Test
  public void testMParallelSorter1() {
    Sorter s=new MParallelSorter1();
    for(Float[]l:dataset){TestHelper.testData(l,s);}
  }
  @Test
  public void testMParallelSorter2() {
    Sorter s=new MParallelSorter2();
    for(Float[]l:dataset){TestHelper.testData(l,s);}
  }

}
