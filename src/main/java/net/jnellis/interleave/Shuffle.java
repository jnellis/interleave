package net.jnellis.interleave;

/**
 * Descriptor to identify types of interleaving shuffle operations on
 * {@link Interleaver}s.
 *
 * @see Interleavers
 */
public enum Shuffle {
  IN(true, false), OUT(false, false),
  IN_FOLDING(true, true), OUT_FOLDING(false, true);

  public final boolean folding;
  public final boolean in;
  public final boolean out;

  Shuffle(boolean inShuffle, boolean folding) {
    in = inShuffle;
    out = !inShuffle;
    this.folding = folding;
  }

  /**
   * Returns the opposite shuffle type but maintains whether it was folding
   * or not.
   *
   * @return An in-shuffle object returns and out-shuffle object and vice-versa.
   */
  public Shuffle opposite() {
    if (folding) {
      return in ? OUT_FOLDING : IN_FOLDING;
    }
    return in ? OUT : IN;
  }

  /**
   * Presents the same shuffle, without folding.
   * @return a similar shuffle object without folding.
   */
  public Shuffle nonFolding() {
    return in ? IN : OUT;
  }
}
