package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * An implementation of
 * <a href="https://arxiv.org/pdf/0805.1598.pdf">
 * A Simple In-Place Algorithm for In-Shuffle</a>
 *
 * @see <a href="https://cs.stackexchange.com/a/400">
 * https://cs.stackexchange.com/a/400</a>
 * @see Interleaver
 */
public final class PermutationInterleaver implements Interleaver {

  /**
   * No-arg constructor provided for use by {@link Interleavers} which creates
   * single instances. Use {@link Interleavers#PERMUTATION}
   */
  PermutationInterleaver() {}

  @Override
  public void interleave(List<?> list, Shuffle shuffle) {
    if(list.size() > 1) {
      if (shuffle.out) { // out-shuffle
        list = list.subList(1, list.size());
      }
      if (shuffle.folding) {
        Collections.reverse(list.subList(list.size() / 2, list.size()));
      }
      interleave(list);
    }
  }

  // single list in-shuffle
  private static <T> void interleave(List<T> list) {
    while (true) {
      final int size = list.size();
      if (size <= 2) { // bail out when size is 2 or less
        if (size == 2) { // if two elements left, swap them on the way out.
          Collections.swap(list, 0, 1);
        }
        return;
      }

      final Constants c = new Constants(size);
      // rotate the correct 2nd m amount of elements into position to be swapped.
      Collections.rotate(list.subList(c.m, c.m + c.n), c.m);

      // For each i ∈ {0, 1, . . . , k − 1}, starting at 3i, do the cycle leader
      // algorithm for the in-shuffle permutation of order 2m elements
      for (int k = 0; k < c.k; k++) {
        int i = Util.POW3[k] - 1;
        T leader = list.get(i);
        do {
          i = (((i+1) * 2) % c.mod) - 1;
          leader = list.set(i, leader);
        } while (i != Util.POW3[k] - 1);
      }
      // shorten the list to work on remaining elements.
      list = list.subList(2 * c.m, size);
    }
  }

  @Override
  public void interleave(Object[] array, int from, int to, Shuffle shuffle) {
    if (shuffle.out) { // out-shuffle
      from++;
    }
    if (shuffle.folding) {
      Util.reverse(array, from + (to - from) / 2, to);
    }
    interleave(array, from, to);
  }

  private static <T> void interleave(T[] arr, int from, int to) {
    while (true) {
      int size = to - from;
      if (size <= 2) {
        if (size == 2) {
          Util.swap(arr, from, to - 1);
        }
        return;
      }

      final Constants c = new Constants(size);
      Util.rotate(arr, from + c.m, from + c.m + c.n, c.m);

      for (int k = 0; k < c.k; k++) {
        int i = Util.POW3[k] - 1;
        T leader = arr[from + i ];
        do {
          i = (((i+1) * 2) % c.mod) - 1;
          leader = set(arr, from + i , leader);
        } while (i != Util.POW3[k] - 1);
      }

      from += (2 * c.m);
    }
  }

  /**
   * Similar to List.set. Sets the value at the specified index and returns
   * the old value that was at that index.
   *
   * @param array array to access
   * @param index index into array
   * @param value value to replace old value
   * @param <T>   type of array element
   * @return old value at that index
   */
  private static <T> T set(T[] array, int index, T value) {
    T oldValue = array[index];
    array[index] = value;
    return oldValue;
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
        if (minSize > 1) {
          interleave(a.subList(1, minSize), b.subList(0, minSize - 1));
        }
      } else {
        interleave(a.subList(0, minSize), b.subList(0, minSize));
      }
    }
  }

  private static <T> void interleave(List<T> a, List<T> b) {
    while (true) {
      int aSize = a.size(), bSize = b.size();
      if (aSize == 0) {
        interleave(b);
        return;
      } else if (aSize + bSize == 2) {
        a.set(0, b.set(0, a.get(0)));
        return;
      }

      Constants c = new Constants(aSize + bSize);
      if (c.m > aSize) {  // just rotate b side
        Collections.rotate(b.subList(c.m - aSize, c.m + c.n - aSize), c.m);
      } else {
        Util.rotate(a.subList(c.m, aSize), b.subList(0, c.m + c.n - aSize), c.m);
      }

      for (int k = 0; k < c.k; k++) {
        int i = Util.POW3[k] - 1;
        T leader = i < aSize ? a.get(i)
                             : b.get(i - aSize);
        do {
          i = (((i+1) * 2) % c.mod) - 1;
          leader = i < aSize ? a.set(i , leader)
                             : b.set(i - aSize, leader);
        } while (i != Util.POW3[k] - 1);
      }
      // adjust a & b to account for 2*m elements we just moved around
      if (aSize <= 2 * c.m) {
        a = Collections.emptyList();
        b = b.subList(2 * c.m - aSize, bSize);
      } else {
        a = a.subList(2 * c.m, aSize);
      }
    }

  }

  @Override
  public <T> void interleave(T[] a, int fromA, int toA,
                             T[] b, int fromB, int toB,
                             Shuffle shuffle) {
    int minSize = Math.min(toA - fromA, toB - fromB);
    if(minSize > 0) {
      if (shuffle.folding) {
        // rotate non-interleaved items to the back
        Util.rotate(b, minSize - toB);
        // reverse the rest
        Util.reverse(b, 0, minSize);
      }
      if (shuffle.out) { // out-shuffle
        interleave(a, fromA + 1, fromA + minSize,
                   b, fromB    , fromB + minSize - 1);
      } else {
        interleave(a, fromA, fromA + minSize,
                   b, fromB, fromB + minSize);
      }
    }
  }

  private static <T> void interleave(T[] a, int fromA, int toA,
                                     T[] b, int fromB, int toB) {
    while (true) {
      int aSize = toA - fromA, bSize = toB - fromB;
      if (aSize == 0) {
        interleave(b, fromB, toB);
        return;
      } else if (aSize + bSize == 2) {
        Util.swap(a, fromA, b, fromB);
        return;
      }

      Constants c = new Constants(aSize + bSize);
      final int _tob = fromB + c.m + c.n - aSize;
      if (c.m > aSize) {  // just rotate b side
        Util.rotate(b, fromB + c.m - aSize, _tob, c.m);
      } else {
        Util.rotate(a, fromA + c.m, toA, b, fromB, _tob, c.m);
      }

      for (int k = 0; k < c.k; k++) {
        int i = Util.POW3[k] - 1;
        T leader = i < aSize ? a[fromA + i]
                             : b[fromB + i - aSize];
        do {
          i = (((i + 1) * 2) % c.mod) - 1;
          leader = i < aSize ? set(a, fromA + i, leader)
                             : set(b, fromB + i - aSize, leader);
        } while (i != Util.POW3[k] - 1);
      }
      // adjust a & b to account for 2*m elements we just moved around
      if (aSize <= 2 * c.m) {
        fromA = toA;
        fromB += (2 * c.m) - aSize;
      } else {
        fromA += 2 * c.m;
      }
    }
  }

  /**
   * For each round of permutations swaps we need some constants for that
   * round that describe the amount of elements that we'll be processing.
   */
  private static class Constants {
    final int n, k, m, mod;

    Constants(final int size) {
      // Find a 2m = 3^k − 1 such that 3^k ≤ 2n < 3^(k+1)
      n = size / 2;  // half the size of the entire collection
      k = Util.ilog3(size); // minimum power of 3 elements to be working on
      mod = Util.POW3[k]; // cycle modulus
      m = (mod - 1) >> 1;  // m <= n, m is the number of elements moved at a time.
    }
  }
}