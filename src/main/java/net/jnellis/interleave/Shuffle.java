package net.jnellis.interleave;

/**
 * Descriptor to identify types of interleaving shuffle operations on
 * {@link Interleaver}s.
 *
 * @see Interleavers
 */
public enum Shuffle {
  /**
   * In-shuffle
   */
  IN(true, false),
  /**
   * Out-shuffle
   */
  OUT(false, false),
  /**
   * Folding In-Shuffle
   */
  IN_FOLDING(true, true),
  /**
   * Folding Out-Shuffle
   */
  OUT_FOLDING(false, true);

  /**
   * If true, shuffle should be folding
   */
  public final boolean folding;
  /**
   * if true, shuffle should be an In-Shuffle
   */
  public final boolean in;
  /**
   * Maintained as the opposite of {@link #in}, if true, shuffle should be
   * Out-Shuffle.
   */
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
