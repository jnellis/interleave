package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * User: Joe Nellis Date: 5/16/2022 Time: 12:47 PM
 */
public class PermutationInterleaver implements Interleaver {

  public <T> void interleave(List<T> list, Shuffle shuffle) {
    if (!shuffle.in) { // out-shuffle
      list = list.subList(1, list.size());
    }
    if (shuffle.folding) {
      Collections.reverse(list.subList(list.size() / 2, list.size()));
    }
    interleave(list);
  }

  public <T> void interleave(T[] array, Shuffle shuffle) {
    interleave(array, 0, array.length, shuffle);
  }

  @Override
  public <T> void interleave(T[] array , int from, int to, Shuffle shuffle) {
    if (!shuffle.in) { // out-shuffle
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
    Util.rotateRight(list.subList(m, m + n), m);
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
    Util.rotateRight(arr, from + m, from + m + n, m);
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

  public <T> void interleave(List<T> a, List<T> b, Shuffle shuffle) {
    int minSize = Math.min(a.size(), b.size());
    if (shuffle.folding) {
      // rotate non-interleaved items to the back
      Util.rotateLeft(b, b.size() - minSize);
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
    if (a.size() == 0) {
      interleave(b);
    } else if (a.size() + b.size() == 2) {
      a.set(0, b.set(0, a.get(0)));
    } else {

      // Find a 2m = 3^k − 1 such that 3^k ≤ 2n < 3^(k+1)
      int size = a.size() + b.size();
      int n = size / 2;
      int k = (int) (Math.log(size) / Math.log(3));
      int m = (int) (Math.pow(3, k) - 1) / 2;
      // Do a right cyclic shift of A[m + 1, . . . , n + m] by a distance m
      int aEnd = Math.min(m + n, a.size());
      if (m > aEnd) {  // just rotate b side
        Util.rotateRight(b.subList(m - a.size(), m + n - a.size()), m);
      } else {
        Util.rotateRight(a.subList(m, aEnd), b.subList(0, m + n - a.size()), m);
      }
      // For each i ∈ {0, 1, . . . , k − 1}, starting at 3i, do the cycle leader
      // algorithm for the in-shuffle permutation of order 2m
      int mod = (int) Math.pow(3, k);
      int startIdx = 1;
      for (int i = 0; i < k; i++) {
        int idx = startIdx;
        int lIdx = startIdx - 1;
        T leader = lIdx < a.size() ? a.get(lIdx) : b.get(lIdx - a.size());
        do {
          idx <<= 1;
          if (idx >= mod)
            idx %= mod;
          int abx = idx - 1;
          leader = abx < a.size() ? a.set(abx, leader)
                                  : b.set(abx - a.size(), leader);
        } while (idx != startIdx);
        startIdx *= 3;
      }
      // Recursively do the in-shuffle algorithm on A[2m + 1, . . . , 2n]
      if (a.size() <= 2 * m) {
        interleave(b.subList(2 * m - a.size(), b.size()));
      } else {
        interleave(a.subList(2 * m, a.size()), b);
      }

    }

  }

  public <T> void interleave(T[] a, T[] b, Shuffle shuffle) {

    int minSize = Math.min(a.length, b.length);
    if (shuffle.folding) {
      // rotate non-interleaved items to the back
      Util.rotateLeft(b, b.length - minSize);
      // reverse the rest
      Util.reverse(b, 0, minSize);
    }
    int fromA = 0, fromB = 0, toa = minSize, tob = minSize;
    if (!shuffle.in) { // out-shuffle
      minSize--;
      fromA = 1;
      tob = minSize;
    }
    interleave(a, fromA, toa, b, fromB, tob);
  }

  @Override
  public <T> void interleave(T[] a,
                             int fromA,
                             int toA,
                             T[] b,
                             int fromB,
                             int toB,
                             Shuffle shuffle) {

  }

  private <T> void interleave(T[] a, int fromA, int toa,
                              T[] b, int fromB, int tob) {
    int aSize = toa - fromA, bsize = tob - fromB;
    if (toa - fromA == 0) {
      interleave(b, fromB, tob);
    } else if (aSize + bsize == 2) {
      Util.swap(a, fromA, b, fromB);
    } else {

      // Find a 2m = 3^k − 1 such that 3^k ≤ 2n < 3^(k+1)
      int size = aSize + bsize;
      int n = size / 2;
      int k = (int) (Math.log(size) / Math.log(3));
      int m = (int) (Math.pow(3, k) - 1) / 2;
      // Do a right cyclic shift of A[m + 1, . . . , n + m] by a distance m
      int aEnd = Math.min(m + n, aSize);
      if (m > aEnd) {  // just rotate b side
        Util.rotateRight(b, fromB + m - aSize, fromB + m + n - aSize, m);
      } else {
        Util.rotateRight(a, fromA + m, fromA + aEnd,
                         b, fromB, fromB + m + n - aSize, m);
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