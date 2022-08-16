package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * User: Joe Nellis Date: 6/5/2022 Time: 11:51 AM
 */
public class RecursiveInterleaver {
  public static <T> void interleave(List<T> list,
                                    boolean inShuffle,
                                    boolean folding) {
    if (!inShuffle) { // out-shuffle
      list = list.subList(1, list.size());
    }
    if (folding) {
      Collections.reverse(list.subList(list.size() / 2, list.size()));
    }

    int n = list.size() >> 1;
    if (n < 2) {
      if (n == 0) { // list was empty or had one element
        return;
      }
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
        interleave(list.subList(2 * k, list.size()), true, false);
      }
    }
  }

  private static <T> void interleave(List<T> a, List<T> b, int n) {
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

  public static <T> void interleave(List<T> a,
                                    List<T> b,
                                    boolean inShuffle,
                                    boolean folding) {
    int minSize = Math.min(a.size(), b.size());
    if (folding) {
      // rotate non-interleaved items to the back
      Util.rotateLeft(b,b.size() - minSize);
      // reverse the rest
      Collections.reverse(b.subList(0,minSize));
    }
    if(a.size() > minSize){
      a = a.subList(0,minSize);
    }
    if(b.size() > minSize){
      b = b.subList(0,minSize);
    }
    if (!inShuffle) { // out-shuffle
      a = a.subList(1,a.size());
      b = b.subList(0,minSize-1);
      minSize--;
    }
    if (minSize < 2) {
      if (minSize == 0 ) { // one of the lists is now empty
        return;
      }
      // just swap these two
      a.set(0, b.set(0, a.get(0)));
    } else {

      // work only on lists whose size is a power of two.
      int k = 1 << Util.log2(minSize);

//      if (n != k) { // only rotate if n is not a power of two
//        Util.rotateLeft(list.subList(k, k + n), n - k);
//      }
//      List<T> a = list.subList(0, k);
//      List<T> b = list.subList(k, 2 * k);
      interleave(a , b, k);
      // process the remaining part of the list
      if (minSize > k) { // if n was a power of two already, then stop
        //swap n-k from front of listB to back of listA
        for (int j = 0; j < minSize-k; j++) {
          b.set(j, a.set(k+j, b.get(j)));
        }
        Util.rotateLeft(b,minSize-k);

        //then rotate listB and perform single list interleave
        interleave(
            b.subList(k - (minSize-k)  , b.size()),
            false,
            false);
      }
    }

  }

  public static <T> void interleave(T[] arr,
                                    boolean inShuffle,
                                    boolean folding) {
    int from = 0, to = arr.length;
    if (!inShuffle) { // out-shuffle
      from++;
    }
    if (folding) {
      Util.reverse(arr, from + (to - from) / 2, to);
    }
    interleave(arr, from, to);
  }

  private static <T> void interleave(T[] arr, int from, int to) {
    int size = to - from;
    int n = size >> 1;
    if (n < 2) {
      if (n == 0) { // array was empty or had one element
        return;
      }
      // just swap these two
      Util.swap(arr, from, from + 1);
    } else {
      // work only on lists whose size is a power of two.
      int k = 1 << Util.log2(n);

      if (n != k) { // only rotate if n is not a power of two
        // rotate the part we interleave into view
        Util.rotateLeft(arr, from + k, from + k + n, n - k);
      }
      interleave(arr, from, from + k,
                 arr, from + k, from + k + k,
                 k);
      // process the remaining part of the list
      if (n > k) { // if n was a power of two already, then stop
        interleave(arr, from + k + k, to);
      }
    }
  }

  private static <T> void interleave(T[] arra, int froma, int toa,
                                     T[] arrb, int fromb, int tob, int n) {
    if (n == 1) {
      Util.swap(arra, froma, arrb, fromb); // swap
    } else {
      // swap all of list a
      for (int i = 0; i < n; i++) {
        Util.swap(arra, froma + i, arrb, fromb + Util.a025480(i));
      }
      // unscramble first half of list b
      for (int i = 2; i <= n / 2; i <<= 1) {
        if (i / 2 == 1) {
          Util.swap(arrb, fromb, fromb + 1);
        } else {
          interleave(arrb, fromb, fromb + i / 2,
                     arrb, fromb + i / 2, fromb + i,
                     i / 2);
        }
      }
      // recursive interleave on list b
      interleave(arrb, fromb, fromb + n / 2,
                 arrb, fromb + n / 2, n,
                 n / 2);
    }
  }

  public static <T> void interleave(T[] arrA,
                                    T[] arrB,
                                    boolean inShuffle,
                                    boolean folding) {
  }
}
