package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * User: Joe Nellis Date: 5/16/2022 Time: 12:47 PM
 */
public class PermutationInterleaver implements Interleaver {

  @Override
  public <T> void interleave(List<T> list, Shuffle shuffle) {
    if (shuffle.out) { // out-shuffle
      list = list.subList(1, list.size());
    }
    if (shuffle.folding) {
      Collections.reverse(list.subList(list.size() / 2, list.size()));
    }
    interleave(list);
  }

  @Override
  public <T> void interleave(T[] array, Shuffle shuffle) {
    interleave(array, 0, array.length, shuffle);
  }

  @Override
  public <T> void interleave(T[] array, int from, int to, Shuffle shuffle) {
    if (shuffle.out) { // out-shuffle
      from++;
    }
    if (shuffle.folding) {
      Util.reverse(array, (array.length + from) / 2, array.length);
    }
    interleave(array, from, to);
  }

  private <T> void interleave(List<T> list) {
    int size = list.size();
    if (size == 2) { // swap
      list.set(0, list.set(1, list.get(0)));
      return;
    } else if (size < 2) {
      return;
    }
    // Find a 2m = 3^k − 1 such that 3^k ≤ 2n < 3^(k+1)
    int n = size / 2;
    int k = (int) (Math.log(size) / Math.log(3));
    int m = (int) (Math.pow(3, k) - 1) / 2;
    // Do a right cyclic shift of A[m + 1, . . . , n + m] by a distance m
    Collections.rotate(list.subList(m, m + n), m);
    // For each i ∈ {0, 1, . . . , k − 1}, starting at 3i, do the cycle leader
    // algorithm for the in-shuffle permutation of order 2m
    int mod = (int) Math.pow(3, k);
    int startIdx = 1;
    for (int i = 0; i < k; i++) {
      int idx = startIdx;
      T leader = list.get(startIdx - 1);
      do {
        idx <<= 1;
        if (idx >= mod)
          idx %= mod;
        leader = list.set(idx - 1, leader);
      } while (idx != startIdx);
      startIdx *= 3;
    }
    // Recursively do the in-shuffle algorithm on A[2m + 1, . . . , 2n]
    interleave(list.subList(2 * m, size));
  }

  private <T> void interleave(T[] arr, int from, int to) {
    int size = to - from;
    if (size == 2) { // swap
      Util.swap(arr, from, to - 1);
      return;
    } else if (size < 2) {
      return;
    }
    // Find a 2m = 3^k − 1 such that 3^k ≤ 2n < 3^(k+1)
    int n = size / 2;
    int k = (int) (Math.log(size) / Math.log(3));
    int m = (int) (Math.pow(3, k) - 1) / 2;
    // Do a right cyclic shift of A[m + 1, . . . , n + m] by a distance m
    Util.rotate(arr, from + m, from + m + n, m);
    // For each i ∈ {0, 1, . . . , k − 1}, starting at 3i, do the cycle leader
    // algorithm for the in-shuffle permutation of order 2m
    int mod = (int) Math.pow(3, k);
    int startIdx = 1;
    for (int i = 0; i < k; i++) {
      int idx = startIdx;
      T leader = arr[from + startIdx - 1];
      do {
        idx <<= 1;
        if (idx >= mod)
          idx %= mod;
        T temp = arr[from + idx - 1];
        arr[from + idx - 1] = leader;
        leader = temp;
      } while (idx != startIdx);
      startIdx *= 3;
    }
    // Recursively do the in-shuffle algorithm on A[2m + 1, . . . , 2n]
    interleave(arr, from + (2 * m), from + size);
  }

  @Override
  public <T> void interleave(List<T> a, List<T> b, Shuffle shuffle) {
    int minSize = Math.min(a.size(), b.size());
    if (shuffle.folding) {
      // rotate non-interleaved items to the back
      Collections.rotate(b, minSize - b.size());
      // reverse the rest
      Collections.reverse(b.subList(0, minSize));
    }
    if (a.size() > minSize) {
      a = a.subList(0, minSize);
    }
    if (b.size() > minSize) {
      b = b.subList(0, minSize);
    }
    if (!shuffle.in) { // out-shuffle
      a = a.subList(1, a.size());
      b = b.subList(0, minSize - 1);
    }
    interleave(a, b);
  }

