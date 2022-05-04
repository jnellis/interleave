package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.InPlaceInterleaver;
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

  @Param({"10", "1000", "100000"})
  public int max;

  List<Object> list;

  @Setup
  public void setup() {
    list = IntStream.range(0, max)
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));

//    list = Stream.generate(Object::new)
//                 .limit(max)
//                 .collect(Collectors.toCollection(ArrayList::new));
  }

  @Benchmark
  public List<Object> rotatingQueueInterleave() {
    return RotatingQueueInterleaver.interleave(
        list.subList(0, max / 2),
        list.subList(max / 2, list.size()),
        false);
  }

  @Benchmark
  public List<Object> interleaveBench() {
    InPlaceInterleaver.interleave(list, false, false);
    return list;
  }

  @Benchmark
  public List<Object> shuffleBench() {
    InPlaceInterleaver.interleave(list, true, false);
    return list;
  }

  @Benchmark
  public List<Object> foldingBench() {
    InPlaceInterleaver.interleave(list, false, true);
    return list;
  }

  @Benchmark
  public List<Object> foldingShuffleBench() {
    InPlaceInterleaver.interleave(list, true, true);
    return list;
  }

}
