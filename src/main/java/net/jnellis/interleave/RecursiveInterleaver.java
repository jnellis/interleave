package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * User: Joe Nellis Date: 6/5/2022 Time: 11:51 AM
 */
public class RecursiveInterleaver implements Interleaver{
  public <T> void interleave(List<T> list, Shuffle shuffle) {
    if (!shuffle.in) { // out-shuffle
      list = list.subList(1, list.size());
    }
    if (shuffle.folding) {
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
        interleave(list.subList(2 * k, list.size()), Shuffle.IN);
      }
    }
  }

  private <T> void interleave(List<T> list) {
    int n = list.size() >> 1;
    if (n < 2) {
      if (n == 0) {
        return;
      }
      list.set(0, list.set(1, list.get(0)));
    } else {
      int k = 1 << Util.log2(n);
      if (n != k) {
        Util.rotateLeft(list.subList(k, k + n), n - k);
      }
      // swap all of first half of list
      for (int i = 0; i < k; i++) {
        list.set(i, list.set(Util.a025480(i), list.get(i)));
      }
    }
  }

  private <T> void interleave(List<T> a, List<T> b, int n) {
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

  public <T> void interleave(List<T> a,  List<T> b, Shuffle shuffle) {
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
      minSize--;
    }
    if (minSize < 2) {
      if (minSize == 0) { // one of the lists is now empty
        return;
      }
      // just swap these two
      a.set(0, b.set(0, a.get(0)));
    } else {

      // work only on lists whose size is a power of two.
      int k = 1 << Util.log2(minSize);

      interleave(a, b, k);
      // process the remaining part of the list
      if (minSize > k) { // if n was a power of two already, then stop
        //swap n-k from front of listB to back of listA
        for (int j = 0; j < minSize - k; j++) {
          b.set(j, a.set(k + j, b.get(j)));
        }
        Util.rotateLeft(b, minSize - k);

        //then rotate listB and perform single list interleave
        interleave(
            b.subList(k - (minSize - k), b.size()), Shuffle.OUT);
      }
    }

  }

  public <T> void interleave(T[] arr, Shuffle shuffle){
    interleave(arr,0,arr.length,shuffle);
  }

  public <T> void interleave(T[] arr,  int from,  int to, Shuffle shuffle) {
    if (!shuffle.in) { // out-shuffle
      from++;
    }
    if (shuffle.folding) {
      Util.reverse(arr, from + (to - from) / 2, to);
    }
    interleave(arr, from, to);
  }

  /**
   * Processes the interleaving at powers of 2 items at a time.
   *
   * @param array
   * @param from
   * @param to
   * @param <T>
   */
  private <T> void interleave(T[] array, int from, int to) {
    int size = to - from;
    int n = size >> 1;
    if (n < 2) {
      if (n == 0) { // array was empty or had one element
        return;
      }
      // just swap these two
      Util.swap(array, from, from + 1);
    } else {
      // work only on lists whose size is a power of two.
      int k = 1 << Util.log2(n);

      if (n != k) { // only rotate if n is not a power of two
        // rotate the part we interleave into view
        Util.rotateLeft(array, from + k, from + k + n, n - k);
      }
      interleave(array, from,
                 array, from + k,
                 k);
      // process the remaining part of the list
      if (n > k) { // if n was a power of two already, then stop
        interleave(array, from + k + k, to);
      }
    }
  }

  private <T> void interleave(T[] a, int fromA,
                              T[] b, int fromB, int n) {
    if (n == 1) {
      Util.swap(a, fromA, b, fromB); // swap
    } else {
      // swap all of list a
      for (int i = 0; i < n; i++) {
        Util.swap(a, fromA + i, b, fromB + Util.a025480(i));
      }
      // unscramble first half of list b
      for (int i = 2; i <= n / 2; i <<= 1) {
        if (i / 2 == 1) {
          Util.swap(b, fromB, fromB + 1);
        } else {
          interleave(b, fromB,
                     b, fromB + i / 2,
                     i / 2);
        }
      }
      // recursive interleave on list b
      interleave(b, fromB,
                 b, fromB + n / 2,
                 n / 2);
    }
  }

  @Override
  public <T> void interleave(T[] a,
                             int fromA,
                             int toA,
                             T[] b,
                             int fromB,
                             int toB,
                             Shuffle shuffle) {
    int minSize = Math.min(a.length, b.length);
    if (shuffle.folding) {
      // rotate non-interleaved items to the back
      Util.rotateLeft(b, b.length - minSize);
      // reverse the rest
      Util.reverse(b, 0, minSize);
    }
    toA = minSize;
    toB = minSize;
    if (!shuffle.in) { // out-shuffle
      fromA++;
      toB--;
      minSize--;
    }
    if (minSize < 2) {
      if (minSize == 0) {
        return;
      }
      // just swap these two
      Util.swap(a, fromA, b, fromB);
    } else {
      // work only on lists whose size is a power of two.
      int k = 1 << Util.log2(minSize);
      interleaveUpToPowerOf2(a, fromA, toA, b, fromB, toB, k);
      if (minSize > k) { // if n was a power of two already then we are done.
        // The amount of final work done in b[] is larger than what is leftover
        // in a[] so swap n-k from the front of b[] to back of array a[].
        for (int j = 0; j < minSize - k; j++) {
          Util.swap(a, fromA + k + j, b, fromB + j);
        }
        // then finally rotate remainder of a[] that's in b[] into position
        Util.rotateLeft(b, fromB, toB, minSize - k);
        // and perform single list interleave
        interleave(b, fromB + 2 * k - minSize, toB, Shuffle.OUT);
      }
    }
  }

  public <T> void interleave(T[] a,  T[] b, Shuffle shuffle) {
    interleave(a, 0, a.length, b, 0, b.length, shuffle);
  }

  private <T> void interleaveUpToPowerOf2(T[] a, int fromA, int toA,
                                                 T[] b, int fromB, int toB,
                                                 int n) {
    if (n == 1) {
      Util.swap(a, fromA, b, fromB);
    } else {
      // swap all of list a
      for (int i = 0; i < n; i++) {
        Util.swap(a, fromA + i, b, fromB + Util.a025480(i));
      }
      // unscramble first half of list b
      for (int i = 2; i <= n / 2; i <<= 1) {
        if (i / 2 == 1) {
          Util.swap(b, fromB, fromB + 1); // save a call
        } else {
          interleave(b, fromB,
                     b, fromB + i / 2,
                     i / 2);
        }
      }
      // recursive interleave on list b
      interleave(b, fromB,
                 b, fromB+n/2,
                 n / 2);
    }
  }
}
