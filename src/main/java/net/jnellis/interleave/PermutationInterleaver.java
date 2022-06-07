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

  public static <T> void interleave(List<T> list) {
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
      do{
        idx <<= 1;
        if(idx >= mod) idx %= mod;
        leader = list.set(idx - 1, leader);
      }while(idx != startIdx);
      startIdx *= 3;
    }
    // Recursively do the in-shuffle algorithm on A[2m + 1, . . . , 2n]
    interleave(list.subList(2 * m, size));
  }

  public static int find2m(int twoN) {
    int k = (int) (Math.log(twoN) / Math.log(3));
    return (int) (Math.pow(3, k) - 1);
  }

}
