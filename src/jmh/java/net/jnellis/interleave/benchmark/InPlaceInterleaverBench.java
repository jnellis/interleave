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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * User: Joe Nellis Date: 5/2/2022 Time: 1:10 PM
 */
@State(Scope.Benchmark)
public class InPlaceInterleaverBench {

  @Param({"10", "100", "1000", "10000", "100000", "1000000", "10000000"})
  public int max;

  List<Object> list;
  Object[] arr;

  @Setup(Level.Trial)
  public void setup() {
    list = IntStream.range(0, max)
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
    arr = IntStream.range(0, max).boxed().toArray();
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

  //// PermutationInterleaver benchmarks
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

  // two list benches
  @Benchmark
  public List<Object> a025480TwoListInShuffle() {
    Interleavers.SEQUENCE.interleave(
        list.subList(0, max / 2),
        list.subList(max / 2, list.size()),
        Shuffle.IN);
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
  public Object[] permutationOneArrayInShuffle(){
    Interleavers.PERMUTATION.interleave(arr, Shuffle.IN);
    return arr;
  }


}
