package net.jnellis.interleave;

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
}
