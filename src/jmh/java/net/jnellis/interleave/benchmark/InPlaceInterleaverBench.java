package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.Interleavers;
import net.jnellis.interleave.Shuffle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * User: Joe Nellis Date: 5/2/2022 Time: 1:10 PM
 */
@State(Scope.Benchmark)
public class InPlaceInterleaverBench {

//  @Param({"10", "100", "1000", "10000", "100000", "1000000", "10000000"})
    @Param({"64", "256", "1024", "4096", "16384", "65536", "262144", "1048576", })
//        "16777216", "268435456"})
  static public int max;

  List<Object> list;
  Object[] arr;

  @Setup(Level.Trial)
  public void setup() {
    // We don't do anything with these numbers and so there should be no need
    // to make an effort to dereference the Integer objects they are pointing
    // to. But...in an effort to not have the runtime just see the swapping of
    // nulls in an empty (but allocated) object array, fill this with some
    // cached Integers I suppose.
    final Object[] ints = IntStream.range(0,128).boxed().toArray();

    // Previously we did this, which kinda drags on trials when N is large
    // due to filling memory, and copying to create a list for list specific
    // trials. The results are seemingly the same.
    //    list = IntStream.range(0, max)
    //                    .boxed()
    //                    .collect(Collectors.toCollection(ArrayList::new));
    //    arr = IntStream.range(0, max).boxed().toArray();
    arr = new Object[max];
    Arrays.setAll(arr, i -> ints[i%ints.length]);

    list = new ArrayList<>(Arrays.asList(arr));
    System.out.println("<setup complete!>");
  }

  @Benchmark
  public Object[] a025480OneArrayInShuffle() {
    Interleavers.SEQUENCE.interleave(arr, Shuffle.IN);
    return arr;
  }

  @Benchmark
  public List<Object> a025480OneListInShuffle() {
    Interleavers.SEQUENCE.interleave(list, Shuffle.IN);
    return list;
  }

  @Benchmark
  public List<Object> a025480OneListOutShuffle() {
    Interleavers.SEQUENCE.interleave(list, Shuffle.OUT);
    return list;
  }

  @Benchmark
  public List<Object> a025480OneListFoldingOutShuffle() {
    Interleavers.SEQUENCE.interleave(list, Shuffle.OUT_FOLDING);
    return list;
  }

  @Benchmark
  public List<Object> a025480OneListFoldingInShuffle() {
    Interleavers.SEQUENCE.interleave(list, Shuffle.IN_FOLDING);
    return list;
  }

  @Benchmark
  public List<Object> a025480TwoListInShuffle() {
    Interleavers.SEQUENCE.interleave(
        list.subList(0, max / 2),
        list.subList(max / 2, list.size()),
        Shuffle.IN);
    return list;
  }

  @Benchmark
  public Object[] a025480TwoArrayInShuffle() {
    Interleavers.SEQUENCE.interleave(
        arr, 0, max / 2,
        arr, max / 2, max,
        Shuffle.IN);
    return arr;
  }

  //// PermutationInterleaver benchmarks
  @Benchmark
  public Object[] permutationOneArrayInShuffle(){
    Interleavers.PERMUTATION.interleave(arr, Shuffle.IN);
    return arr;
  }

  @Benchmark
  public List<Object> permutationOneListOutShuffle() {
    Interleavers.PERMUTATION.interleave(list, Shuffle.OUT);
    return list;
  }

  @Benchmark
  public List<Object> permutationOneListInShuffle() {
    Interleavers.PERMUTATION.interleave(list, Shuffle.IN);
    return list;
  }

  @Benchmark
  public List<Object> permutationOneListFoldingOutShuffle() {
    Interleavers.PERMUTATION.interleave(list, Shuffle.OUT_FOLDING);
    return list;
  }

  @Benchmark
  public List<Object> permutationOneListFoldingInShuffle() {
    Interleavers.PERMUTATION.interleave(list, Shuffle.IN_FOLDING);
    return list;
  }

  @Benchmark
  public List<Object> permutationTwoListInShuffle() {
    Interleavers.PERMUTATION.interleave(
        list.subList(0, max / 2),
        list.subList(max / 2, list.size()),
        Shuffle.IN);
    return list;
  }

  @Benchmark
  public Object[] permutationTwoArrayInShuffle() {
    Interleavers.PERMUTATION.interleave(
        arr, 0, max / 2,
        arr, max / 2, max,
        Shuffle.IN);
    return arr;
  }

  //// RecursiveInterleaver benchmarks
  @Benchmark
  public List<Object> recursiveOneListOutShuffle() {
    Interleavers.RECURSIVE.interleave(list, Shuffle.OUT);
    return list;
  }

  @Benchmark
  public List<Object> recursiveOneListInShuffle() {
    Interleavers.RECURSIVE.interleave(list, Shuffle.IN);
    return list;
  }

  @Benchmark
  public List<Object> recursiveTwoListInShuffle() {
    Interleavers.RECURSIVE.interleave(
        list.subList(0, max / 2),
        list.subList(max / 2, list.size()),
        Shuffle.IN);
    return list;
  }

  @Benchmark
  public Object[] recursiveOneArrayInShuffle(){
    Interleavers.RECURSIVE.interleave(arr, Shuffle.IN);
    return arr;
  }

  @Benchmark
  public Object[] recursiveTwoArrayInShuffle() {
    Interleavers.RECURSIVE.interleave(
        arr, 0, max / 2,
        arr, max / 2, max,
        Shuffle.IN);
    return arr;
  }

  @Benchmark
  public Object[] josephusOneArrayInShuffle(){
    Interleavers.JOSEPHUS.interleave(arr, Shuffle.IN);
    return arr;
  }

  @Benchmark
  public List<Object> josephusOneListInShuffle(){
    Interleavers.JOSEPHUS.interleave(list, Shuffle.IN);
    return list;
  }

  @Benchmark
  public List<Object> josephusTwoListInShuffle() {
    Interleavers.JOSEPHUS.interleave(
        list.subList(0, max / 2),
        list.subList(max / 2, list.size()),
        Shuffle.IN);
    return list;
  }

  @Benchmark
  public Object[] josephusTwoArrayInShuffle() {
    Interleavers.JOSEPHUS.interleave(
        arr, 0, max / 2,
        arr, max / 2, max,
        Shuffle.IN);
    return arr;
  }

  @Benchmark
  public Object[] shufflePrimeOneArrayInShuffle(){
    Interleavers.SHUFFLE.interleave(arr, Shuffle.IN);
    return arr;
  }
}
