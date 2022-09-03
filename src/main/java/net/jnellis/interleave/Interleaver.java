package net.jnellis.interleave;

import java.util.List;
import java.util.RandomAccess;

/**
 * In place (in-memory) interleave a single collection or two collections,
 * using
 * a <a href="https://en.wikipedia.org/wiki/Faro_shuffle">Faro Shuffle</a>.
 * <p>
 * Interleaving a single collection chooses a midpoint then interleaves items
 * from that midpoint with the beginning of the collection.
 * <pre>
 * Ex. [1,2,3,4,a,b,c,d]  ->  [1,a,2,b,3,c,4,d]
 * </pre>
 * Interleaving two collections swaps items between them.
 * <pre>
 * Ex. [1,2,3,4] and [a,b,c,d]  ->  [1,a,2,b] and [3,c,4,d]
 * </pre>
 * <p>
 * There are two types of interleaving: InShuffle and OutShuffle.  The
 * OutShuffle
 * guarantees that the first item of the collection (or first collection)
 * remains
 * first and the last item of the collection (or second collection) remains
 * last. The InShuffle leads with the middle of the collection or the second
 * collections if there are two.
 * <pre>
 * Example of OutShuffle [1,2,3,a,b,c]   -> [1,a,2,b,3,c]
 *                       [1,2,3] [a,b,c] -> [1,a,2] [b,3,c]
 * </pre>
 * <pre>
 * Example of InShuffle [1,2,3,a,b,c]   -> [a,1,b,2,c,3]
 *                      [1,2,3] [a,b,c] -> [a,1,b] [2,c,3]
 * </pre>
 * <p>
 * A folding interleave will shuffle items from the end of the collection
 * instead of the midpoint, effectively folding the collection at the midpoint.
 * <pre>
 * Example of Folding OutShuffle [1,2,3,a,b,c]   -> [1,c,2,b,3,a]
 *                               [1,2,3] [a,b,c] -> [1,c,2] [b,3,a]
 * </pre>
 * <pre>
 * Example of Folding InShuffle [1,2,3,a,b,c]   -> [c,1,b,2,a,3]
 *                              [1,2,3] [a,b,c] -> [c,1,b] [2,a,3]
 * </pre>
 * <p>
 * <em>NOTE:</em>
 * Unintuitive behavior can arise when trying to interleave a single collection
 * of odd length or two collections of unequal length. The follow examples
 * describe
 * behavior of each type of shuffle operation ({@link Shuffle}) with one and
 * two collection interleaving:
 * <p>
 * In-Shuffle
 * <pre>
 * one odd length collection
 *   Ex. [1,2,3,a,b,c,d] -> [a,1,b,2,c,3,d]
 *   EX. [1,2,3,4,a,b,c] -> [<b>4</b>,1,a,2,b,3,c] // unexpected behavior, at front!
 *
 * two unequal length collections
 *   Ex. [1,2,3,4,5], [a,b,c,d] -> [a,1,b,2,<b>5</b>], [c,3,d,4]
 *   Ex. [1,2,3,4], [a,b,c,d,e] -> [a,1,b,2], [c,3,d,4,e]
 * </pre>
 * <p>
 * Out-Shuffle
 * <pre>
 * one odd length collection
 *   Ex. [1,2,3,4,b,c,d] -> [1,a,2,b,3,c,4]
 *   Ex. [1,2,3,a,b,c,d] -> [1,<b>b</b>,2,c,3,d,<b>a</b>] // unexpected behavior, off by 1!
 *
 * two unequal length collections
 *   Ex. [1,2,3,4,5], [a,b,c,d] -> [1,a,2,b,5], [3,c,4,d]
 *   Ex. [1,2,3,4], [a,b,c,d,e] -> [1,a,2,b], [3,c,4,d,e]
 * </pre>
 * <p>
 * Folding In-Shuffle
 * <pre>
 * one odd length collection
 *   Ex. [1,2,3,4,c,b,a] -> [a,1,b,2,c,3,<b>4</b>]  // unexpected behavior, at end!
 *   Ex. [1,2,3,d,c,b,a] -> [a,1,b,2,c,3,d]
 *
 * two unequal length collections
 *   Ex. [1,2,3,4,5], [d,c,b,a] -> [a,1,b,2,5], [c,3,d,4]
 *   Ex. [1,2,3,4], [e,d,c,b,a] -> [a,1,b,2], [c,3,d,4,e]
 * </pre>
 * <p>
 * Folding Out-Shuffle
 * <pre>
 * one odd length collection
 *   Ex. [1,2,3,4,c,b,a] -> [1,a,2,b,3,c,4]
 *   Ex. [1,2,3,d,c,b,a] -> [1,a,2,b,3,c,<b>d</b>] // unexpected behavior, at end!
 *
 * two unequal length collections
 *   Ex. [1,2,3,4,5], [d,c,b,a] -> [1,a,2,b,5], [3,c,4,d]
 *   Ex. [1,2,3,4], [e,d,c,b,a] -> [1,a,2,b], [3,c,4,d,e]
 * </pre>
 */
