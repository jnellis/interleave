package net.jnellis.interleave;

/**
 * A suite of algorithms to perform interleaving of one or two collections,
 * in place, without heap allocation.
 *
 * <pre>
 * {@code
 *    List<Object> list = Arrays.asList(1,2,3,"a","b","c");
 *    Interleavers.SEQUENCE.interleave(list, Shuffle.IN);
 *
 *    // list order is now  ["a", 1, "b", 2, "c", 3]
 * }</pre>
 * <p>
 * Motivation was derived from
 * <a href="https://cs.stackexchange.com/questions/332/in-place-algorithm-for-interleaving-an-array/105263#105263">
 * this topic</a> regarding three different methods for
 * in-place shuffling of collections.
 *
 * @see Interleaver
 */
public final class Interleavers {

  /**
   * Implementation of in-place interleaving using
   * <a href="https://cs.stackexchange.com/a/400">rotation and permutation.</a>
   *
   * @see <a href="https://arxiv.org/pdf/0805.1598.pdf">
   * A Simple In-Place Algorithm for In-Shuffle</a>
   */
  public static final Interleaver PERMUTATION = new PermutationInterleaver();
  /**
   * Implementation of in-place interleaving using a
   * <a href="https://cs.stackexchange.com/a/105263">sequence algorithm</a> for
   * determining swap positions.
   */
  public static final Interleaver SEQUENCE = new SequenceInterleaver();
  /**
   * Implementation of in-place interleaving using a
   * <a href="https://cs.stackexchange.com/a/403">divide and conquer
   * recursive algorithm</a> while utilizing both ideas of the permutation and
   * sequence algorithms above.
   */
  public static final Interleaver RECURSIVE = new RecursiveInterleaver();

  private Interleavers() {}
}
