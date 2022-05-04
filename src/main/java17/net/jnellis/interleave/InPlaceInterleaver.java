package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * User: Joe Nellis Date: 11/5/2021 Time: 1:37 AM
 */
public class InPlaceInterleaver {
  /**
   * Interleaves the lower and upper halfs of the <i>modifiable</i> list of
   * pieces.
   *
   * Ex. A,B,C, X,Y,Z  becomes A,X,B,Y,C,Z
   *
   * Options:
   *
   * <i>outShuffle</i> leaves the first and last pieces in place as in the
   * example above.
   * <i>inShuffle</i> starts the upper half first
   *
   * Ex. A,B,C, X,Y,Z becomes X,A,Y,B,Z,C
   *
   * <i>folding</i> allows the interleave to fold the upper into the lower
   *
   * Ex.  becomes A,Z,B,Y,C,X
   *
   *
   * together folding and inShuffle
   *
   * Ex. A,B,C, X,Y,Z  becomes Z,A,Y,B,X,C
   *
   * @param pieces  List to be reordered.
   * @param shuffle if false, performs an out-shuffle, the first and last piece
   *                remain unchanged, if true, performs and in-shuffle which
   *                starts with a piece from the upper half of the list.
   * @param folding if true, fold at the middle and interleave front and back
   *                ends of the list.
   * @param <T>     Type of object being ordered.
   * @return list of pieces
   */
  public static <T> List<T> interleave(final List<T> pieces,
                                       final boolean shuffle,
                                       final boolean folding) {

    //todo: delete this notice after testing confirmed
//    System.out.println("jdk17 interleave method");

    int numItems = pieces.size();
    int i = 0;
    int midpt = mid(numItems);
    if (folding) {
      // reverse lower half of list
      Collections.reverse(pieces.subList(midpt, numItems));
    }

    while (i < numItems - 1) {
      //for an out-shuffle, the left item is at an even index
      if (((i & 1) == 0) ^ shuffle) {
        i++;
      }
      int base = i;

      // emplace left half
      for (; i < midpt; i++) {
        int j = a025480(i - base);
        Collections.swap(pieces, i, midpt + j);
      }

      //unscramble swapped items
      int swap_cnt = mid(i - base);
      for (int j = 0; j + 1 < swap_cnt; j++) {
        int k = unshuffle(j, i - base);
        if (j != k) {
          Collections.swap(pieces, midpt + j, midpt + k);
        }
      }
      midpt += swap_cnt;
    }
    return pieces;
  }

  private static int a025480(int n) {
   return  n >> (Integer.numberOfTrailingZeros(~n) + 1);
  }

  private static int unshuffle(int j, int size) {
    int i = j;
    do {
      i = a025480((size >> 1) + i);
    }
    while (i < j);
    return i;
  }

  private static int mid(int size) {return size - (size >> 1);}

  private static boolean isEven(int n) {return (n & 1) == 0;}

}
