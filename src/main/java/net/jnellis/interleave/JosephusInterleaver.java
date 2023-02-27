package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * Perform a {@link SequenceInterleaver} interleave up to the first half and
 * then performs a Josephus_2 Prime cycle on the remaining half.
 * @see <a href="https://oeis.org/A163782">OEIS A163782</a>
 */
public class JosephusInterleaver extends AbstractInterleaver {

  /**
   * No-arg constructor provided for use by {@link Interleavers} which creates
   * single instances. Use {@link Interleavers#JOSEPHUS}
   */
  public JosephusInterleaver() {
  }

  /**
   * This is an index offset for the cycle trailer algorithm.
   * When interleaving between two collections, we need to perform the
   * cycle trailer algorithm on the second collection only so the offset is 0.
   * For single collections, the offset will be the starting point of the
   * second half of the collection to be interleaved.
   */
  public final static int COLLECTION_B_ONLY = 0;

  @SuppressWarnings({"rawtypes","unchecked"})
  protected void interleave(List<?> list){
    while(list.size() > 1) {
      final int size = list.size();
      int midpt = size / 2;
      // process in chunks based on J2 prime-sized cycles
      int k = Util.findNextLowestJ2Prime(midpt);

      // swap first half
      for (int i = 0; i < k; i++) {
        Collections.swap(list, i, midpt + Util.a025480(i));
      }
      // interleave back half using Josephus_2 prime cycle
      cycleTrailer(k, midpt, ((List) list)::get, ((List) list)::set);

      // rotate left the un-interleaved elements between k and midpt
      // into the back half of the list.
      if (k != midpt) {
        Collections.rotate(list.subList(k, k + midpt), k - midpt);
      }
      // restart the interleave process on the end bit of the list.
      list = list.subList(2 * k, size);
    }
  }

  /**
   * The Josephus_2 prime cycle trailer. Takes a forward value and places
   * it at the trailing index.
   * @param k a Josephus_2 prime that coincides with size of a previously
   *          half interleaved collection or two collections.
   * @param offset For single collections being interleaved, the offset is a
   *               J2 prime (k) if rotating elements before interleaving,
   *               or the midpoint if rotating elements after interleaving.
   *               For two collections the offset should be 0.
   * @param getter An instance GET method reference or lambda
   * @param setter An instance SET method reference or lambda
   * @see List#set(int, Object)
   */
  private static <T> void cycleTrailer(final int k, final int offset,
                                       final Getter<T>  getter,
                                       final Setter<T>  setter){
    int trailerIdx = 0;
    T initialVal = getter.get(offset + trailerIdx);
    for (int i = 0; i < k - 1; i++) {
      int nextIdx = Util.a025480(k + trailerIdx);
      setter.set(offset + trailerIdx, getter.get(offset + nextIdx));
      trailerIdx = nextIdx;
    }
    setter.set(offset + trailerIdx, initialVal);
  }

  protected void interleave(final Object[] array, int from, final int to) {
    while (to - from > 1) {
      int midpt = (to - from )/ 2;
      int k = Util.findNextLowestJ2Prime(midpt);

      for (int i = 0; i < k; i++) {
        Util.swap(array, from + i, from + midpt + Util.a025480(i));
      }

      final int _from = from; // for lambdas
      cycleTrailer(k, midpt,
                   (i) -> array[_from + i], // getter
                   (i, obj) -> Util.set(array, _from + i, obj)); // setter

      if (k != midpt) {
        Util.rotate(array, from + k, from + k + midpt, k - midpt);
      }
      from += 2 * k;
    }
  }

  protected <T> void interleave(List<T> a, List<T> b) {
    assert !a.isEmpty() : "Lists should not be empty.";
    assert a.size() == b.size(): "Lists should be equal sizes at start.";

    int size = a.size();
    int k = Util.findNextLowestJ2Prime(size);

    for (int i = 0; i < k; i++) {
      a.set(i, b.set(Util.a025480(i), a.get(i)));
    }

    cycleTrailer(k, COLLECTION_B_ONLY, b::get, b::set);

    if(k != size){
      Util.rotate(a.subList(k, size), b.subList(0, k), k - size);
      // NOTE: There will always exist a j2 big enough to leave 'a' interleaved.
      interleave(b.subList(2 * k - size, size)); // single list interleave
    }
  }

  protected <T> void interleave(T[] a, int fromA, int toA,
                                T[] b, int fromB, int toB) {
    assert toA - fromA != 0 : "Lists should not be empty.";
    assert toA - fromA == toB - fromB : "Lists should be equal sizes at start.";

    int size = toA - fromA;
    int k = Util.findNextLowestJ2Prime(size);

    for (int i = 0; i < k; i++) {
      Util.swap(a, fromA + i, b, fromB + Util.a025480(i));
    }

    final int _from = fromB;  // for lambdas
    cycleTrailer(k, COLLECTION_B_ONLY,
                 (i) -> b[_from + i],  // getter
                 (i, obj) -> Util.set(b, _from + i, obj)); // setter

    if (k != size) {
      Util.rotate(a, fromA + k, toA, b, fromB, fromB + k, k - size);
      // NOTE: There will always exist a j2 big enough to leave 'a' interleaved.
      interleave(b, fromB + 2 * k - size, toB); // single array interleave
    }
  }
}
