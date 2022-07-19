package net.jnellis.interleave;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * User: Joe Nellis Date: 6/5/2022 Time: 7:58 PM
 */
public class Util {
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
   * Modular Exponentiation: wrapper to using BigInteger's modPow method.
   *
   * @param a base
   * @param b exponent
   * @param c modulo
   * @return remainder of (a^b) % c
   */
  public static long bigIntModPow(int a, int b, int c) {
    return BigInteger.valueOf(a)
                     .modPow(BigInteger.valueOf(b), BigInteger.valueOf(c))
                     .longValueExact();
  }

  /**
   * Fast Modular Exponentiation: performs (a^b) % c Note: (a^b) is an
   * exponentiation function, not exclusive-or
   *
   * @param a base
   * @param b exponent
   * @param c modulo
   * @return remainder of (a^b) % c
   */
  public static long fastmod(int a, int b, int c) {
    long x = 1, y = a;
    while (b > 0) {
      if ((b & 1) == 1) {
        x = (x * y) % c;
      }
      y = (y * y) % c;
      b >>= 1;
    }
    return x % c;
  }

  public static boolean isOdd(int val) {
    return (val & 1) == 1;
  }

  public static <T> void rotateRight(List<T> list, int positions) {
    rotateViaCycleLeader(list, positions, false);
  }

  public static <T> void rotateLeft(List<T> list, int positions) {
    rotateViaCycleLeader(list, positions, true);
  }

  static <T> void rotateViaCycleLeader(List<T> list, int m, boolean cycleLeft) {
    int n = list.size();
    m = m % n; // if m is bigger than the list size, reduce redundancy
    if (cycleLeft) {
      m = n - m;
    }
    int sets = gcd(n, m);
    for (int i = 0; i < sets; i++) {
      T temp = list.get(i);
      int j = i;
      do {
        j += m;
        // j = (j + m) % n
        // checking for overrun vs explicit modulus everytime seems to
        // be faster for lower sizes of m (branch prediction perhaps.)
        if (j >= n)
          j %= n;
        temp = list.set(j, temp);
      } while (j != i);
    }
  }

  /**
   * Rotates right the list m positions via the triple reverse algorithm.
   *
   * @param list list to rotate
   * @param m    number of positions to rotate right
   * @param <T>  type of list item
   */
  public static <T> void rotateViaTripleReverse(List<T> list, int m) {
    m = list.size() - m % list.size();
    Collections.reverse(list.subList(0, m));
    Collections.reverse(list.subList(m, list.size()));
    Collections.reverse(list);
  }

  public static int gcd(int a, int b) {
    while (b > 0) {
      int c = a % b;
      a = b;
      b = c;
    }
    return a;
  }

  /**
   * Corresponds to an integer sequence of the
   * <a href="https://oeis.org/A025480">https://oeis.org/A025480</a>
   * which generates the index positions for the proper sequence of swaps (and
   * swap backs) for the interleave operation, given that series of swaps was
   * done sequentially and in order.
   *
   * Implementation note:  The algorithm effectively right shifts the value of n
   * just past the lowest clear bit.
   *
   * @param n index on the left side to swap
   * @return index on the right side to swap (plus a midpoint offset)
   */
  static int a025480(int n) {
    return n >> (Integer.numberOfTrailingZeros(~n) + 1);
  }

  static int mid(int size) {return size - (size >> 1);}

  static boolean isEven(int n) {return (n & 1) == 0;}

  static int log2(int i) {
    return i == 0 ? 0 : 31 - Integer.numberOfLeadingZeros(i);
  }
}
