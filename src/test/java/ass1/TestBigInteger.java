package ass1;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;


public class TestBigInteger {

  public static final BigInteger[][] dataset={
    {new BigInteger("999101101"),new BigInteger("999101201"),new BigInteger("999101301"),new BigInteger("999101401"),new BigInteger("999101501")},
    {new BigInteger("999191101"),new BigInteger("999181201"),new BigInteger("999171301"),new BigInteger("999161401"),new BigInteger("999151501")},
    {new BigInteger("-999101101"),new BigInteger("-999101201"),new BigInteger("999101301"),new BigInteger("-999101401"),new BigInteger("999101501")},
    {new BigInteger("0"),new BigInteger("-999101201"),new BigInteger("1"),new BigInteger("-999101401"),new BigInteger("999101501")},
    {},
    manyOrdered(10000),
    manyReverse(10000),
    manyRandom(10000)
  };
  static private BigInteger[] manyRandom(int size) {
    Random r=new Random(0);
    BigInteger[] result=new BigInteger[size];
    for(int i=0;i<size;i++){result[i]=new BigInteger(250,r);}
    return result;
  }
  static private BigInteger[] manyReverse(int size) {
    BigInteger[] result=new BigInteger[size];
    for(int i=0;i<size;i++){result[i]=new BigInteger("99999"+(size-i));}
    return result;
  }
  static private BigInteger[] manyOrdered(int size) {
    BigInteger[] result=new BigInteger[size];
    for(int i=0;i<size;i++){result[i]=new BigInteger("99999"+i);}
    return result;
  }

  @Test
  public void testISequentialSorter() {
    Sorter s=new ISequentialSorter();
    for(BigInteger[]l:dataset){TestHelper.testData(l,s);}
  }
  @Test
  public void testMSequentialSorter() {
    Sorter s=new MSequentialSorter();
    for(BigInteger[]l:dataset){TestHelper.testData(l,s);}
  }
  @Test
  public void testMParallelSorter1() {
    Sorter s=new MParallelSorter1();
    for(BigInteger[]l:dataset){TestHelper.testData(l,s);}
  }
  @Test
  public void testMParallelSorter2() {
    Sorter s=new MParallelSorter2();
    for(BigInteger[]l:dataset){TestHelper.testData(l,s);}
  }

}
