package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * AbstractInterleaver holds some essential boilerplate for converting
 * out-shuffle and folding variants to in-shuffle operations. Implementations
 * only need to provide an in-shuffle algorithm implementation for each
 * collection type.
 */
public abstract class AbstractInterleaver implements Interleaver {

  @Override
  public  void interleave(List<?> list, Shuffle shuffle) {
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
  public void interleave(Object[] array, int from, int to, Shuffle shuffle) {
    Objects.checkFromToIndex(from,to,array.length);
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
    Objects.checkFromToIndex(fromA,toA,a.length);
    Objects.checkFromToIndex(fromB,toB,b.length);
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

  protected abstract void interleave(List<?> list);

  protected abstract <T> void interleave(List<T> a, List<T> b);

  protected abstract void interleave(Object[] arr, int from, int to);

  protected abstract <T> void interleave(T[] a, int fromA, int toA,
                                         T[] b, int fromB, int toB);
 
}
