package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.InPlaceInterleaver;
import net.jnellis.interleave.PermutationInterleaver;
import net.jnellis.interleave.RecursiveInterleaver;
import net.jnellis.interleave.RotatingQueueInterleaver;
import org.openjdk.jmh.annotations.Benchmark;
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

  @Setup
  public void setup() {
    list = IntStream.range(0, max)
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
    arr = IntStream.range(0,max).boxed().toArray();
  }

  @Benchmark
  public List<Object> rotatingQueueInterleaveBench() {
    return RotatingQueueInterleaver.interleave(
        list.subList(0, max / 2),
        list.subList(max / 2, list.size()),
        false);
  }

  @Benchmark
  public Object[] aO25480ArrayInterleaveBench(){
    InPlaceInterleaver.interleave(arr, false,false);
    return arr;
  }

  @Benchmark
  public List<Object> a025480ListInterleaveBench() {
    InPlaceInterleaver.interleave(list, false, false);
    return list;
  }

  @Benchmark
  public List<Object> a025480ListShuffleBench() {
    InPlaceInterleaver.interleave(list, true, false);
    return list;
  }

  @Benchmark
  public List<Object> a025480ListFoldingBench() {
    InPlaceInterleaver.interleave(list, false, true);
    return list;
  }

  @Benchmark
  public List<Object> a025480ListFoldingShuffleBench() {
    InPlaceInterleaver.interleave(list, true, true);
    return list;
  }

  //// PermutationInterleaver benchmarks
  @Benchmark
  public List<Object> permutationListInterleaveBench() {
    PermutationInterleaver.interleave(list, false, false);
    return list;
  }

  @Benchmark
  public List<Object> permutationListShuffleBench() {
    PermutationInterleaver.interleave(list, true, false);
    return list;
  }

  @Benchmark
  public List<Object> permutationListFoldingBench() {
    PermutationInterleaver.interleave(list, false, true);
    return list;
  }

  @Benchmark
  public List<Object> permutationListFoldingShuffleBench() {
    PermutationInterleaver.interleave(list, true, true);
    return list;
  }

  //// RecursiveInterleaver benchmarks
  @Benchmark
  public List<Object> recursiveListInterleaveBench() {
    RecursiveInterleaver.interleave(list, false, false);
    return list;
  }

}
