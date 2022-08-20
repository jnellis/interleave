package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.InPlaceInterleaver;
import net.jnellis.interleave.PermutationInterleaver;
import net.jnellis.interleave.RecursiveInterleaver;
import net.jnellis.interleave.RotatingQueueInterleaver;
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
    InPlaceInterleaver.interleave(arr, false, false);
    return arr;
  }

  @Benchmark
  public List<Object> a025480OneListInShuffle() {
    InPlaceInterleaver.interleave(list, false, false);
    return list;
  }

  @Benchmark
  public List<Object> a025480OneListOutShuffle() {
    InPlaceInterleaver.interleave(list, true, false);
    return list;
  }

  @Benchmark
  public List<Object> a025480OneListFoldingOutShuffle() {
    InPlaceInterleaver.interleave(list, false, true);
    return list;
  }

  @Benchmark
  public List<Object> a025480OneListFoldingInShuffle() {
    InPlaceInterleaver.interleave(list, true, true);
    return list;
  }

  //// PermutationInterleaver benchmarks
  @Benchmark
  public List<Object> permutationOneListOutShuffle() {
    PermutationInterleaver.interleave(list, false, false);
    return list;
  }

  @Benchmark
  public List<Object> permutationOneListInShuffle() {
    PermutationInterleaver.interleave(list, true, false);
    return list;
  }

  @Benchmark
  public List<Object> permutationOneListFoldingOutShuffle() {
    PermutationInterleaver.interleave(list, false, true);
    return list;
  }

  @Benchmark
  public List<Object> permutationOneListFoldingInShuffle() {
    PermutationInterleaver.interleave(list, true, true);
    return list;
  }

  //// RecursiveInterleaver benchmarks
  @Benchmark
  public List<Object> recursiveOneListOutShuffle() {
    RecursiveInterleaver.interleave(list, false, false);
    return list;
  }

  // two list benches
  @Benchmark
  public List<Object> a025480TwoListInShuffle() {
    InPlaceInterleaver.interleave(list.subList(0, max / 2),
                                  list.subList(max / 2, list.size()),
                                  true,
                                  false);
    return list;
  }
  @Benchmark
  public List<Object> permutationTwoListInShuffle() {
    PermutationInterleaver.interleave(list.subList(0, max / 2),
                                  list.subList(max / 2, list.size()),
                                  true,
                                  false);
    return list;
  }
  @Benchmark
  public List<Object> recursiveTwoListInShuffle() {
    RecursiveInterleaver.interleave(list.subList(0, max / 2),
                                  list.subList(max / 2, list.size()),
                                  true,
                                  false);
    return list;
  }

}
