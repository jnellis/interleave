package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * User: Joe Nellis Date: 5/16/2022 Time: 12:47 PM
 */
public class PermutationInterleaver {

  public static <T> void interleave(List<T> list,
                                    boolean inShuffle,
                                    boolean folding) {
    if (!inShuffle) { // out-shuffle
      list = list.subList(1, list.size());
    }
    if (folding) {
      Collections.reverse(list.subList(list.size() / 2, list.size()));
    }
    interleave(list);
  }

  public static <T> void interleave(T[] arr,
                                    boolean inShuffle,
                                    boolean folding) {
    int from = 0, to = arr.length;
    if (!inShuffle) { // out-shuffle
      from++;
    }
    if (folding) {
      Util.reverse(arr, (arr.length + from) / 2, arr.length);
    }
    interleave(arr, from, to);
  }

  private static <T> void interleave(List<T> list) {
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

  public static <T> void interleave(T[] arr, int from, int to) {
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

  public static <T> void interleave(List<T> a,
                                    List<T> b,
                                    boolean inShuffle,
                                    boolean folding) {
    int minSize = Math.min(a.size(), b.size());
    if (folding) {
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
    if (!inShuffle) { // out-shuffle
      a = a.subList(1, a.size());
      b = b.subList(0, minSize - 1);
      minSize--;
    }
    interleave(a, b);
  }

  private static <T> void interleave(List<T> a, List<T> b) {
    int minSize = Math.min(a.size(), b.size());
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
        Util.rotateRight(b.subList(m-a.size(), m+n-a.size()), m);
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

}