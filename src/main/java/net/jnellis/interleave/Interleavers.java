package net.jnellis.interleave;

import java.util.List;

/**
 * A suite of algorithms to perform interleaving of one or two collections,
 * in place, without heap allocation.
 *
 * <pre>
 * {@code
 *    List<Object> list = Arrays.asList(1,2,3,"a","b","c");
 *    Interleavers.inPlace.interleave(list, Shuffle.IN);
 *
 *    list order is now  ["a", 1, "b", 2, "c", 3]
 * }</pre>
 * <p>
 *
 * Motivation was derived from this topic regarding three different methods for
 * in-place shuffling of collections.
 * <a
 * href="https://cs.stackexchange.com/questions/332/in-place-algorithm-for-interleaving-an-array/105263#105263">https://cs.stackexchange.com/questions/332/in-place-algorithm-for-interleaving-an-array/105263#105263</a>
 *
 * @see Interleaver
 */
public final class Interleavers {

  public static final Interleaver permutation = new PermutationInterleaver();
  public static final Interleaver a025480 = new SequenceInterleaver();
  public static final Interleaver recursive = new RecursiveInterleaver();
                
  private Interleavers() {}

  /**
   * Interleave two lists into a new list.
   *
   * @param a         first list
   * @param b         second list
   * @param inShuffle if true, start the interleaving with the second list.
   *                  otherwise, perform an out-shuffle.
   * @param <T>       type of element the list holds.
   * @return A new list with references to elements of lists a and b
   * interleaved.
   */
  public static <T> List<T> interleave(List<T> a,
                                       List<T> b,
                                       boolean inShuffle) {
    return RotatingQueueInterleaver.interleave(a, b, inShuffle);
  }

  /**
   * Creates a new list from interleaving the elements of multiple lists.
   * The order is an out shuffling, where the first element comes from the
   * first list. If all lists are of the same size then the last element
   * will come from the last list.
   * <p>
   * For lists that of unequal sizes, the interleaving continues placing the
   * next element from the next list with available elements.
   * <p>
   * For maintaining the interleaving stride for uneven lists,
   * {@link RotatingQueueInterleaver#interleave(Object, List[])}
   * interleave(spacer,lists)}
   *
   * @param lists Lists to be interleaved
   * @param <T>   type of element the list holds.
   * @return new list of interleaved elements
   */
  @SafeVarargs
  public static <T> List<T> interleave(List<T>... lists) {
    return RotatingQueueInterleaver.interleave(lists);
  }

  @SafeVarargs
  public static <T> List<T> interleave(T spacer, List<T>... lists) {
    return RotatingQueueInterleaver.interleave(spacer, lists);
  }

}
