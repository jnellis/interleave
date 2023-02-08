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
public final class RecursiveInterleaver implements Interleaver {

  /**
   * No-arg constructor provided for use by {@link Interleavers} which creates
   * single instances. Use {@link Interleavers#RECURSIVE}
   */
  RecursiveInterleaver() {}

  public <T> void interleave(T[] array, int from, int to, Shuffle shuffle) {
    int size = to - from;
    if (size > 1) {
      if (shuffle.out) { // out-shuffle
        if (size == 2) { return; } // too small, no change
        from++; size--;
      }
      if (shuffle.folding) {
        Util.reverse(array, from + (size / 2), to);
      }
      interleave(array, from, to);
    }
  }

  private static <T> void interleave(T[] arr, int from, int to) {
    while (to - from > 1) {
      int midpt = (to - from) >> 1;
      int k = Integer.highestOneBit(midpt);

      // when the list size isn't a power of 2, handle the back end first.
      if (k != midpt) {
        // rotate the difference out of the way
        Util.rotate(arr, from + k, to, k - midpt);
        // interleave the rest of the list
        // todo: this can be forked
        interleave(arr, from + 2 * k + 1, to);
      }
      // continue with interleaving the front 2k of the list

      // swap all in first half of list (k elements)
      for (int i = 0; i < k; i++) {
        Util.swap(arr, from + i, from + k + Util.a025480(i));
      }

      // unscramble back half of list
      for (int j = 1; j <= k / 4; j <<= 1) {
        interleave(arr, from + k, from + k + 2 * j);
      }
      if(k == 2){
        Util.swap(arr, from + k , from + k + 1);
        return;
      }
      // re-interleave the back of this front 2k section
      from += k;
      to = from + k;
    }
  }

  @Override
  public <T> void interleave(List<T> list, Shuffle shuffle) {
    int size = list.size();
    if (size > 1) {
      if (shuffle.out) {
        if (size == 2) { return; } // too small, no change
        list = list.subList(1, size--);
      }
      if (shuffle.folding) {
        Collections.reverse(list.subList(size / 2, size));
      }
      interleave(list);
    }
  }

  private static <T> void interleave(List<T> list) {
    while (list.size() > 1) {
      int size = list.size();
      int midpt = size / 2;
      int k = Integer.highestOneBit(midpt);
      // when the list size isn't a power of 2, handle the back end first.
      if (k != midpt) {
        // rotate the difference out of the way
        Collections.rotate(list.subList(k, size), k - midpt);
        // interleave the rest of the list
        // todo: this can be forked if the list is concurrent
        interleave(list.subList(2 * k + 1, size));
      }
      // continue with interleaving the front 2k of the list

      // swap all in first half of list (k elements)
      for (int i = 0; i < k; i++) {
        Collections.swap(list, i, k + Util.a025480(i));
      }
      // unscramble back half of list (
      for (int j = 1; j <= k / 4; j <<= 1) {
        if (j < 2) {
          Collections.swap(list, k, k + 1);
        } else {
          interleave(list.subList(k, k + 2 * j));
        }
      }
      // re-interleave the back of this front 2k section
      list = list.subList(k, 2 * k);
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
    assert aSize != 0 : "List A can not be empty.";

    if (aSize + bSize == 2) {
      a.set(0, b.set(0, a.get(0)));
    } else {
      int minSize = Math.min(aSize, bSize);
      // work only on lists whose size is a power of two.
      int k = Integer.highestOneBit(minSize);

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
      if (minSize > k) {
        // First get everything into the second list, it is guaranteed to fit.
        // Rotate un-interleaved remainder of A to back of B, and some
        // interleaved elements at start of B, to back of A.
        Util.rotate(a.subList(k, aSize), b, k - aSize);
        // At this point, List A remainder elements are behind List B remainder
        // elements, effectively swapping the order of the lists from whence
        // started. We have a small optimization to skip one element now by
        // adding 1 to the next starting index, in addition to skipping
        // the remaining interleave if the first sublist would be less than two.
        int nextStart = 2 * k - minSize + 1;
        if (bSize - nextStart > 1) {
          // can use single list interleave now
          interleave(b.subList(nextStart, bSize));
        }
      }
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
        Util.rotate(b, fromB, toB, minSize - toB);
        // reverse the rest
        Util.reverse(b, fromB, minSize);
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
                              T[] b, int fromB, int toB) {
    int aSize = toA - fromA, bSize = toB - fromB;
    assert aSize != 0 : "List A can not be empty.";

    if (aSize + bSize == 2) {
      Util.swap(a, fromA, b, fromB);
    } else {
      int minSize = Math.min(aSize, bSize);
      int k = Integer.highestOneBit(minSize);

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

      if (minSize > k) {
        Util.rotate(a, fromA + k, toA,
                    b, fromB, toB, k - aSize);
        int nextStart = 2 * k - minSize + 1;
        if (bSize - nextStart > 1) {
          interleave(b, fromB + nextStart, toB);
        }
      }
    }
  }
}
