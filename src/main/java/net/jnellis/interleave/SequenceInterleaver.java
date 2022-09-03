package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * Interleaves two lists or two halves of a single list in place without
 * extra space.
 * <p>
 * The interleaving can be in-shuffle, out-shuffle, with a combination of
 * folding the bottom to the top.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Faro_shuffle">Faro shuffle</a>
 */
public class SequenceInterleaver implements Interleaver {

  private static boolean isEven(int n) {return (n & 1) == 0;}

  /**
   * The midpoint of a length, biased away from zero if the size is odd. ex.
   * mid(5) = 3 mid(4) = 2 mid(3) = 2 mid(2) = 1 mid(1) = 1 mid(0) = 0
   *
   * @param size a positive value
   * @return midpoint of a length.
   */
  private static int mid(int size) {return size - (size >> 1);}

  @Override
  public <T> void interleave(List<T> list, Shuffle shuffle) {

    // https://cs.stackexchange.com/questions/332/in-place-algorithm-for-interleaving-an-array/105263#105263

    int numItems = list.size();
    int i = 0;
    int midpt = mid(numItems);
    // if the list is of odd length and doing in-shuffle, pretend its even length
    if (!isEven(numItems) && shuffle.in) {
      midpt--;
      // ignore last element unless we are folding because it would be
      // untouched in in-shuffle of odd length anyway
      if (!shuffle.folding) {
        numItems--;
      }
    }
    // reverse lower half of list
    if (shuffle.folding) {
      Collections.reverse(list.subList(midpt, numItems));
    }

    while (i < numItems - 1) {
      //for an out-shuffle, the left item is at an even index
      if (isEven(i) ^ shuffle.in) {
        i++;
      }
      int base = i;

      // emplace left half
      for (; i < midpt; i++) {
        int j = Util.a025480(i - base);
        list.set(i, list.set(midpt + j, list.get(i)));
      }

      //unscramble swapped items in right half
      int swap_cnt = mid(i - base);
      for (int j = 0; j < swap_cnt - 1; j++) {
        int k = unshuffle(j, i - base);
        if (j != k) {
          list.set(midpt + j, list.set(midpt + k, list.get(midpt + j)));
        }
      }
      // reset the midpoint to work on the remaining half of the list
      midpt += swap_cnt;
    }
  }

  @Override
  public <T> void interleave(List<T> a, List<T> b, Shuffle shuffle) {

    int minSize = Math.min(a.size(), b.size());
    if (shuffle.folding) {
      // Rotate extra items to the back
      Collections.rotate(b, minSize - b.size());
      // then reverse the part we intend to interleave.
      Collections.reverse(b.subList(0, minSize));
    }

    int i = 0;
    if (shuffle.out) { // if true then don't skip the first element in List A
      i++;
    }
    int base = i;

    // swap all of List A
    for (; i < minSize; i++) {
      int j = Util.a025480(i - base);
      a.set(i, b.set(j, a.get(i)));
    }

    // unscramble the first half of List B
    int swap_cnt = mid(i - base);
    for (int j = 0; j + 1 < swap_cnt; j++) {
      int k = unshuffle(j, i - base);
      if (j != k) {
        b.set(j, b.set(k, b.get(j)));
//        Collections.swap(b, j, k);
      }
    }

    // finish interleaving List B on its own
    interleave(
        b.subList(0, minSize),
        // for odd sized lists, reverse the shuffle otherwise the midpoint
        // picked by the one-list algorithm will be off by one.
        (isEven(minSize) ? shuffle : shuffle.opposite())
            .nonFolding()); // don't refold
  }

  @Override
  public <T> void interleave(T[] arr, int from, int to, Shuffle shuffle) {
    int numItems = to - from;
    int i = from;
    int midpt = from + mid(numItems);
    // if the list is of odd length and doing in-shuffle, pretend its even length
    if (!isEven(numItems) && shuffle.in) {
      midpt--;
      // ignore last element unless we are folding because it would be
      // untouched in in-shuffle of odd length anyway
      if (!shuffle.folding) {
        numItems--;
      }
    }
    // reverse lower half of list
    if (shuffle.folding) {
      Util.reverse(arr, midpt, numItems);
    }

    while (i < numItems - 1) {
      //for an out-shuffle, the left item is at an even index
      if (isEven(i) ^ shuffle.in) {
        i++;
      }
      int base = i;

      // emplace left half
      for (; i < midpt; i++) {
        int j = Util.a025480(i - base);
        Util.swap(arr, i, midpt + j);
      }

      //unscramble swapped items in right half
      int swap_cnt = mid(i - base);
      for (int j = 0; j < swap_cnt - 1; j++) {
        int k = unshuffle(j, i - base);
        if (j != k) {
          Util.swap(arr, midpt + j, midpt + k);
        }
      }
      // reset the midpoint to work on the remaining half of the list
      midpt += swap_cnt;
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
    int i = 0;
    if (shuffle.folding) {
      // Rotate extra items to the back
      Util.rotate(b, minSize - b.length);
      // then reverse the part we intend to interleave.
      Util.reverse(b, 0, minSize);
    }

    if (shuffle.out) { // if true then don't skip the first element in array A
      i++;
    }
    int base = i;

    // swap all of List A
    for (; i < minSize; i++) {
      int j = Util.a025480(i - base);
      Util.swap(a, i, b, j);
    }

    // unscramble the first half of List B
    int swap_cnt = mid(i - base);
    for (int j = 0; j + 1 < swap_cnt; j++) {
      int k = unshuffle(j, i - base);
      if (j != k) {
        Util.swap(b, j, k);
      }
    }

    // finish interleaving List B on its own
    interleave(
        b, //.subList(0, minSize),
        0,
        minSize,
        // for odd sized lists, reverse the shuffle otherwise the midpoint
        // picked by the one-list algorithm will be off by one.
        (isEven(minSize) ? shuffle : shuffle.opposite())
            .nonFolding()); // don't refold
  }

  /*
   * When the first pass of interleaving is done up to the midpoint of the list,
   * the beginning of the list is correctly interleaved but from the midpoint to
   * 3/4 is methodically scrambled. This provides the unscrambling order to
   * get the back half of the list ready to interleave again.
   *
   * @todo: explore the unscrambling technique in RecursiveInterleaver to
   *   more accurately find swap positions.
   */
  private static int unshuffle(int j, int size) {
    int i = j;
    do {
      i = Util.a025480((size >> 1) + i);
    }
    while (i < j);
    return i;
  }
}
