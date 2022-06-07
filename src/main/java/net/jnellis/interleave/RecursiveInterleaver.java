package net.jnellis.interleave;

import java.util.List;

/**
 * User: Joe Nellis Date: 6/5/2022 Time: 11:51 AM
 */
public class RecursiveInterleaver {
  public static <T> void interleave(List<T> list,
                                    boolean inShuffle,
                                    boolean folding) {
    int n = list.size() >> 1;
    if (n == 1) {
      // just swap these two
      list.set(0, list.set(1, list.get(0)));
    } else {
      // work only on lists whose size is a power of two.
      int k = 1 << Util.log2(n);

      if (n != k) { // only rotate if n is not a power of two
        // rotate the part we interleave into view
        Util.rotateLeft(list.subList(k, k + n), n - k);
      }
      List<T> a = list.subList(0, k);
      List<T> b = list.subList(k, 2 * k);
      interleave(a, b, k);
      // process the remaining part of the list
      if (n > k) { // if n was a power of two already, then stop
        interleave(list.subList(2 * k, list.size()), inShuffle, folding);
      }
    }
  }

    private static <T > void interleave (List < T > a, List < T > b,int n){
      if (n == 1) {
        a.set(0, b.set(0, a.get(0))); // swap
      } else {
        // swap all of list a
        for (int i = 0; i < n; i++) {
          a.set(i, b.set(Util.a025480(i), a.get(i)));
        }
        // unscramble first half of list b
        for (int i = 2; i <= n / 2; i <<= 1) {
          if (i / 2 == 1) {
            b.set(0, b.set(1, b.get(0))); // try to save a call to sublist
          } else {
            interleave(b.subList(0, i / 2), b.subList(i / 2, i), i / 2);
          }
        }
        // recursive interleave on list b
        interleave(b.subList(0, n / 2), b.subList(n / 2, n), n / 2);
      }
    }

}
