package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * An implementation of 'power of 2' based divide and conquer recursive
 * interleaver. Input is interleaved by largest power of two number of elements
 * at a time. This interleaver may be preferred if the total input size is
 * an exact power of 2 for in-shuffles and 2^k +1 for out-shuffles.
 *
 * @see <a href="https://cs.stackexchange.com/a/403">
 * https://cs.stackexchange.com/a/403</a>
 * @see Interleaver
 */
public final class RecursiveInterleaver extends AbstractInterleaver {

  /**
   * No-arg constructor provided for use by {@link Interleavers} which creates
   * single instances. Use {@link Interleavers#RECURSIVE}
   */
  RecursiveInterleaver() {}

  protected void interleave(Object[] array, int from, int to) {
    while (to - from > 1) {
      final int size = to - from;

      int midpt = size / 2;
      int k = Integer.highestOneBit(midpt);

      // when the list size isn't a power of 2
      if (k != midpt) {
        // rotate the difference out of the way
        Util.rotate(array, from + k, from + k + midpt, k - midpt);
      }
      // continue with interleaving the front 2k of the list
      int base = 0;
      int m = k;
      while(base < 2*k-1) {
        int fb = from + base;
        // swap all in first half of list (k elements)
        for (int i = 0; i < m; i++) {
          Util.swap(array, fb + i, fb + m + Util.a025480(i));
        }

        // unscramble back half of list
        for (int j = 1; j <= m / 4; j <<= 1) {
          if (j < 2) {
            Util.swap(array, fb + m, fb + m + 1);
          } else {
            interleave(array, fb + m, fb + m + 2 * j);
          }
        }
        base += m;
        m/=2;
      }
      // re-interleave the back of this front 2k section
      from += 2 * k;
    }
  }
  
  protected void interleave(List<?> list) {
    while (list.size() > 1) {
      int size = list.size();
      if(size < 4){
        Collections.swap(list,0,1);
        break;
      }
      int midpt = size / 2;
      int k = Integer.highestOneBit(midpt);
      // when the list size isn't a power of 2, handle the back end first.
      if (k != midpt) {
        // rotate the difference out of the way
        Collections.rotate(list.subList(k, k + midpt), k - midpt);
      }
      // continue with interleaving the front 2k of the list
      int base = 0;
      int m = k;
      while(base < 2*k-1) {
        // swap all in first half of list (k elements)
        for (int i = 0; i < m; i++) {
          Collections.swap(list, base + i,base + m + Util.a025480(i));
        }
        // unscramble back half of list (
        for (int j = 1; j <= m / 4; j <<= 1) {
          if (j < 2) {
            Collections.swap(list, base + m,base + m + 1);
          } else {
            interleave(list.subList(base + m, base + m + 2 * j));
          }
        }
        base += m;
        m/=2;
      }
      // interleave remainder of list we rotated earlier
      if(2*k >= size) {
        break;
      }
      list = list.subList(2 * k, size);
    }
  }


  protected <T> void interleave(List<T> a, List<T> b) {
    assert !a.isEmpty() : "Lists should not be empty.";
    assert a.size() == b.size() : "Lists should be equal sizes at start.";

    int size = a.size();

    if (size == 1) {
      a.set(0, b.set(0, a.get(0)));
    } else {

      // work only on lists whose size is a power of two.
      int k = Integer.highestOneBit(size);

      // swap upto k elements in list a
      for (int i = 0; i < k; i++) {
        a.set(i, b.set(Util.a025480(i), a.get(i)));
      }
      // unscramble k/2 elements of list b
      for (int j = 1; j <= k / 4; j <<= 1) {
        if (j < 2) {
          Collections.swap(b, 0, 1);
        } else {
          interleave(b.subList(0, j * 2));
        }
      }
      // recurse interleave this section of list b with single list interleave
      interleave(b.subList(0, k));

      // Process remainders of each list if minSize wasn't a power of 2.
      if (k != size) {
        // Rotate un-interleaved remainder of A to back of already interleaved b
        Util.rotate(a.subList(k, size), b.subList(0, k), k - size);
        // finish this remainder in a one list interleave.
        interleave(b.subList(2 * k - size, size));
      }
    }
  }


  protected <T> void interleave(T[] a, int fromA, int toA,
                              T[] b, int fromB, int toB) {
    assert toA - fromA != 0 : "Lists should not be empty.";
    assert toA - fromA == toB - fromB: "Lists should be equal sizes at start.";

    int size = toA - fromA;

    if (size == 1) {
      Util.swap(a, fromA, b, fromB);
    } else {
      int k = Integer.highestOneBit(size);

      for (int i = 0; i < k; i++) {
        Util.swap(a, fromA + i, b, fromB + Util.a025480(i));
      }

      for (int j = 1; j <= k / 4; j <<= 1) {
        if (j < 2) {
          Util.swap(b, fromB, fromB + 1);
        } else {
          interleave(b, fromB, fromB + (2 * j));
        }
      }

      interleave(b, fromB, fromB + k);

      if (k != size) {
        Util.rotate(a, fromA + k, toA,
                    b, fromB    , fromB + k, k - size);
        interleave(b, fromB + 2 * k - size, toB);
      }
    }

  }
}
