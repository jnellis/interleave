package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.Util;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * User: Joe Nellis Date: 5/29/2022 Time: 12:16 PM
 */
@State(Scope.Benchmark)
public class CyclicShiftBench {

  @Param({"10","1000","100000","10000000"})
  public int arrLen;

  @Param({"7","3","2","1.5"})
  public double shiftAmt;

  List<Object> list;
  int by;
  @Setup
  public void setup(){
    list = IntStream.range(0, arrLen)
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
    by = (int)(arrLen/shiftAmt);
  }

  @Benchmark
  public List<Object> tripleReverseMethod(){
    Util.rotateViaTripleReverse(list, by,false);
    return list;
  }

  @Benchmark
  public List<Object> rightCycleLeaderMethod(){
    Util.rotateRight(list, by);
    return list;
  }

  @Benchmark
  public List<Object> leftCycleLeaderMethod(){
    Util.rotateLeft(list, by);
    return list;
  }

  @Benchmark
  public List<Object> collectionsRotate(){
    Collections.rotate(list,by);
    return list;
  }
}
