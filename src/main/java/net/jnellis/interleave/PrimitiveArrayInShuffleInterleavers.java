package net.jnellis.interleave;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;

import static net.jnellis.interleave.PermutationInterleaver.Constants;
import static net.jnellis.interleave.SequenceInterleaver.*;

/**
 * User: Joe Nellis
 * Date: 3/17/2023
 * Time: 3:21 PM
 */
public class PrimitiveArrayInShuffleInterleavers {

  /**
   * Utility class, no constructor.
   */
  private PrimitiveArrayInShuffleInterleavers(){}

  @FunctionalInterface
  interface IntBiConsumer{ void accept(int j, int k);}

  /**
   * Takes element from temp[b] and puts it in array[a]
   * @param array  destination array
   * @param temp   source array
   * @return an BiConsumer that takes index positions a & b
   */
   static IntBiConsumer arrayArraySetFunc(Object array, Object temp){
     assert array.getClass().isArray() : "array must be and Array type.";
     assert temp.getClass().isArray() : "temp must be an Array type.";
     return switch (array) {
       case int[]    i -> (a, b) ->  i[a] = ((int[]) (temp))[b];
       case long[]   l -> (a, b) ->  l[a] = ((long[]) (temp))[b];
       case double[] d -> (a, b) ->  d[a] = ((double[]) (temp))[b];
       case float[]  f -> (a, b) ->  f[a] = ((float[]) (temp))[b];
       case char[]   c -> (a, b) ->  c[a] = ((char[]) (temp))[b];
       case byte[]  bt -> (a, b) -> bt[a] = ((byte[]) (temp))[b];
       default -> throw new IllegalStateException("""
         Only primitive arrays allowed (int, long, double, float, char, byte)""");
     };
  }

  /**
   * swaps elements at two different index positions
   * @param array array
   * @return BiConsumer that swaps elements at indexes a & b
   */
   static IntBiConsumer swapFunc(Object array){
     assert array.getClass().isArray() : "array must be and Array type.";
     return  switch(array) {
       case int[]    a -> (j,k) ->{ int    i = a[j]; a[j] = a[k]; a[k] = i; };
       case long[]   a -> (j,k) ->{ long   i = a[j]; a[j] = a[k]; a[k] = i; };
       case double[] a -> (j,k) ->{ double i = a[j]; a[j] = a[k]; a[k] = i; };
       case float[]  a -> (j,k) ->{ float  i = a[j]; a[j] = a[k]; a[k] = i; };
       case char[]   a -> (j,k) ->{ char   i = a[j]; a[j] = a[k]; a[k] = i; };
       case byte[]   a -> (j,k) ->{ byte   i = a[j]; a[j] = a[k]; a[k] = i; };
       default -> throw new IllegalStateException("""
         Only primitive arrays allowed (int, long, double, float, char, byte)""");
     };
  }

  /**
   * Performs a swap at a single location to facilitate a cycle leader
   * permutation. The last swap of the cycle should end up at the initialIndex
   * location.
   * @param array array
   * @param initialIndex index position in array to swap items to.
   * @return a function that accepts the next array index position of the cycle.
   */
   static IntConsumer arrayCycleSet(Object array, int initialIndex){
     assert array.getClass().isArray() : "array must be and Array type.";
    // choose one position in array as a temp variable.
     final int i = initialIndex;
     return  switch(array) {
       case int[]    a -> (j) ->{ int t =    a[i]; a[i] = a[j]; a[j] = t; };
       case long[]   a -> (j) ->{ long t =   a[i]; a[i] = a[j]; a[j] = t; };
       case double[] a -> (j) ->{ double t = a[i]; a[i] = a[j]; a[j] = t; };
       case float[]  a -> (j) ->{ float t =  a[i]; a[i] = a[j]; a[j] = t; };
       case char[]   a -> (j) ->{ char t =   a[i]; a[i] = a[j]; a[j] = t; };
       case byte[]   a -> (j) ->{ byte t =   a[i]; a[i] = a[j]; a[j] = t; };
       default -> throw new IllegalStateException("""
         Only primitive arrays allowed (int, long, double, float, char, byte)""");
     };
  }

