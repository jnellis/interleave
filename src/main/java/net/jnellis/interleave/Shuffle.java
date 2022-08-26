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
		this.in = inShuffle;
		this.out = !inShuffle;
		this.folding = folding;
	}
}
