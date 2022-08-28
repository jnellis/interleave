package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.Interleavers;
import net.jnellis.interleave.RotatingQueueInterleaver;
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

  @Setup(Level.Iteration)
  public void setup() {
    list = IntStream.range(0, max)
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
    arr = IntStream.range(0, max).boxed().toArray();
  }

  @Benchmark
  public List<Object> rotatingQueueTwoListInShuffle() {
    return RotatingQueueInterleaver.interleave(
        list.subList(0, max / 2),
        list.subList(max / 2, list.size()),
        true);
  }

  @Benchmark
  public Object[] aO25480OneArrayInShuffle() {
    Interleavers.a025480.interleave(arr, Shuffle.IN);
    return arr;
  }

  @Benchmark
  public List<Object> a025480OneListInShuffle() {
    Interleavers.a025480.interleave(list, Shuffle.IN);
    return list;
  }

  @Benchmark
  public List<Object> a025480OneListOutShuffle() {
    Interleavers.a025480.interleave(list, Shuffle.OUT);
    return list;
  }

  @Benchmark
  public List<Object> a025480OneListFoldingOutShuffle() {
    Interleavers.a025480.interleave(list, Shuffle.OUT_FOLDING);
    return list;
  }

  @Benchmark
  public List<Object> a025480OneListFoldingInShuffle() {
    Interleavers.a025480.interleave(list, Shuffle.IN_FOLDING);
    return list;
  }

  //// PermutationInterleaver benchmarks
  @Benchmark
  public List<Object> permutationOneListOutShuffle() {
    Interleavers.permutation.interleave(list, Shuffle.OUT);
    return list;
  }

  @Benchmark
  public List<Object> permutationOneListInShuffle() {
    Interleavers.permutation.interleave(list, Shuffle.IN);
    return list;
  }

  @Benchmark
  public List<Object> permutationOneListFoldingOutShuffle() {
    Interleavers.permutation.interleave(list, Shuffle.OUT_FOLDING);
    return list;
  }

  @Benchmark
  public List<Object> permutationOneListFoldingInShuffle() {
    Interleavers.permutation.interleave(list, Shuffle.IN_FOLDING);
    return list;
  }

  //// RecursiveInterleaver benchmarks
  @Benchmark
  public List<Object> recursiveOneListOutShuffle() {
    Interleavers.recursive.interleave(list, Shuffle.OUT);
    return list;
  }

  // two list benches
  @Benchmark
  public List<Object> a025480TwoListInShuffle() {
    Interleavers.a025480.interleave(
        list.subList(0, max / 2),
        list.subList(max / 2, list.size()),
        Shuffle.IN);
    return list;
  }

  @Benchmark
  public List<Object> permutationTwoListInShuffle() {
    Interleavers.permutation.interleave(
        list.subList(0, max / 2),
        list.subList(max / 2, list.size()),
        Shuffle.IN);
    return list;
  }

  @Benchmark
  public List<Object> recursiveTwoListInShuffle() {
    Interleavers.recursive.interleave(
        list.subList(0, max / 2),
        list.subList(max / 2, list.size()),
        Shuffle.IN);
    return list;
  }

}
