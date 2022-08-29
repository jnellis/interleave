package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * Interleaves two lists or two halves of a single list in place without
 * extra space.
 *
 * The interleaving can be in-shuffle, out-shuffle, with a combination of
 * folding the bottom to the top.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Faro_shuffle">Faro shuffle</a>
 */
@SuppressWarnings("FeatureEnvy")
public class SequenceInterleaver implements Interleaver{

  public SequenceInterleaver(){}

  /**
   * The midpoint of a length, biased away from zero if the size is odd. ex.
   * mid(5) = 3 mid(4) = 2 mid(3) = 2 mid(2) = 1 mid(1) = 1 mid(0) = 0
   *
   * @param size a positive value
   * @return midpoint of a length.
   */
  static int mid(int size) {return size - (size >> 1);}

  static boolean isEven(int n) {return (n & 1) == 0;}

  /**
   * Interleaves the lower and upper halves of the <b><i>modifiable</i></b> list
   * of pieces.
   *
   * <pre>Ex. A,B,C,X,Y,Z  becomes A,X,B,Y,C,Z</pre>
   *
   * Options:
   *
   * <i>outShuffle</i> leaves the first and last pieces in place as in the
   * example above.
   * <i>inShuffle</i> starts with the upper half first
   *
   * <pre>Ex. A,B,C, X,Y,Z becomes X,A,Y,B,Z,C</pre>
   *
   * <i>folding</i> allows the interleaving operation to fold the upper into
   * the lower
   *
   * <pre>Ex.  becomes A,Z,B,Y,C,X</pre>
   *
   * together folding and inShuffle
   *
   * <pre>Ex. A,B,C, X,Y,Z  becomes Z,A,Y,B,X,C</pre>
   *
   * @param pieces  List to be reordered.
   * @param shuffle
   */
  @Override
  public <T> void interleave(final List<T> pieces, Shuffle shuffle) {

    // https://cs.stackexchange.com/questions/332/in-place-algorithm-for-interleaving-an-array/105263#105263

    int numItems = pieces.size();
    int i = 0;
    int midpt = mid(numItems);
    // if the list is of odd length and doing in-shuffle, pretend its even length
    if (!isEven(numItems) && shuffle.in) {
      midpt--;
      // ignore last element unless we are folding because it would be
      // untouched in in-shuffle of odd length anyway
      if(!shuffle.folding){
        numItems--;
      }
    }
    // reverse lower half of list
    if (shuffle.folding) {
      Collections.reverse(pieces.subList(midpt, numItems));
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
        Collections.swap(pieces, i, midpt + j);
      }

      //unscramble swapped items in right half
      int swap_cnt = mid(i - base);
      for (int j = 0; j < swap_cnt - 1; j++) {
        int k = unshuffle(j, i - base);
        if (j != k) {
          Collections.swap(pieces, midpt + j, midpt + k);
        }
      }
      // reset the midpoint to work on the remaining half of the list
      midpt += swap_cnt;
    }
//    return pieces;
  }

  /**
   * Interleave two lists without extra space.
   * @param a first list
   * @param b second list
   * @param shuffle
   */
  @Override
  public <T> void interleave(List<T> a,
                         List<T> b,
                         Shuffle shuffle) {

    int minSize = Math.min(a.size(), b.size());
    int i = 0;
    if (shuffle.folding) {
      // Rotate extra items to the back
      Collections.rotate(b, minSize - b.size());
      // then reverse the part we intend to interleave.
      Collections.reverse(b.subList(0, minSize));
    }

    if (!shuffle.in) { // if true then don't skip the first element in List A
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
        Collections.swap(b, j, k);
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
  public <T> void interleave(T[] arr, Shuffle shuffle){
    interleave(arr,0,arr.length,shuffle);
  }

  @Override
  public <T> void interleave(T[] arr, int from, int to, Shuffle shuffle ){
    // some bounds checks
    if(from < 0 || from > to || to > arr.length){
      throw new IllegalArgumentException("Bounds check on from and to parameters failed.");
    }

    int numItems = to - from;
    int i = from;
    int midpt = from + mid(numItems);
    // if the list is of odd length and doing in-shuffle, pretend its even length
    if (!isEven(numItems) && shuffle.in) {
      midpt--;
      // ignore last element unless we are folding because it would be
      // untouched in in-shuffle of odd length anyway
      if(!shuffle.folding){
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
  public <T> void interleave(T[] a, T[] b, Shuffle shuffle ){
    interleave(a,0,a.length,b,0,b.length,shuffle);

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

    if (!shuffle.in) { // if true then don't skip the first element in array A
      i++;
    }
    int base = i;

    // swap all of List A
    for (; i < minSize; i++) {
      int j = Util.a025480(i - base);
      Util.swap(a,i,b,j);
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
   * the end is methodically scrambled. This provides the unscrambling order to
   * get the front half of the list ready to interleave the end half.
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
