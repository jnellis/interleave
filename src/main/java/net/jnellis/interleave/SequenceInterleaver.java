package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * An implementation of an interleaver that uses a sequence generated method
 * to place elements into position.
 *
 * @see <a href="https://cs.stackexchange.com/a/105263">
 * https://cs.stackexchange.com/a/105263</a>
 * @see Interleaver
 */
public final class SequenceInterleaver implements Interleaver {
  /**
   * No-arg constructor provided for use by {@link Interleavers} which creates
   * single instances. Use {@link Interleavers#SEQUENCE}
   */
  SequenceInterleaver() {}

  @Override
  public <T> void interleave(List<T> list, Shuffle shuffle) {
    if (list.size() > 1) {
      if (shuffle.out) {
        list = list.subList(1, list.size());
      }
      if (shuffle.folding) {
        Collections.reverse(list.subList(list.size() / 2, list.size()));
      }
      interleave(list);
    }
  }

  /**
   * Performs in-shuffle interleaving
   *
   * @param list list of elements to interleave at the midpoint
   * @param <T>  type of elements in list
   */
  public static <T> void interleave(List<T> list) {

    // https://cs.stackexchange.com/questions/332/in-place-algorithm-for-interleaving-an-array/105263#105263

    int size = list.size();
    int i = 0;
    // take zero biased midpoint and treat odd sized lists as even.
    int midpt = size / 2;

    while (i < size - 1) {
      // re-align start of shuffle to an even index.
      if (isOdd(i)) {
        i++;
      }
      int base = i;

      // shuffle first half of list
      for (; i < midpt; i++) {
        Collections.swap(list, i, midpt + Util.a025480(i - base));
      }

      // take odd length biased midpoint for swap count
      int swap_cnt = biasedMidpoint(i - base);
      // unscramble swapped items first half of remaining list
      for (int j = 0; j < swap_cnt - 1; j++) {
        int k = unshuffle(j, i - base);
        if (j != k) {
          Collections.swap(list, midpt + j, midpt + k);
        }
      }
      // push up the new midpoint to work on the remaining half of the list
      midpt += swap_cnt;
    }
  }

  private static boolean isOdd(int n) {return (n & 1) == 1;}

  /**
   * The midpoint of a length, biased away from zero if the size is odd. ex.
   * mid(5) = 3 mid(4) = 2 mid(3) = 2 mid(2) = 1 mid(1) = 1 mid(0) = 0
   *
   * @param size a positive value
   * @return midpoint of a length.
   */
  private static int biasedMidpoint(int size) {return size - (size >> 1);}

  /*
   * When the first pass of interleaving is done up to the midpoint of the list,
   * the beginning of the list is correctly interleaved but from the midpoint to
   * 3/4 is methodically scrambled. This provides the unscrambling order to
   * get the back half of the list ready to interleave again.
   *
   * Why does this work? I have no idea. It is similar in the index search that
   * happens in the divide and conquer un-scrambling of the RecursiveInterleaver.
   */
  private static int unshuffle(int j, int size) {
    int i = j;
    do {
      i = Util.a025480((size >> 1) + i);
    }
    while (i < j);
    return i;
  }

  @Override
  public <T> void interleave(T[] array, int from, int to, Shuffle shuffle) {
    int size = to - from;
    if (size > 1) {
      if (shuffle.out) {
        if (size == 2) {
          return;
        } // too small, no change
        from++;
        size--;
      }
      if (shuffle.folding) {
        Util.reverse(array, from + (size / 2), to);
      }
      interleave(array, from, to);
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
        if (minSize > 1) {
          interleave(a.subList(1, minSize), b.subList(0, minSize - 1));
        }
      } else {
        interleave(a.subList(0, minSize), b.subList(0, minSize));
      }
    }
  }

  public <T> void interleave(List<T> a, List<T> b) {
    int size = Math.min(a.size(), b.size());
    int i = 0;

    // swap all of List A
    for (; i < size; i++) {
      Util.swap(a,i, b, Util.a025480(i));
    }

    // unscramble the first half of List B
    int swap_cnt = biasedMidpoint(i);
    for (int j = 0; j + 1 < swap_cnt; j++) {
      int k = unshuffle(j, i);
      if (j != k) {
        Collections.swap(b, j, k);
      }
    }

    // adjust continuance starting point of B, based on i
    int fromB = isOdd(i) ? 1 : 0;
    // finish interleaving List B on its own
    interleave(b.subList(fromB, size));
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
      if (shuffle.out) {
        if (minSize > 1) {
          interleave(a, fromA + 1, fromA + minSize,
                     b, fromB, fromB + minSize - 1);
        }
      } else {
        interleave(a, fromA, fromA + minSize, b, fromB, fromB + minSize);
      }
    }
  }

  public <T> void interleave(T[] a, int fromA, int toA,
                             T[] b, int fromB, int toB) {
    int size = toA - fromA;
    assert size == toB - fromB : "Intervals for both arrays must be equal.";

    // shuffle the entire array A section
    int i = 0;
    for (; i < size; i++) {
      Util.swap(a, fromA + i, b, fromB + Util.a025480(i));
    }

    // take odd length biased midpoint for swap count
    int swap_cnt = biasedMidpoint(i);
    // unscramble the first half of array B
    for (int j = 0; j + 1 < swap_cnt; j++) {
      int k = unshuffle(j, i);
      if (j != k) {
        Util.swap(b, fromB + j, fromB + k);
      }
    }

    // finish interleaving array B on its own and
    // adjust continuance starting point of fromB, based on i
    interleave(b, fromB + (isOdd(i) ? 1 : 0), toB);
  }

  public <T> void interleave(T[] arr, int from, int to) {
    int size = to - from;
    int i = 0;
    // take zero biased midpoint and treat odd sized lists as even.
    int midpt = size / 2;

    while (i < size - 1) {
      // re-align start of shuffle to an even index.
      if (isOdd(i)) {
        i++;
      }
      int base = i;

      // shuffle first half of list
      for (; i < midpt; i++) {
        Util.swap(arr, from + i, from + midpt + Util.a025480(i - base));
      }

      // take odd length biased midpoint for swap count
      int swap_cnt = biasedMidpoint(i - base);
      // unscramble swapped items first half of remaining list
      for (int j = 0; j < swap_cnt - 1; j++) {
        int k = unshuffle(j, i - base);
        if (j != k) {
          Util.swap(arr, from + midpt + j, from + midpt + k);
        }
      }
      // push up the new midpoint to work on the remaining half of the list
      midpt += swap_cnt;
    }
  }
}
