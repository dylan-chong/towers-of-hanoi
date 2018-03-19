package ass1;

import java.util.List;
/*
 * An interface offering a uniform access
 * to multiple possible sorting techniques.
 * list *must* be untouched by the algorithm,
 * so the sorting will be *not in place*.
 * */
public interface Sorter {
  <T extends Comparable<? super T>> List<T> sort(List<T> list);
  }