  /**
   * Rotate elements in an array similar to
   * {@link Collections#rotate(List, int)}
   *
   * @param array    array to rotate
   * @param from     starting index to rotate
   * @param to       ending index to rotate, exclusive
   * @param distance distance to rotate
   * @see Collections#rotate(List, int)
   */
   static void rotate(Object array, int from, int to, int distance) {
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
       int i = cycleStart;
       IntConsumer cycleSet = arrayCycleSet(array, from + i);
       do {
         i += dist;
         if (i >= size) {
           i -= size;
         }
         cycleSet.accept(from + i);
         nMoved++;
       } while (i != cycleStart);
     }
  }

  /**
   * Primitive array implementation of an in-shuffle interleaver that uses
   * new heap memory as a temporary work space.
   * @param array primitive type array
   * @param from  starting index
   * @param to    ending index (exclusive)
   * @see SimpleInterleaver
   */
  @SuppressWarnings("SuspiciousSystemArraycopy")
  public static void simple(Object array, int from, int to) {
    var type = array.getClass().componentType();
    assert type != null : "Parameter array must be an array object.";
    int halfSize = (to - from) / 2;
    var temp = Array.newInstance(type, halfSize << 1);
    IntBiConsumer arraySet = arrayArraySetFunc(array, temp);
    // copy first to prime caches
    System.arraycopy(array, from, temp, 0, halfSize << 1);
    // write back interleaving
    for (int i = 0, k = 0; k < halfSize; i += 2, k++) {
      arraySet.accept(from + i, halfSize + k);
      arraySet.accept(from + i + 1, k);
    }
  }

  /**
   * Primitive array implementation of an in-shuffle {@link SequenceInterleaver}.
   * @param array primitive type array
   * @param from  starting index
   * @param to    ending index (exclusive)
   * @see SequenceInterleaver
   */
  public static void sequence(Object array, int from, int to){
    IntBiConsumer swapFunc = swapFunc(array);

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
      for (; i < midpt; i++) {  //swap
        swapFunc.accept(from + i, from + midpt + Util.a025480(i - base));
      }

      // take odd length biased midpoint for swap count
      int swap_cnt = biasedMidpoint(i - base);
      // unscramble swapped items first half of remaining list
      for (int j = 0; j < swap_cnt - 1; j++) {
        int k = unshuffle(j, i - base);
        if (j != k) {  //swap
          swapFunc.accept(from + midpt + j, from + midpt + k);
        }
      }
      // push up the new midpoint to work on the remaining half of the list
      midpt += swap_cnt;
    }
  }

  /**
   * Primitive array implementation of an in-shuffle {@link PermutationInterleaver}.
   * @param array primitive type array
   * @param from  starting index
   * @param to    ending index (exclusive)
   * @see PermutationInterleaver
   */
  public static void permutation(final Object array, int from, final int to) {
    IntBiConsumer swapFunc = swapFunc(array);
    while (to - from > 1) {
      final int size = to - from;
      if (size < 4) {
        swapFunc.accept(from, from + 1);
        break;
      }

      final Constants c = Constants.from(size);
      if (c.m() != c.n()) {
        rotate(array, from + c.m(), from + c.m() + c.n(), c.m());
      }

      for (int k = 0; k < c.k(); k++) {
        final long u64c = Long.divideUnsigned(-1L, c.mod()) + 1L;
        final int startIdx = Util.POW3[k];
        int i = startIdx;
        int initialValIndex = from + Util.POW3[k] - 1;
        IntConsumer cycleSet = arrayCycleSet(array, initialValIndex);
        do {
          i = Util.fastmod(i * 2, u64c, c.mod());
          cycleSet.accept(from + i - 1);
        } while (i != startIdx);
      }

      from += (2 * c.m());
    }
  }

  /**
   * Primitive array implementation of an in-shuffle {@link RecursiveInterleaver}.
   * @param array primitive type array
   * @param from  starting index
   * @param to    ending index (exclusive)
   * @see RecursiveInterleaver
   */
  public static void recursive(Object array, int from, int to){
    IntBiConsumer swap = swapFunc(array);
    while (to - from > 1) {
      final int size = to - from;

      int midpt = size / 2;
      int k = Integer.highestOneBit(midpt);

      // when the list size isn't a power of 2
      if (k != midpt) {
        // rotate the difference out of the way
        rotate(array, from + k, from + k + midpt, k - midpt);
      }
      // continue with interleaving the front 2k of the list
      int base = 0;
      int m = k;
      while (base < 2 * k - 1) {
        int fb = from + base;
        // swap all in first half of list (k elements)
        for (int i = 0; i < m; i++) {
          swap.accept(fb + i, fb + m + Util.a025480(i));
        }

        // unscramble back half of list
        for (int j = 1; j <= m / 4; j <<= 1) {
          if (j < 2) {
            swap.accept(fb + m, fb + m + 1);
          } else {
            recursive(array, fb + m, fb + m + 2 * j);
          }
        }
        base += m;
        m /= 2;
      }
      // re-interleave the back of this front 2k section
      from += 2 * k;
    }
  }

  /**
   * Primitive array implementation of an in-shuffle {@link JosephusInterleaver}.
   * @param array primitive type array
   * @param from  starting index
   * @param to    ending index (exclusive)
   * @see JosephusInterleaver
   */
  public static void josephus(final Object array, int from, final int to) {
    IntBiConsumer swap = swapFunc(array);
    while (to - from > 1) {
      int midpt = (to - from) / 2;
      int k = Util.findNextLowestJ2Prime(midpt);

      for (int i = 0; i < k; i++) {
        swap.accept(from + i, from + midpt + Util.a025480(i));
      }

      int trailingIdx = 0;
      for (int i = 0; i < k - 1; i++) {
        int nextIdx = Util.a025480(k + trailingIdx);
        swap.accept(from + midpt + trailingIdx, from + midpt + nextIdx);
        trailingIdx = nextIdx;
      }

      if (k != midpt) {
        rotate(array, from + k, from + k + midpt, k - midpt);
      }
      from += 2 * k;
    }
  }

  /**
   * Primitive array implementation of an in-shuffle {@link ShufflePrimeInterleaver}.
   * @param array primitive type array
   * @param from  starting index
   * @param to    ending index (exclusive)
   * @see ShufflePrimeInterleaver
   */
  public static void shufflePrime(final Object array, int from, final int to) {
    IntBiConsumer swap = swapFunc(array);
    while (to - from > 1) {
      final int size = to - from;
      int midpt = size / 2;
      int j2 = Util.findNextLowestJ2Prime(midpt);
      int k = j2 * 2;

      if (k != size) {
        rotate(array, from + j2, from + j2 + midpt, j2 - midpt);
      }

      int idx = 0;
      int mod = k + 1;  // fyi, mod is a prime number
      final long u64_c = Long.divideUnsigned(-1L, mod) + 1;
      for (int i = 0; i < k; i++) {
        idx = Util.fastmod(2 * idx + 1, u64_c, mod);
        swap.accept(from + idx, from);
      }

      from += k;
    }
  }

}
