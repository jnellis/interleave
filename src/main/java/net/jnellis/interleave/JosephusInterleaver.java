package net.jnellis.interleave;

import java.util.Collections;
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
    if(list.size() > 1){
      if (shuffle.out) {
        list = list.subList(1,list.size());
      }
      if(shuffle.folding){
        Collections.reverse(list.subList(list.size()/2, list.size()));
      }
      interleave(list);
    }
  }

  private static <T> void interleave(List<T> list){
    int size = list.size();
    if(size<2) return;
    if(size<4){
      Collections.swap(list, 0,1);
      return;
    }
    int midpt = size/2;
    // process in chunks based on J2 prime-sized cycles
    int k = findNextLowestJ2Prime(midpt);

    // rotate elements we're not working on out of the way.
    if(k != midpt){
      Collections.rotate(list.subList(k,size),k-midpt);
      interleave(list.subList(2*k+1,size));
    }

    // swap front half
    for (int i = 0; i < k; i++) {
      Collections.swap(list,i,k+Util.a025480(i));
    }

    // Perform a J2 prime cycle to fix back third quarter and interleave the rest
    int trailerIdx = 0;
    T initialVal = list.get(k + trailerIdx);
    for (int i = 0; i < k-1; i++) {
      int nextIdx = Util.a025480(k + trailerIdx);
      list.set(k + trailerIdx, list.get(k + nextIdx));
      trailerIdx = nextIdx;
    }
    list.set(k + trailerIdx, initialVal);

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

  private static int findNextLowestJ2Prime(int n){
    do{
      if(Util.isJ2Prime(n)){
        return n;
      }
    } while(n-- > 2);
    return 0;
  }

  private static <T> void interleave(T[] array, int from, int to) {
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
    int minSize = Math.min(a.size(), b.size());
    if(minSize > 0) {
      if (shuffle.folding) {
        // rotate non-interleaved items to the back
        Collections.rotate(b, minSize - b.size());
        // reverse the rest
        Collections.reverse(b.subList(0, minSize));
      }
      if (shuffle.out) {
        if (minSize > 1) {
          interleave(a.subList(1, minSize), b.subList(0, minSize - 1));
        }
      } else {
        interleave(a.subList(0, minSize), b.subList(0, minSize));
      }
    }
  }

  private static <T> void interleave(List<T> a, List<T> b) {
    int aSize = a.size(), bSize = b.size();
    assert aSize != 0 : "List A can not be empty.";
    if (aSize + bSize == 2) {
      a.set(0, b.set(0, a.get(0)));
      return;
    }

    int minSize = Math.min(aSize, bSize);

    int k = findNextLowestJ2Prime(minSize);

    // swap upto k elements in list a
    for (int i = 0; i < k; i++) {
      a.set(i, b.set(Util.a025480(i), a.get(i)));
    }

    // J2 prime cycle to interleave scramble first half of b and rest of b
    int trailerIdx = 0;
    T initialVal = b.get(trailerIdx);
    for (int i = 0; i < k - 1; i++) {
      int nextIdx = Util.a025480(k + trailerIdx);
      b.set(trailerIdx, b.get( nextIdx));
      trailerIdx = nextIdx;
    }
    b.set( trailerIdx, initialVal);

    if (minSize > k) {
      Util.rotate(a.subList(k, aSize), b, k - aSize);

      int nextStart = 2 * k + 1 - minSize;
      if (bSize - nextStart > 1) {
        interleave(b.subList(nextStart, bSize));
      }
    }

  }

  @Override
  public <T> void interleave(T[] a, int fromA, int toA,
                             T[] b, int fromB, int toB,
                             Shuffle shuffle) {
    int minSize = Math.min(toA - fromA, toB - fromB);
    if (minSize > 0) {
      if (shuffle.folding) {
        // rotate non-interleaved items to the back
        Util.rotate(b, fromB, toB, minSize - toB);
        // reverse the rest
        Util.reverse(b, fromB, minSize);
      }
      if (shuffle.out) { // out-shuffle
        if (minSize > 1) {
          interleave(a, fromA + 1, fromA + minSize,
                     b, fromB, fromB + minSize - 1);
        }
      } else {
        interleave(a, fromA, fromA + minSize, b, fromB, fromB + minSize);
      }
    }


  }

  private static <T> void interleave(T[] a, int fromA, int toA,
                                     T[] b, int fromB, int toB) {
    int aSize = toA - fromA, bSize = toB - fromB;
    assert aSize != 0 : "List A can not be empty.";

    if (aSize + bSize == 2) {
      Util.swap(a, fromA, b, fromB);
      return;
    }
    int minSize = Math.min(aSize, bSize);
    int k = findNextLowestJ2Prime(minSize);

    for (int i = 0; i < k; i++) {
      Util.swap(a, fromA + i, b, fromB + Util.a025480(i));
    }

    int trailerIdx = 0;
    T initialVal = b[trailerIdx];
    for (int i = 0; i < k - 1; i++) {
      int next = Util.a025480(k + trailerIdx);
      b[trailerIdx] = b[next];
      trailerIdx = next;
    }
    b[trailerIdx] = initialVal;

    if(minSize > k){
      Util.rotate(a, fromA+k, toA,
                  b, fromB, toB, k - aSize);
      int nextStart = 2 * k + 1 - minSize;
      if(bSize - nextStart > 1){
        interleave(b, fromB + nextStart, toB);
      }
    }
  }
}
