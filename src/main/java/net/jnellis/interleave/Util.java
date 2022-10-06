package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * Some static utility functions used by interleaving algorithms.
 */
public final class Util {
  /* Approximate value of log(2)/log(3) = 323/512 */
  private static final int LN2_DIV_LN3_NUMERATOR = 323;
  /* All powers of 3 (32-bit signed) */
  static final int[] POW3 = {1, 3, 9, 27, 81, 243, 729, 2187, 6561,
      19683, 59049, 177147, 531441, 1594323, 4782969, 14348907, 43046721,
      129140163, 387420489, 1162261467};

  private Util() {}

  /**
   * Reverses elements in a section of an array.
   *
   * @param arr  The array
   * @param from The starting point
   * @param to   Exclusive end point
   */
  public static void reverse(Object[] arr, int from, int to) {
    int size = to - from;
    for (int i = from, mid = from + (size >> 1), j = to - 1; i < mid; i++, j--) {
      swap(arr, i, j);
    }
  }

  /**
   * Swap two elements in an array.
   *
   * @param arr The array
   * @param i   first location
   * @param j   other location
   */
  public static void swap(Object[] arr, int i, int j) {
    Object temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
  }

  /**
   * Swap two elements in two different arrays.
   *
   * @param arrA The first array
   * @param i    location in first array
   * @param arrB The second array
   * @param j    location in second array
   */
  public static void swap(Object[] arrA, int i, Object[] arrB, int j) {
    Object temp = arrA[i];
    arrA[i] = arrB[j];
    arrB[j] = temp;
  }

  /**
   * Rotate, in-place, across two lists. Elements at the end of the first list
   * get moved to the beginning of the second list. Elements at the end of the
   * second list get pushed to the first list.
   * <p>
   * Similar in behavior to {@link Collections#rotate(List, int)} wherein
   * negative distances rotate left and positive distances rotate right.
   *
   * @param a        first list
   * @param b        second list
   * @param distance distance to rotate
   * @param <T>      type of element in lists
   */
  public static <T> void rotate(List<T> a, List<T> b, int distance) {
    int dist = distance;
    int aSize = a.size();
    int size = aSize + b.size();
    if (size == 0)
      return;
    dist = dist % size;
    if (dist < 0)
      dist += size;
    if (dist == 0)
      return;

    int nMoved = 0;
    for (int cycleStart = 0; nMoved != size; cycleStart++) {
      T displaced = cycleStart >= aSize ? b.get(cycleStart - aSize)
                                        : a.get(cycleStart);
      int i = cycleStart;
      do {
        i += dist;
        if (i >= size)
          i -= size;
        if (i >= aSize) {
          displaced = b.set(i - aSize, displaced);
        } else {
          displaced = a.set(i, displaced);
        }
        nMoved++;
      } while (i != cycleStart);
    }
  }

  /**
   * Rotate elements in an array similar to
   * {@link Collections#rotate(List, int)}
   *
   * @param array    array to rotate
   * @param distance distance to rotate
   * @param <T>      type of array element
   * @see Collections#rotate(List, int)
   */
  public static <T> void rotate(T[] array, int distance) {
    rotate(array, 0, array.length, distance);
  }

  /**
   * Rotate elements in an array similar to
   * {@link Collections#rotate(List, int)}
   *
   * @param array    array to rotate
   * @param from     starting index to rotate
   * @param to       ending index to rotate, exclusive
   * @param distance distance to rotate
   * @param <T>      array element type
   * @see Collections#rotate(List, int)
   */
  public static <T> void rotate(T[] array, int from, int to, int distance) {
    int dist = distance;
    int size = to - from;
    if (size == 0)
      return;
    dist = dist % size;
    if (dist < 0)
      dist += size;
    if (dist == 0)
      return;

    int nMoved = 0;
    for (int cycleStart = 0; nMoved != size; cycleStart++) {
      T displaced = array[from + cycleStart];
      int i = cycleStart;
      do {
        i += dist;
        if (i >= size)
          i -= size;
        T temp = array[from + i];
        array[from + i] = displaced;
        displaced = temp;
        nMoved++;
      } while (i != cycleStart);
    }
  }

  /**
   * Rotate, in-place, across two arrays. Elements at the end of the first array
   * get moved to the beginning of the second array. Elements at the end of the
   * second array get pushed to the first array.
   * <p>
   * Similar in behavior to {@link Collections#rotate(List, int)} wherein
   * negative distances rotate left and positive distances rotate right.
   *
   * @param a        First array
   * @param fromA    start index of first array
   * @param toA      end index of first array, exclusive
   * @param b        Second array
   * @param fromB    start index of second array
   * @param toB      end index of second array, exclusive
   * @param distance distance to rotate
   * @param <T>      array element type
   * @see Collections#rotate(List, int)
   */
  public static <T> void rotate(T[] a,
                                int fromA,
                                int toA,
                                T[] b,
                                int fromB,
                                int toB,
                                int distance) {
    int dist = distance;
    int aSize = toA - fromA;
    int size = aSize + (toB - fromB);
    if (size == 0)
      return;
    dist = dist % size;
    if (dist < 0)
      dist += size;
    if (dist == 0)
      return;

    int nMoved = 0;
    for (int cycleStart = 0; nMoved != size; cycleStart++) {
      T displaced = cycleStart >= aSize ? b[fromB + cycleStart - aSize]
                                        : a[fromA + cycleStart];
      int i = cycleStart;
      do {
        i += dist;
        if (i >= size)
          i -= size;
        if (i >= aSize) {
          int idx = fromB + i - aSize;
          T temp = b[idx];
          b[idx] = displaced;
          displaced = temp;
        } else {
          int idx = fromA + i;
          T temp = a[idx];
          a[idx] = displaced;
          displaced = temp;
        }
        nMoved++;
      } while (i != cycleStart);
    }
  }

  /**
   * Corresponds to an integer sequence of the
   * <a href="https://oeis.org/A025480">https://oeis.org/A025480</a>
   * which generates the index positions for the proper sequence of swaps (and
   * swap backs) for the interleave operation, given that series of swaps was
   * done sequentially and in order. Implementation note:  The algorithm
   * effectively right shifts the value of n just past the lowest clear bit.
   *
   * @param n index on the left side to swap
   * @return index on the right side to swap (plus any midpoint offset)
   */
  public static int a025480(int n) {
    return n >> (Integer.numberOfTrailingZeros(~n) + 1);
  }

  /**
   * Comparable to Math.pow for 3^k
   *
   * @param k exponent value
   * @return 3^k
   */
  public static int powersOf3(int k) {
    return POW3[k];
  }

  /**
   * Fast log base 3 for integers
   *
   * @param i value
   * @return the base 3 logarithm of i
   */
  public static int ilog3(int i) {
    // https://graphics.stanford.edu/%7Eseander/bithacks.html#IntegerLog10
    int t = ((ilog2(i) + 1) * LN2_DIV_LN3_NUMERATOR) >>> 9;
    return (i < POW3[t]) ? t - 1 : t;
  }

  /**
   * Fast log base 2 for integers
   *
   * @param i value
   * @return the base 2 logarithm of i
   */
  public static int ilog2(int i) {
    //noinspection MagicNumber
    return i == 0 ? 0 : 31 - Integer.numberOfLeadingZeros(i);
  }

}