  private <T> void interleave(List<T> a, List<T> b) {
    int aSize = a.size(), bSize = b.size();
    if (aSize == 0) {
      interleave(b);
    } else if (aSize + bSize == 2) {
      a.set(0, b.set(0, a.get(0)));
    } else {

      // Find a 2m = 3^k − 1 such that 3^k ≤ 2n < 3^(k+1)
      int size = aSize + bSize;
      int n = size / 2;
      int k = (int) (Math.log(size) / Math.log(3));
      int m = (int) (Math.pow(3, k) - 1) / 2;
      // Do a right cyclic shift of A[m + 1, . . . , n + m] by a distance m
      int aEnd = Math.min(m + n, aSize);
      if (m > aEnd) {  // just rotate b side
        Collections.rotate(b.subList(m - aSize, m + n - aSize), m);
      } else {
        Util.rotate(a.subList(m, aEnd), b.subList(0, m + n - aSize), m);
      }
      // For each i ∈ {0, 1, . . . , k − 1}, starting at 3i, do the cycle leader
      // algorithm for the in-shuffle permutation of order 2m
      int mod = (int) Math.pow(3, k);
      int startIdx = 1;
      for (int i = 0; i < k; i++) {
        int idx = startIdx;
        int lIdx = startIdx - 1;
        T leader = lIdx < aSize ? a.get(lIdx) : b.get(lIdx - aSize);
        do {
          idx <<= 1;
          if (idx >= mod)
            idx %= mod;
          int abx = idx - 1;
          leader = abx < aSize ? a.set(abx, leader)
                                  : b.set(abx - aSize, leader);
        } while (idx != startIdx);
        startIdx *= 3;
      }
      // Recursively do the in-shuffle algorithm on A[2m + 1, . . . , 2n]
      if (aSize <= 2 * m) {
        interleave(b.subList(2 * m - aSize, bSize));
      } else {
        interleave(a.subList(2 * m, aSize), b);
      } 
    }

  }

  @Override
  public <T> void interleave(T[] a, T[] b, Shuffle shuffle) {
    interleave(a, 0, a.length, b, 0, b.length, shuffle);
  }

  @Override
  public <T> void interleave(T[] a, int fromA, int toA,
                             T[] b, int fromB, int toB,
                             Shuffle shuffle) {
    int minSize = Math.min(toA - fromA, toB - fromB);
    if (shuffle.folding) {
      // rotate non-interleaved items to the back
      Util.rotate(b, minSize - b.length);
      // reverse the rest
      Util.reverse(b, 0, minSize);
    }
    toA = minSize;
    toB = minSize;
    if (!shuffle.in) { // out-shuffle
      minSize--;
      fromA = 1;
      toB = minSize;
    }
    interleave(a, fromA, toA, b, fromB, toB);

  }

  private <T> void interleave(T[] a, int fromA, int toa,
                              T[] b, int fromB, int tob) {
    int aSize = toa - fromA, bSize = tob - fromB;
    if (toa - fromA == 0) {
      interleave(b, fromB, tob);
    } else if (aSize + bSize == 2) {
      Util.swap(a, fromA, b, fromB);
    } else {

      // Find a 2m = 3^k − 1 such that 3^k ≤ 2n < 3^(k+1)
      int size = aSize + bSize;
      int n = size / 2;
      int k = (int) (Math.log(size) / Math.log(3));
      int m = (int) (Math.pow(3, k) - 1) / 2;
      // Do a right cyclic shift of A[m + 1, . . . , n + m] by a distance m
      int aEnd = Math.min(m + n, aSize);
      if (m > aEnd) {  // just rotate b side
        Util.rotate(b, fromB + m - aSize, fromB + m + n - aSize, m);
      } else {
        Util.rotate(a, fromA + m, fromA + aEnd,
                    b, fromB    , fromB + m + n - aSize, m);
      }
      // For each i ∈ {0, 1, . . . , k − 1}, starting at 3i, do the cycle leader
      // algorithm for the in-shuffle permutation of order 2m
      int mod = (int) Math.pow(3, k);
      int startIdx = 1;
      for (int i = 0; i < k; i++) {
        int idx = startIdx;
        int lIdx = startIdx - 1;
        T leader = lIdx < aSize ? a[fromA + lIdx] : b[fromB + lIdx - aSize];
        do {
          idx <<= 1;
          if (idx >= mod)
            idx %= mod;
          int abx = idx - 1;
          if (abx < aSize) {
            int idxA = fromA + abx;
            T tempLeader = a[idxA];
            a[idxA] = leader;
            leader = tempLeader;
          } else {
            int idxB = fromB + abx - aSize;
            T tempLeader = b[idxB];
            b[idxB] = leader;
            leader = tempLeader;
          }
        } while (idx != startIdx);
        startIdx *= 3;
      }
      // Recursively do the in-shuffle algorithm on A[2m + 1, . . . , 2n]
      if (aSize <= 2 * m) {
        interleave(b, fromB + 2 * m - aSize, tob);
      } else {
        interleave(a, fromA + 2 * m, toa, b, fromB, tob);
      }
    }
  }
}