public interface Interleaver {

  /**
   * Performs interleaving of the midpoint of this list with the head of the
   * list.
   * <p>
   * The exact behavior of the interleaving is dependent upon the shuffle
   * type which is explained in the class description above.
   *
   * @param list    elements to be interleaved in-place.
   *                For performance, must implement {@link RandomAccess}
   * @param shuffle A descriptor indicating the type of interleave operation.
   * @param <T>     type of list element
   * @throws UnsupportedOperationException if the specified list or its
   *                                       list-iterator does not support the
   *                                       set operation.
   */
  <T> void interleave(List<T> list, Shuffle shuffle);

  /**
   * Performs interleaving of the midpoint of this array with the head of the
   * array.
   * <p>
   * The exact behavior of the interleaving is dependent upon the shuffle
   * type which is explained in the class description above.
   *
   * @param array   elements to be interleaved in-place.
   * @param shuffle A descriptor indicating the type of interleave operation.
   * @param <T>     type of array element
   */
  default <T> void interleave(T[] array, Shuffle shuffle) {
    interleave(array, 0, array.length, shuffle);
  }

  /**
   * Performs interleaving of the midpoint of this array <em>section</em>
   * with the start of the array <em>section</em>.
   * <p>
   * The exact behavior of the interleaving is dependent upon the shuffle
   * type which is explained in the class description above.
   *
   * @param array   elements to be interleaved in-place.
   * @param from    starting index
   * @param to      ending index, exclusive
   * @param shuffle A descriptor indicating the type of interleave operation.
   * @param <T>     type of array element
   */
  <T> void interleave(T[] array, int from, int to, Shuffle shuffle);

  /**
   * Performs interleaving of two lists.
   * <p>
   * The exact behavior of the interleaving is dependent upon the shuffle
   * type which is explained in the class description above.
   *
   * @param a       elements of first list to be interleaved in-place.
   *                For performance, must implement {@link RandomAccess}
   * @param b       elements of second list to be interleaved in-place.
   *                For performance, must implement {@link RandomAccess}
   * @param shuffle A descriptor indicating the type of interleave operation.
   * @param <T>     type of list element
   * @throws UnsupportedOperationException if the specified list(s) or its
   *                                       list-iterator does not support the
   *                                       set operation.
   */
  <T> void interleave(List<T> a, List<T> b, Shuffle shuffle);

  /**
   * Performs interleaving of two arrays.
   * <p>
   * The exact behavior of the interleaving is dependent upon the shuffle
   * type which is explained in the class description above.
   *
   * @param a       elements of first array to be interleaved in-place.
   * @param b       elements of second array to be interleaved in-place.
   * @param shuffle A descriptor indicating the type of interleave operation.
   * @param <T>     type of array element
   */
  default <T> void interleave(T[] a, T[] b, Shuffle shuffle) {
    interleave(a, 0, a.length, b, 0, b.length, shuffle);
  }

  /**
   * Performs interleaving of two array sections.
   * <p>
   * The exact behavior of the interleaving is dependent upon the shuffle
   * type which is explained in the class description above.
   *
   * @param a       elements of first array section to be interleaved in-place.
   * @param fromA   starting index of first array
   * @param toA     ending index of first array, exclusive
   * @param b       elements of second array section to be interleaved in-place.
   * @param fromB   starting index of second array
   * @param toB     ending index of second array, exclusive
   * @param shuffle A descriptor indicating the type of interleave operation.
   * @param <T>     type of array element
   */
  <T> void interleave(T[] a, int fromA, int toA,
                      T[] b, int fromB, int toB,
                      Shuffle shuffle);
}
