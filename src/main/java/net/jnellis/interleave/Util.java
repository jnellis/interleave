package net.jnellis.interleave;

import java.util.BitSet;
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
  // sample of j2primes from 2 to 2^29-ish
  static final int[] j2primes = {2, 5, 6, 9, 14, 18, 26, 33, 41, 53, 69,
      81, 98, 113, 134, 158, 194, 233, 270, 309, 354, 413, 473, 561, 653, 761,
      873, 1014, 1169, 1346, 1541, 1818, 2078, 2406, 2753, 3161, 3626, 4146,
      4745, 5433, 6218, 7121, 8181, 9350, 10689, 12221, 13973, 16001, 18293,
      20921, 23934, 27386, 31301, 35774, 40886, 46746, 53438, 61074, 69809,
      79785, 91193, 104234, 119130, 136158, 155618, 177861, 203273, 232370,
      265566, 303521, 346889, 396453, 453098, 517829, 591806, 676374, 773001,
      883449, 1009658, 1153910, 1318793, 1507193, 1722578, 1968698, 2249973,
      2571414, 2938761, 3358598, 3838430, 4386818, 5013510, 5729738, 6548273,
      7483761, 8552909, 9774773, 11171181, 12767069, 14590949, 16675373,
      19057574, 21780098, 24891545, 28447494, 32511449, 37155965, 42464010,
      48530321, 55463261, 63386609, 72441893, 82790738, 94617989, 108134846,
      123582726, 141237506, 161414330, 184473521, 210826910, 240945045,
      275365794, 314703774, 359661533, 411041769, 469762025, 536870894};

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
    a.set(i, b.set(Util.a025480(i), a.get(i)));
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

  public static boolean isSemiprime(int n) {
    // extract all factors of two in one go
    int c = Integer.numberOfTrailingZeros(n);
    n >>= c;
    int ceil = (int) Math.sqrt(n);
    for (int i = 3; c < 2 && i <= ceil; i += 2) {
      while (n % i == 0) {
        n /= i;
        c++;
      }
    }
    if (n > 1) {
      c++; // remainder is our other prime
    }
    return c == 2;
  }

  /**
   * Compute Josephus_2 primes up to some value. Josephus_2
   * primes are X_primes that provide a single cycle.
   * <p>
   * J2 primes are computed by traversing ordinary primes from double a ceiling
   * value down to 1. There are many J2 primes. To reduce this size and take
   * only a sample, provide a sample factor that increases the step size of the
   * down traversal of primes. The step size is governed such:
   * <p>
   * {@code nextPrime = currentPrime - currentPrime/sample}
   * <p>
   * Note: The largest step is when sample size is 2.
   * Sample size of 1 is special and returns only J2 primes which are also
   * ordinary primes themselves.
   *
   * @param primes a bitset of prime numbers, created by calling {@link #sieve}
   * @param sample if ;&lt;= 0 then don't sample,
   *               if 1 then return J2 primes which are also ordinary primes,
   *               else sample ;&gt; 1, reduce granularity of the search size by
   *               {@code prime/sample} each iteration. Suggested values
   *               are powers of 2, e.g. 2, 4, 8, 16, etc.
   * @return The set of J2 primes.
   * @see <a href="https://oeis.org/A163782">A163782</a>
   */
  public static BitSet josephus2primes(BitSet primes, int sample) {
    // large BitSets can be size negative due to int wraparound.
    int bsize = (int)(Integer.toUnsignedLong(primes.size()) / 2);
    BitSet j2primes = new BitSet(bsize);
//    assert ceiling <= 1 << 29 : "Reduce ceiling to below 2^29";
//    BitSet primes = sieve(2 * ceiling);
//    primes.clear(3);
//    System.out.println("prime sieve initialized...");
    // if N is a j2 prime, 2N + 1 is guaranteed to be an ordinary prime
    // so only search from known primes.
    int prime = primes.nextSetBit(4);
    while ((prime = primes.nextSetBit(prime + 1)) != -1) {
      int N = (prime - 1) / 2;
      // only consider j2 primes that are also ordinary primes
      if (sample == 1 && !primes.get(N))
        continue;
      // j2 primes must be congruent to 1 (mod 4) or 2 (mod 4)
      if (N % 4 != 3) {
//        assert N % 4 != 0;
        if (sample == 1) {
          j2primes.set(N);
          continue;
        }

//        final int cycleStart = 0;
//        int leader = cycleStart;
//        int count = 0;
//        do {
//          leader = Util.a025480(N + leader);
//          count++;
//        } while (leader != cycleStart);
        int count = z2orderA025480(N);
        if (count == N) {
          j2primes.set(N);
          if (sample > 1) {
            System.out.print(N);
            System.out.print(", ");
            System.out.flush();
            prime = prime + prime / sample;
            if(prime < 0) break;
          }
        }
      }
    }
//    System.out.println();
//    System.out.println(j2primes.cardinality() + " J2 primes found.");
    return j2primes;
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
   * Determines if {@code n} is a <a href="https://oeis.org/A163782">
   *   Josephus2_prime</a>
   * @param n   constraint on n is that 2n+1 is prime
   * @return
   *
   */
  public static boolean isJ2Prime(int n){
    int count = 0, leader = 0;
    if(n % 4 == 1 || n % 4 == 2){
      do{
        leader = Util.a025480(leader + n);
        count++;
      }while(leader != 0);
    }
    return count == n;
  }

  public static BitSet computeJ2Primes(BitSet primes, int sample) {
    // large BitSets can be size negative due to int wraparound.
    int bsize = (int)(Integer.toUnsignedLong(primes.size()) / 2);
    BitSet j2primes = new BitSet(bsize);

    int prime = primes.nextSetBit(4);
    while ((prime = primes.nextSetBit(prime + 1)) != -1) {
      int N = (prime - 1) / 2;
      // only consider j2 primes that are also ordinary primes
      // OEIS A103579, Sophie Germain primes that are not Lucasian primes.
      if (sample == 1 && !primes.get(N))
        continue;

      // Per OEIS.org/A163782
      if (N % 4 == 1 || N % 4 == 2) {
        if (sample == 1) {
          j2primes.set(N);
          continue;
        }
        int k = z2order2(prime);
        if (2*k == prime - 1) {
          j2primes.set(N);
          if (sample > 1) {
            System.out.print(k);
            System.out.print(", ");
            System.out.flush();
            prime = prime + prime / sample;
            if(prime < 0) break;
          }
        }
      }
    }
    return j2primes;
  }

  private static int z2orderA025480(int n){
    int leader = 0;
    int count = 0;
    do {
      leader = Util.a025480(n + leader);
      count++;
    } while (leader != 0);
    return count == n ? n : n - count;
  }


  /**
   * Brute force z2order using Lemire fastmod reduction.
   * @param prime a prime number
   * @return z2order
   */
  private static int z2orderFastmod(int prime) {
    // preamble consts for fastmod
    int pd = Math.abs(prime);
    long u64_c = Long.divideUnsigned(-1L, pd) + 1L;
    //          + ((pd & (pd-1L))==0 ? 1L : 0);  // unneeded part of algo

    // skip a bit brother
    // k upto 2^log2(p) will be less than p and not 1
    int k = ilog2(prime);
    int rem = 1 << k;
    // cycle to find 2^p ≡ 1 (mod prime)
    while (k < prime) {
      long u64lowbits = rem * u64_c * 2;
      rem = (int) Math.unsignedMultiplyHigh(u64lowbits, pd);
      k++;
      if (rem == 1) {
        return k;
      }
    }
    return -1;
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
   * Brute force z2order
   * @param p  a prime
   * @return z2order
   */
  private static int z2order2(int p) {
    int k = ilog2(p);
    int rem = 1 << k;
    while (k < (p-1)/2) {
      rem = (rem * 2) % p;
      k++;
      if (rem == 1) {
        return k;
      }
    }
    return k;
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
   * Sieve of Eratosthenes
   *
   * @param ceiling Precompute all primes below this number.
   * @return BitSet with indexes of primes set to true.
   */
  public static BitSet sieve(int ceiling) {
    if(ceiling < 0)
      throw new IllegalArgumentException("Ceiling must be positive.");
    BitSet sieve = new BitSet(ceiling);
    // 2 is the first prime number
    int p = 2;
    while (  (long)p * (long)p <= (long) ceiling) {
      // set the bit of indexes that are multiples of p
      // but starting from p^2 because previous lower multiples
      // have already been covered.
      int i = p * p;
      while (i <= ceiling && i > 0) {
        sieve.set(i);
        i = i + p;
      }
      p = sieve.nextClearBit(p + 1);
    }
    sieve.flip(2,ceiling);
    return sieve;
  }

  public static void swap(int[] array, int from, int to) {
    int temp = array[to];
    array[to] = array[from];
    array[from] = temp;
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

  public static int findNextLowestJ2Prime(int n){
    do{
      if(isJ2Prime(n)){
        return n;
      }
    } while(n-- > 2);
    return 0;
  }
}
