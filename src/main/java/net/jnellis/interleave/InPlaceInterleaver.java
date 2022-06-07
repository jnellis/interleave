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
public class InPlaceInterleaver{

  private InPlaceInterleaver(){}

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
   * @param shuffle if false, performs an out-shuffle, the first and last piece
   *                remain unchanged, if true, performs and in-shuffle which
   *                starts with a piece from the upper half of the list.
   * @param folding if true, fold at the middle and interleave front and back
   *                ends of the list.
   * @param <T>     Type of object being ordered.
   * @return the same list of pieces, interleaved
   */
  public static <T> List<T> interleave(final List<T> pieces,
                                       final boolean shuffle,
                                       final boolean folding) {

    // https://cs.stackexchange.com/questions/332/in-place-algorithm-for-interleaving-an-array/105263#105263

    int numItems = pieces.size();
    int i = 0;
    int midpt = Util.mid(numItems);
    // if the list is of odd length and doing in-shuffle, pretend its even length
    if (!Util.isEven(numItems) && shuffle) {
      midpt--;
      // ignore last element unless we are folding because it would be
      // untouched in in-shuffle of odd length anyway
      if(!folding){
        numItems--;
      }
    }
    // reverse lower half of list
    if (folding) {
      Collections.reverse(pieces.subList(midpt, numItems));
    }

    while (i < numItems - 1) {
      //for an out-shuffle, the left item is at an even index
      if (Util.isEven(i) ^ shuffle) {
        i++;
      }
      int base = i;

      // emplace left half
      for (; i < midpt; i++) {
        int j = Util.a025480(i - base);
        Collections.swap(pieces, i, midpt + j);
      }

      //unscramble swapped items in right half
      int swap_cnt = Util.mid(i - base);
      for (int j = 0; j < swap_cnt - 1; j++) {
        int k = unshuffle(j, i - base);
        if (j != k) {
          Collections.swap(pieces, midpt + j, midpt + k);
        }
      }
      // reset the midpoint to work on the remaining half of the list
      midpt += swap_cnt;
    }
    return pieces;
  }

  /**
   * Interleave two lists without extra space.
   * @param a first list
   * @param b second list
   * @param inShuffle if true, perform an in-shuffle, otherwise an out-shuffle
   * @param folding if true, interleave from the bottom of the second list
   * @param <T>  type of element the list holds.
   */
  public static <T> void interleave(List<T> a,
                                    List<T> b,
                                    boolean inShuffle,
                                    boolean folding) {

    int minSize = Math.min(a.size(), b.size());
    int i = 0;
    if (folding) {
      // Reverse list B, but only as much as the smallest of List A or B.
      // Special case for in-shuffle, reverse the whole list regardless of size
      // as we lead with this element and the leftover, if odd sized, is ignored.
      Collections.reverse(inShuffle? b : b.subList(0,minSize));
    }

    if (!inShuffle) { // if true then don't skip the first element in List A
      i++;
    }
    int base = i;

    // swap all of List A
    for (; i < minSize; i++) {
      int j = Util.a025480(i - base);
      a.set(i, b.set(j, a.get(i)));
    }

    // unscramble the first half of List B
    int swap_cnt = Util.mid(i - base);
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
        Util.isEven(minSize) == inShuffle,
        false); // we already pre-folded

  }

  /*
   * When the first pass of interleaving is done up to the midpoint of the list,
   * the beginning of the list is correctly interleaved but from the midpoint to
   * the end is methodically scrambled. This provides the unscrambling order to
   * get the end half of the list ready to interleave the end half.
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
