package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * Some static utility functions used by interleaving algorithms.
 */
public final class Util {
  /* All powers of 3 (32-bit signed) */
  static final int[] POW3 = {1, 3, 9, 27, 81, 243, 729, 2187, 6561,
      19683, 59049, 177147, 531441, 1594323, 4782969, 14348907, 43046721,
      129140163, 387420489, 1162261467};

  /* Approximate value of log(2)/log(3) = 323/512 */
  private static final int LN2_DIV_LN3_NUMERATOR = 323;

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
   * Swap two elements in two different lists.
   * @param a The first list
   * @param i index location in first list
   * @param b The second list
   * @param j index location in second list
   * @param <T> type element of lists.
   */
  public static <T> void swap(List<T> a, int i, List<T> b, int j){
    a.set(i, b.set(j, a.get(i)));
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
   * Fast log base 2 for integers
   *
   * @param i value
   * @return the base 2 logarithm of i
   */
  public static int ilog2(int i) {
    //noinspection MagicNumber
    return i == 0 ? 0 : 31 - Integer.numberOfLeadingZeros(i);
  }


  /**
   * Lemire's <a href="https://arxiv.org/abs/1902.01961">
   * Fast Remainder by Direct Computation</a>
   * <p>
   * Requires pre-computation of a constant before calling
   * <p>
   * For {@code n % d} where {@code n} and {@code d} are signed integers,
   * {@code u64_c} is the multiplicative inverse of {@code d} given by:
   *
   * <pre>{@code
   *     int pd = d < 0 ? -d : d ; // divisor d can't be Integer.MIN_VALUE
   *     long u64c = Long.divideUnsigned( -1L, pd) + 1L +
   *         ((pd & (pd-1L))==0 ? 1L : 0); }
   * </pre>
   * <p>
   * Requires jdk 18
   *
   * @param n     the dividend
   * @param u64_c unsigned multiplicative inverse of the divisor (see above)
   * @param pd    unsigned absolute value of divisor, positive d.
   * @return remainder of n % d
   */
  public static int fastmod(int n, long u64_c, int pd) {
    //    int pd = d < 0 ? -d : d ;
    //    long u64c = Long.divideUnsigned( -1L, pd) + 1L +
    //        ((pd & (pd-1L))==0 ? 1L : 0);

    long u64lowbits = (long) n * u64_c;
    long highbits = Math.unsignedMultiplyHigh(u64lowbits, pd);
    return (int) (highbits);//- (( pd - 1) & ( n >> 31)));
  }


  /**
   * Fast log base 3 for integers
   *
   * @param i value
   * @return the base 3 logarithm of i
   */
  public static int ilog3(int i) {
    // https://graphics.stanford.edu/%7Eseander/bithacks.html#IntegerLog10
    /*
    log3(i) = log2(i)/log2(3)
            = log2(i)/(ln(3)/ln(2))
            = log2(i)* ln(2)/ln(3)
            = log2(i)* 323/512 <-- divisor is power of 2 so we can shift later
            = (log2(i)*323) >> 9 <--- 2^9 == 512
     */
    int t = ((ilog2(i) + 1) * LN2_DIV_LN3_NUMERATOR) >>> 9;
    return (i < POW3[t]) ? t - 1 : t;
  }

  /**
   * Determines if {@code n} is a <a href="https://oeis.org/A163782">
   * Josephus2_prime</a>
   *
   * @param n constraint on n is that 2n+1 is prime
   * @return true if n is a J2 prime
   */
  public static boolean isJ2Prime(int n) {
    int count = 0, leader = 0;
    if (n % 4 == 1 || n % 4 == 2) {
      do {
        leader = Util.a025480(leader + n);
        count++;
      } while (leader != 0);
    }
    return count == n;
  }

  /**
   * Descending brute force test for Josephus_2 prime numbers
   *
   * @param n Starting number to test
   * @return first J2 prime found less than or equal to {@code n}
   */
  public static int findNextLowestJ2Prime(int n) {
//    assert n > 1 : "n must be greater than 1.";
    do {
      if (isJ2Prime(n)) {
        return n;
      }
    } while (n-- > 2);
    return 0;
  }

  /**
   * Similar to List.set. Sets the value at the specified index and returns
   * the old value that was at that index.
   *
   * @param array array to access
   * @param index index into array
   * @param value value to replace old value
   * @param <T>   type of array element
   * @return old value at that index
   */
  public static <T> T set(T[] array, int index, T value) {
    T oldValue = array[index];
    array[index] = value;
    return oldValue;
  }
}
