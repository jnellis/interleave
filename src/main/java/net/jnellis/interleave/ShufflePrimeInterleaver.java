package net.jnellis.interleave;

import java.util.Collections;
import java.util.List;

/**
 * User: Joe Nellis
 * Date: 11/18/2022
 * Time: 12:44 PM
 */
public
class ShufflePrimeInterleaver implements Interleaver{

  @Override
  public <T> void interleave(List<T> list, Shuffle shuffle) {
    if(list.size() > 1){
      if(shuffle.out){
        list = list.subList(1,list.size());
      }
      if(shuffle.folding){
        Collections.reverse(list.subList(list.size()/2, list.size()));
      }
      interleave(list);
    }

  }

  // single list in-shuffle
  private <T> void interleave(List<T> list){
    int size = list.size();
    if(size<2)return;
    if(size<4){
      Collections.swap(list,0,1);
      return;
    }
    int midpt = size / 2;

    int k = findNextLowestShufflePrime(midpt);

    if (k != size) {
      // rotate difference out of the way
      Collections.rotate(list.subList(k/2,size),k/2 - size/2);
      interleave(list.subList(k+1,size));
    }

    int idx = 0;
    int mod = k + 1;
    final long u64_c = Long.divideUnsigned(-1L,mod)+1;
    T leader = list.get(idx);
    for (int i = 0; i < k; i++) {
//      idx = (2*idx+1) % mod;
      idx = Util.fastmod(2*idx+1,u64_c, mod);
      leader = list.set(idx, leader);
    }

  }

  @Override
  public <T> void interleave(T[] array, int from, int to, Shuffle shuffle) {
    int size = to - from;
    if (size > 1) {
      if (shuffle.out) { // out-shuffle
        if (size == 2) {
          return;
        } // too small, no change
        from++;
        size--;
      }
      if (shuffle.folding) {
        Util.reverse(array, from + (size / 2), to);
      }
      interleave(array, from, to);
    }
  }

  private int findNextLowestShufflePrime(int n){
    assert n >= 2: "lowest J2 prime is 2, n can't be less than 2.";
    while(!Util.isJ2Prime(n--)){ /* ugh, linter, shutup */};
    return (n+1)<<1;
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
//    // S-primes are J2-primes times two
//    int k = ((pos < 0) ? Util.j2primes[-(pos+2)] : Util.j2primes[pos])*2;
    int k = findNextLowestShufflePrime(midpt);

    if (k != size) {
      // rotate difference out of the way
      Util.rotate(array, from + k/2, to, k/2 - size/2);
      interleave(array, from + k + 1, to);
    }

    int idx = 0;
    int mod = k + 1;
    final long u64_c = Long.divideUnsigned(-1L,mod)+1;
    T leader = array[from + idx];
    for (int i = 0; i < k; i++) {
//      idx = (2*idx+1) % mod;
      idx = Util.fastmod(2*idx+1,u64_c, mod);
      leader = set(array, from + idx, leader);
    }
  }

  /**
   * Similar to List.set. Sets the value at the specified index and returns
   * the old value that was at that index.
   *
   * @param array array to access
   * @param index index into array
   * @param value value to replace old value
   * @param <T>   type of array element
   * @return old value at that index
   */
  private static <T> T set(T[] array, int index, T value) {
    T oldValue = array[index];
    array[index] = value;
    return oldValue;
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
