package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.Util;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
public class CyclicShiftBench {

  public int arrLen = 100000;

  public int shiftAmt = -arrLen/2;

  List<Object> list;
  Object[] array;

  @Setup
  public void setup() {
    list = IntStream.range(0, arrLen)
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
    array = list.toArray();
  }

  @Benchmark
  public Object[] utilRotateArray() {
    Util.rotate(array, shiftAmt);
    return array;
  }

  @Benchmark
  public List<Object> collectionsRotate() {
    Collections.rotate(list, shiftAmt);
    return list;
  }
}
