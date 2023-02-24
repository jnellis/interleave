package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An implementation of
 * <a href="https://arxiv.org/pdf/0805.1598.pdf">
 * A Simple In-Place Algorithm for In-Shuffle</a>
 *
 * @see <a href="https://cs.stackexchange.com/a/400">
 * https://cs.stackexchange.com/a/400</a>
 * @see Interleaver
 */
public final class PermutationInterleaver extends AbstractInterleaver {

  /**
   * No-arg constructor provided for use by {@link Interleavers} which creates
   * single instances. Use {@link Interleavers#PERMUTATION}
   */
  PermutationInterleaver() {}

  @SuppressWarnings({"rawtypes","unchecked"})
  protected void interleave(List<?> list) {
    while (list.size() > 1) {
      final int size = list.size();
      if (size < 4) {
        Collections.swap(list, 0, 1);
        break;
      }

      final Constants c = Constants.from(size);

      if (c.m != c.n) { // when size is not an exact power of 3
        // rotate the 2nd half of m elements into position to be swapped. 
        Collections.rotate(list.subList(c.m, c.m + c.n), c.m);
        // a concurrent list implementation would allow forking the remainder here
      }
      // For each i ∈ {0, 1, . . . , k − 1}, starting at 3^i, do the cycle leader
      // algorithm for the in-shuffle permutation of order 2m elements
      final List l = list;
      for (int i = 0; i < c.k; i++) {
        cycleLeader(i, c.mod, l::get, l::set);
      }
      // shorten the list to work on remaining elements.
      list = list.subList(2 * c.m, size);
    }
  }

  /*
   * Cycle leader for lists using Lemire fastmod. Using lambdas turns out to
   * be faster than not, so said jmh.
   */
  private static <T> void cycleLeader(final int k, final int mod,
                                      final Function<Integer,T> getter,
                                      final BiFunction<Integer,T,T> setter ){
    final long u64c = Long.divideUnsigned(-1L, mod) + 1L;
    final int startIdx = Util.POW3[k];
    int i = startIdx;
    T leader = getter.apply(i - 1);
    do {
      // i = ((i * 2) % c.mod) ; // slow mod for history sake
      i = Util.fastmod(i * 2, u64c, mod);
      leader = setter.apply(i - 1, leader);
    } while (i != startIdx);
  }


  protected void interleave(final Object[] array, int from, final int to) {
    while (to - from > 1) {
      final int size = to - from;
      if (size < 4) {
        Util.swap(array, from, from + 1);
        break;
      }

      final Constants c = Constants.from(size);
      if (c.m != c.n) {
        Util.rotate(array, from + c.m, from + c.m + c.n, c.m);
      }

      int _from = from;
      Function<Integer, Object> getter = (i) -> array[_from + i];
      BiFunction<Integer, Object, Object> setter =
          (i, obj) -> Util.set(array, _from + i, obj);

      for (int i = 0; i < c.k; i++) {
        cycleLeader(i, c.mod, getter, setter);
      }

      from += (2 * c.m);
    }
  }

  protected <T> void interleave(List<T> a, final List<T> b) {
    assert !a.isEmpty() : "Lists should not be empty.";
    assert a.size() == b.size(): "Lists should be equal sizes at start.";
    while (true) {
      int aSize = a.size(), bSize = b.size();
      if (aSize + bSize < 4) {
        a.set(0, b.set(0, a.get(0)));
        break;
      }

      final Constants c = Constants.from(aSize + bSize);
      if (c.m != c.n) {
        if (c.m > aSize) {  // just rotate b side
          Collections.rotate(b.subList(c.m - aSize, c.m + c.n - aSize), c.m);
        } else {
          Util.rotate(a.subList(c.m, aSize), b.subList(0, c.m + c.n - aSize), c.m);
        }
      }

      List<T> _a = a;
      Function<Integer,T> getter = (i)-> i < aSize ? _a.get(i)
                                                   : b.get(i-aSize);
      BiFunction<Integer,T,T> setter = (i,t)-> i < aSize ? _a.set(i,t)
                                                         : b.set(i - aSize, t);

      for (int k = 0; k < c.k; k++) {
        cycleLeader(k,c.mod,getter,setter);
      }

      // adjust a & b to account for 2*m elements we just moved around
      if (aSize > 2 * c.m) {
        a = a.subList(2 * c.m, aSize);
      } else {
        // no more a left, just work on b.
        interleave( b.subList(2 * c.m - aSize, bSize));
        break;
      }
    }
  }

  protected <T> void interleave(final T[] a, int fromA, final int toA,
                                final T[] b, final int fromB, final int toB) {
    assert toA - fromA != 0 : "Lists should not be empty.";
    assert toA - fromA == toB - fromB: "Lists should be equal sizes at start.";
    while (true) {
      int aSize = toA - fromA, bSize = toB - fromB;
      if (aSize + bSize < 4) {
        Util.swap(a, fromA, b, fromB);
        break;
      }

      Constants c = Constants.from(aSize + bSize);
      if(c.m != c.n) {
        final int _tob = fromB + c.m + c.n - aSize;
        if (c.m > aSize) {  // just rotate b side
          Util.rotate(b, fromB + c.m - aSize, _tob, c.m);
        } else {
          Util.rotate(a, fromA + c.m, toA, b, fromB, _tob, c.m);
        }
      }

      int _fromA = fromA;
      Function<Integer,T> getter = (i) -> i < aSize ? a[_fromA + i]
                                                    : b[fromB + i - aSize];
      BiFunction<Integer,T,T> setter =
          (i, obj) -> i < aSize ? Util.set(a, _fromA + i, obj)
                                : Util.set(b, fromB + i - aSize, obj);

      for (int k = 0; k < c.k; k++) {
        cycleLeader(k,c.mod,getter,setter);
      }

      // adjust a & b to account for 2*m elements we just moved around
      if (aSize > 2 * c.m) {
        fromA += 2 * c.m;
      } else {
        // no more a left, just work on b.
        interleave(b, fromB + (2 * c.m) - aSize, bSize);
        break;
      }
    }
  }

  /**
   * For each round of permutations swaps we need some constants for that
   * round that describe the amount of elements that we'll be processing.
   */
  record Constants(int n, int k, int mod, int m){
    public static Constants from(int size){
      // Find a 2m = 3^k − 1 such that 3^k ≤ 2n < 3^(k+1)
      int n = size / 2;  // half the size of the entire collection
      int k = Util.ilog3(size); // minimum power of 3 elements to be working on
      int mod = Util.POW3[k]; // cycle modulus
      int m = (mod - 1) >> 1;  // m <= n, 2m is the number of elements moved at a time.
      return new Constants(n, k, mod, m);
    }
  }
}