package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import static net.jnellis.interleave.Util.set;

/**
 * Implementation of Shuffle Prime interleaving based on Josephus_2 Primes
 * @see <a href="https://research.utwente.nl/en/publications/permuting-operations-on-strings-their-permutations-and-their-prim">
 *   Permuting Operations on Strings: Their Permutations and Their Primes</a>
 * @see <a href="https://oeis.org/A163782">OEIS A163782</a>
 */
public class ShufflePrimeInterleaver extends AbstractInterleaver {

  /**
   * No-arg constructor provided for use by {@link Interleavers} which creates
   * single instances. Use {@link Interleavers#SHUFFLE}
   */
  ShufflePrimeInterleaver() {}

  @SuppressWarnings({"rawtypes", "unchecked"})
  protected void interleave(List<?> list) {
    while (list.size() > 1) {
      final int size = list.size();

      int midpt = size / 2;
      int j2 = Util.findNextLowestJ2Prime(midpt);
      int k = j2 * 2; // The Shuffle Prime

      if (k != size) {
        // rotate unhandled elements out of the way before interleaving because
        // the cycle leader bounces around both halves of the list.
        Collections.rotate(list.subList(j2, j2 + midpt), j2 - midpt);
      }

      // interleave this section.
      cycleLeader(k, ((List) list).get(0), ((List) list)::set);

      // reset list to interleave the rest.
      list = list.subList(k, size);
    }
  }

  /**
   * Cycle leader algorithm for interleaving elements from the start and a k/2
   * midpoint of a collection(s).
   * @param k Shuffle prime, always an even number, twice its J2 prime.
   * @param initialValue The value at the start of the list, in lieu of
   *                     providing a getter for just one use.
   * @param setter instance SET method reference or lambda
   * @see List#set(int, Object)
   */
  @SuppressWarnings({"unchecked"})
  private static <T> void cycleLeader(final int k,
                                      final Object initialValue,
                                      final BiFunction<Integer, T, T> setter) {
    int idx = 0;
    int mod = k + 1;
    final long u64_c = Long.divideUnsigned(-1L, mod) + 1;
    T leader = (T) initialValue;
    for (int i = 0; i < k; i++) {
      idx = Util.fastmod(2 * idx + 1, u64_c, mod);
      leader = setter.apply(idx, leader);
    }
  }

  protected void interleave(final Object[] array, int from, final int to) {
    while (to - from > 1) {
      final int size = to - from;
      int midpt = size / 2;
      int j2 = Util.findNextLowestJ2Prime(midpt);
      int k = j2 * 2;

      if (k != size) {
        Util.rotate(array, from + j2, from + j2 + midpt, j2 - midpt);
      }

      int _from = from;
      cycleLeader(k, array[from],
                  (i, obj) -> set(array, _from + i, obj)); // setter

      from += k;
    }
  }

  protected <T> void interleave(final List<T> a, final List<T> b) {
    assert !a.isEmpty() : "Lists should not be empty.";
    assert a.size() == b.size() : "Lists should be equal sizes at start.";

    int size = a.size();

    int j2 = Util.findNextLowestJ2Prime(size);
    int k = j2 * 2;

    if (j2 != size) {
      Util.rotate(a.subList(j2, size), b.subList(0, j2), j2 - size);
      // NOTE: There will always exist a j2 big enough leave 'a' interleaved.
      interleave(b.subList(k - size, size));
    }

    cycleLeader(k, a.get(0),
                (Integer i, T obj) -> i < size ? a.set(i, obj)
                                               : b.set(i - size, obj));
  }

  protected <T> void interleave(final T[] a, final int fromA, final int toA,
                                final T[] b, final int fromB, final int toB) {
    assert toA - fromA != 0 : "Lists should not be empty.";
    assert toA - fromA == toB - fromB : "Lists should be equal sizes at start.";

    int size = toA - fromA;

    int j2 = Util.findNextLowestJ2Prime(size);
    int k = j2 * 2;

    if (j2 != size) {
      Util.rotate(a, fromA + j2, toA, b, fromB, fromB + j2, j2 - size);
      // NOTE: There will always exist a j2 big enough leave 'a' interleaved.
      interleave(b, fromB + k - size, size);
    }

    cycleLeader(k, a[fromA],
                (Integer i, T obj) -> i < size ? set(a, fromA + i, obj)
                                               : set(b, fromB + i - size, obj));
  }
}
