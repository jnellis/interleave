package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * An implementation of 'power of 2' based divide and conquer recursive
 * interleaver. Input is interleaved by largest power of two number of elements
 * at a time. This interleaver may be preferred if the total input size is
 * an exact power of 2.
 *
 * @see <a href="https://cs.stackexchange.com/a/403">
 * https://cs.stackexchange.com/a/403</a>
 * @see Interleaver
 */
public final class RecursiveInterleaver implements Interleaver {

  /**
   * No-arg constructor provided for use by {@link Interleavers} which creates
   * single instances. Use {@link Interleavers#RECURSIVE}
   */
  RecursiveInterleaver() {}

  public <T> void interleave(T[] arr, int from, int to, Shuffle shuffle) {
    int size = to - from;
    if (size > 1) {
      if (shuffle.out) { // out-shuffle
        if (size == 2) {
          return;
        } // too small, no change
        from++; size--;
      }
      if (shuffle.folding) {
        Util.reverse(arr, from + (size / 2), to);
      }
      interleave(arr, from, to);
    }
  }
  
  private static <T> void interleave(T[] arr, int from, int to) {
    int n = (to - from) >> 1;
    if (n == 1) {
      Util.swap(arr, from, from + 1);
    } else {
      // Only process 'powers of 2' amounts of the list at a time.
      int k = Integer.highestOneBit(n);

      if (n != k) {
        // because not power of 2, rotate the part we wish interleave into view
        Util.rotate(arr, from + k, from + k + n, k - n);
        interleave(arr, from, arr, from + k, k);
        // recursively finish the remainder
        interleave(arr, from + k + k, to);
      } else {
        interleave(arr, from, arr, from + k, k);
      }
    }
  }

  private static <T> void interleave(T[] a, int fromA,
                                     T[] b, int fromB, int n) {
    while(true){
      if (n == 1) {
        Util.swap(a, fromA, b, fromB); // swap
        return;
      }
      // swap all in list a
      for (int i = 0; i < n; i++) {
        Util.swap(a, fromA + i, b, fromB + Util.a025480(i));
      }
      // unscramble first half of list b
      for (int j = 1; j <= n / 4; j <<= 1) {
        if (j < 2) {
          Util.swap(b, fromB, fromB + 1);
        } else {
          interleave(b, fromB, b, fromB + j, j);
        }
      }
      // reprocess list b
      a = b;
      fromA = fromB;
      fromB = fromB + n / 2;
      n /= 2;
    }
  }

  @Override
  public <T> void interleave(List<T> list, Shuffle shuffle) {
    int size = list.size();
    if (size > 1) {
      if (shuffle.out) {
        if (size == 2) {  return;  } // too small, no change
        list = list.subList(1, size--);
      }
      if (shuffle.folding) {
        Collections.reverse(list.subList(size / 2, size));
      }
      interleave(list.subList(0, size / 2), list.subList(size / 2, size));
    }
  }


  @Override
  public <T> void interleave(List<T> a, List<T> b, Shuffle shuffle) {
    int minSize = Math.min(a.size(), b.size());
    if(minSize > 0) {
      if (shuffle.folding) {
        // rotate non-interleaved items to the back
        Collections.rotate(b, minSize - b.size());
        // reverse the rest
        Collections.reverse(b.subList(0, minSize));
      }
      if (shuffle.out) {
        if (minSize > 1) { // never call with an empty first list.
          interleave(a.subList(1, minSize), b.subList(0, minSize - 1));
        }
      } else {
        interleave(a.subList(0, minSize), b.subList(0, minSize));
      }
    }
  }

