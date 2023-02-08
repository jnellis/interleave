package net.jnellis.interleave;

import java.util.List;

/**
 * Perform a sequence interleave up to the first half and then performs a j2
 * prime cycle on the remaining half.
 */
public class JosephusInterleaver implements Interleaver {

  /**
   * No-arg constructor provided for use by {@link Interleavers} which creates
   * single instances. Use {@link Interleavers#JOSEPHUS}
   */
  public JosephusInterleaver() {
  }

  @Override
  public <T> void interleave(List<T> list, Shuffle shuffle) {

  }

  @Override
  public <T> void interleave(T[] array, int from, int to, Shuffle shuffle) {
    int size = to - from;
    if (size > 1) {
      if (shuffle.out) { // out-shuffle
        if (size == 2) { return;  } // too small, no change
        from++;
        size--;
      }
      if (shuffle.folding) {
        Util.reverse(array, from + (size / 2), to);
      }
      interleave(array, from, to);
    }
  }

  private int findNextLowestJ2Prime(int n){
    do{
      if(Util.isJ2Prime(n)){
        return n;
      }
    } while(n-- > 2);
    return 0;
  }

  private <T> void interleave(T[] array, int from, int to) {
    int size = to - from;
    if(size < 2) return;
    if(size < 4){
      Util.swap(array,from, from+1);
      return;
    }
    int midpt = (size) >> 1;
    // choose closest J2-prime less than midpt
//    int pos = Arrays.binarySearch(Util.j2primes, midpt);
//
//    int k = (pos < 0) ? Util.j2primes[-(pos+2)] : Util.j2primes[pos];
    int k = findNextLowestJ2Prime(midpt);

    if (k != midpt) {
      // rotate difference out of the way
      Util.rotate(array, from + k, to, k - midpt);
      interleave(array, from + 2 * k + 1, to);
    }

    // swap front half
    for (int i = 0; i < k; i++) {
      Util.swap(array, from + i, from + k + Util.a025480(i));
    }

    // J2 prime cycle to fix scrambled back quarter and interleave the rest
    int base = from + k;
    int trailerIdx = 0;
    T initialVal = array[base + trailerIdx];
    for (int i = 0; i < k - 1; i++) {
      int next = Util.a025480(k + trailerIdx);
      array[base + trailerIdx] = array[base + next];
      trailerIdx = next;
    }
    array[base + trailerIdx] = initialVal;
  }

  @Override
  public <T> void interleave(List<T> a, List<T> b, Shuffle shuffle) {

  }

  @Override
  public <T> void interleave(T[] a,
                             int fromA,
                             int toA,
                             T[] b,
                             int fromB,
                             int toB,
                             Shuffle shuffle) {

  }
}
