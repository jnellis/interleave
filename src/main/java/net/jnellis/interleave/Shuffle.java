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

	public Shuffle opposite(){
	  if(folding){
			return in ? OUT_FOLDING : IN_FOLDING;
	  }
		return in ? OUT : IN;
	}

	public Shuffle nonFolding(){
		return in ? IN : OUT;
	}

	Shuffle(boolean inShuffle, boolean folding) {
		this.in = inShuffle;
		this.out = !inShuffle;
		this.folding = folding;
	}
}