  private <T> void interleave(List<T> a, List<T> b) {
    int aSize = a.size(), bSize = b.size();

    if (aSize == 0) {
      throw new IllegalStateException(  /*todo remove this*/
          "list 'a' should not be empty. Report this bug with your input.");
    } else if (aSize + bSize == 2) {
      a.set(0, b.set(0, a.get(0)));
    } else {
      int minSize = Math.min(aSize, bSize);
      // work only on lists whose size is a power of two.
      int k = Integer.highestOneBit(minSize);

      interleave(a, b, k);
      // process the remaining part of both lists
      if (minSize > k) {
        // First get everything into the second list, it is guaranteed to fit.
        // Rotate un-interleaved remainder of A to back of B, and some
        // interleaved elements at start of B, to back of A.
        Util.rotate(a.subList(k, aSize), b, k - aSize);
        // At this point, List A remainder elements are behind List B remainder
        // elements, effectively swapping the order of the lists from whence
        // started. We have a small optimization to skip one element now by
        // adding 1 to the next starting index, in addition to skipping
        // the remaining interleave if the first sublist would be empty.
        int nextStart = 2 * k - minSize + 1;
        int midpt = nextStart + (bSize - (nextStart)) / 2;
        if (nextStart != midpt) {
          interleave(b.subList(nextStart, midpt), b.subList(midpt, bSize));
        }
      }
    }
  }

  private static <T> void interleave(List<T> a, List<T> b, int n) {
    while (true) {
      if (n == 1) {
        a.set(0, b.set(0, a.get(0))); // swap and skedaddle
        return;
      }
      // swap all in list a
      for (int i = 0; i < n; i++) {
        a.set(i, b.set(Util.a025480(i), a.get(i)));
      }
      // unscramble first half of list b
      for (int j = 1; j <= n / 4; j <<= 1) {
        if (j < 2) {
          Collections.swap(b, 0, 1);
        } else {
          interleave(b.subList(0, j), b.subList(j, j * 2), j);
        }
      }
      // Split b into two parts and re-interleave
      a = b.subList(0, n / 2);
      b = b.subList(n / 2, n);
      n /= 2;
    }
  }

  @Override
  public <T> void interleave(T[] a, int fromA, int toA,
                             T[] b, int fromB, int toB,
                             Shuffle shuffle) {
    int minSize = Math.min(toA - fromA, toB - fromB);
    if (minSize > 0) {
      if (shuffle.folding) {
        // rotate non-interleaved items to the back
        Util.rotate(b, minSize - b.length);
        // reverse the rest
        Util.reverse(b, 0, minSize);
      }
      if (shuffle.out) { // out-shuffle
        if (minSize > 1) {
          interleave(a, fromA + 1, fromA + minSize,
                     b, fromB, fromB + minSize - 1);
        }
      } else {
        interleave(a, fromA, fromA + minSize, b, fromB, fromB + minSize);
      }
    }
  }

  private <T> void interleave(T[] a, int fromA, int toA,
                              T[] b, int fromB, int toB){
    int aSize = toA - fromA, bSize = toB - fromB;

    if(aSize == 0) {
      throw new IllegalStateException(  /*todo remove this*/
          "list 'a' should not be empty. Report this bug with your input.");
    } else if (aSize + bSize == 2) {
        Util.swap(a, fromA, b, fromB);
    } else {
      int minSize = Math.min(aSize, bSize);
      // work only on lists whose size is a power of two.
      int k = Integer.highestOneBit(minSize);

      interleave(a, fromA, b, fromB, k);
      // process the remaining part of both lists
      if (minSize > k) {
        // First get everything into the second list, it is guaranteed to fit.
        // Rotate un-interleaved remainder of A to back of B, and some
        // interleaved elements at start of B, to back of A.
        Util.rotate(a,fromA+k,toA,
                    b,fromB,toB, k-aSize);
        // At this point, List A remainder elements are behind List B remainder
        // elements, effectively swapping the order of the lists from whence
        // started. We have a small optimization to skip one element now by
        // adding 1 to the next starting index, in addition to skipping
        // the remaining interleave if the first sublist would be empty.
        int nextStart = 2 * k - minSize + 1;
        int midpt = nextStart + (bSize - nextStart) / 2;
        if(nextStart != midpt) {
          interleave(b, fromB + nextStart, fromB + midpt,
                     b, fromB + midpt, toB);
        }
      }
    }
  }


}
