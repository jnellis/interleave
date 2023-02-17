package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * AbstractInterleaver holds some essential boilerplate for converting
 * out-shuffle and folding variants to in-shuffle operations. Implementations
 * only need to provide an in-shuffle algorithm implementation for each
 * collection type.
 */
public abstract class AbstractInterleaver implements Interleaver {

  private static void rangeCheck(Object[] array, int from, int to){
    if(from < 0){
      throw new IndexOutOfBoundsException("from index = "+ from);
    }
    if(from > to) {
      throw new IndexOutOfBoundsException("from index("+ from +
                                              ") > to index(" + to);
    }
    if(to > array.length){
      throw new IndexOutOfBoundsException("to index = "+ to);
    }
  }

  @Override
  public <T> void interleave(List<T> list, Shuffle shuffle) {
    if (list.size() > 1) {
      if (shuffle.out) {
        list = list.subList(1, list.size());
      }
      if (shuffle.folding) {
        Collections.reverse(list.subList(list.size() / 2, list.size()));
      }
      interleave(list);
    }
  }

  @Override
  public <T> void interleave(T[] array, int from, int to, Shuffle shuffle) {
    rangeCheck(array,from,to);
    int size = to - from;
    if (size > 1) {
      if (shuffle.out) {
        from++;
        size--;
      }
      if (shuffle.folding) {
        Util.reverse(array, from + (size / 2), to);
      }
      interleave(array, from, to);
    }
  }

  @Override
  public <T> void interleave(List<T> a, List<T> b, Shuffle shuffle) {
    int minSize = Math.min(a.size(), b.size());
    if (minSize > 0) {
      if (shuffle.folding) {
        // rotate non-interleaved items to the back
        Collections.rotate(b, minSize - b.size());
        // reverse the rest
        Collections.reverse(b.subList(0, minSize));
      }
      if (shuffle.out) {
        if (minSize > 1) {
          interleave(a.subList(1, minSize), b.subList(0, minSize - 1));
        }
      } else {
        interleave(a.subList(0, minSize), b.subList(0, minSize));
      }
    }
  }


  @Override
  public <T> void interleave(T[] a, int fromA, int toA,
                             T[] b, int fromB, int toB,
                             Shuffle shuffle) {
    rangeCheck(a,fromA,toA);
    rangeCheck(b,fromB,toB);
    int minSize = Math.min(toA - fromA, toB - fromB);
    if (minSize > 0) {
      if (shuffle.folding) {
        // rotate non-interleaved items to the back
        Util.rotate(b, fromB, toB, minSize - toB);
        // reverse the rest
        Util.reverse(b, fromB, minSize);
      }
      if (shuffle.out) {
        if (minSize > 1) {
          interleave(a, fromA + 1, fromA + minSize,
                     b, fromB,     fromB + minSize - 1);
        }
      } else {
        interleave(a, fromA, fromA + minSize,
                   b, fromB, fromB + minSize);
      }
    }
  }

  protected abstract <T> void interleave(List<T> list);

  protected abstract <T> void interleave(List<T> a, List<T> b);

  protected abstract <T> void interleave(T[] arr, int from, int to);

  protected abstract <T> void interleave(T[] a, int fromA, int toA,
                                         T[] b, int fromB, int toB);
 
}
