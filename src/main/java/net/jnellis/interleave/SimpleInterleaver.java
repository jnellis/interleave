package net.jnellis.interleave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple interleaving of elements by creating a new collection on the heap,
 * then adding alternating elements to it. Finalizes by copying the elements
 * back to the original collection(s).
 */
public class SimpleInterleaver extends AbstractInterleaver{

  @Override
  public void interleave(List<?> list) {
    zipListHalves(list);
  }

  @SuppressWarnings("unchecked")
  <T> void zipListHalves(List<T> list) {
    int midpt = list.size() / 2;
    T[] temp = (T[]) list.toArray();
    for (int i = 0, k = 0; k < midpt; i += 2, k++) {
      list.set(i, temp[midpt + k]);
      list.set(i + 1, temp[k]);
    }
  }

  @Override
  public void interleave(Object[] array, int from, int to ) {
    int halfSize = (to - from)/2;
    Object[] temp = new Object[halfSize<<1];
    // copy first to prime caches
    System.arraycopy(array,from,temp,0,temp.length);
    // write back interleaving
    for (int i = 0, k = 0; k < halfSize; i+=2, k++) {
      array[from + i] = temp[halfSize + k];
      array[from + i+1] = temp[k];
    }
  }

  @Override
  public <T> void interleave(List<T> a, List<T> b ) {
    assert !a.isEmpty() : "Lists should not be empty.";
    assert a.size() == b.size() : "Lists should be equal sizes at start.";
    int size = a.size();

    List<T> l = new ArrayList<>(size << 1);
    for (int i = 0; i < size; i++) {
      l.add(b.get(i));
      l.add(a.get(i));
    }
    Collections.copy(a, l.subList(0, size));
    Collections.copy(b, l.subList(size, size << 1));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> void interleave(T[] a, int fromA, int toA,
                             T[] b, int fromB, int toB ) {
    assert toA - fromA != 0 : "Lists should not be empty.";
    assert toA - fromA == toB - fromB : "Lists should be equal sizes at start.";
    int size = toA - fromA;
    T[] temp = (T[])new Object[size << 1];
    for (int i = 0, k = 0; k < size; i += 2, k++) {
      temp[i] = b[fromB + k];
      temp[i + 1] = a[fromA + k];
    }

    System.arraycopy(temp, 0, a, fromA, size);
    System.arraycopy(temp, size, b, fromB, size);
  }
}
