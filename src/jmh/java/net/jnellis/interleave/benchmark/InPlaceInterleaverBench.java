package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.Interleaver;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * User: Joe Nellis Date: 5/2/2022 Time: 1:10 PM
 */
@State(Scope.Benchmark)
public class InPlaceInterleaverBench {

  @Param({"baseline","simple","sequence","permutation","recursive","josephus","shuffle"})
  public String interleaverName;

  @Param({"10", "100", "1000", "10000", "100000", "1000000", "10000000"})
//    @Param({"64", "256", "1024", "4096", "16384", "65536", "262144", "1048576", })
//        "16777216", "268435456"}) // powers of 2
  public int max;

  @Param({"Nulls", "Single","Cached","Unique"})
  public String colType;

  enum COLL_TYPE{ Nulls, Single, Cached, Unique}

  enum INTERLEAVERS {
    sequence(Interleavers.SEQUENCE),
    permutation(Interleavers.PERMUTATION),
    recursive(Interleavers.RECURSIVE),
    josephus(Interleavers.JOSEPHUS),
    shuffle(Interleavers.SHUFFLE),
    simple(Interleavers.SIMPLE),
    baseline(new Interleaver() {  // does absolutely nothing
      @Override
      public void interleave(List<?> list, Shuffle shuffle) {}

      @Override
      public void interleave(Object[] array, int from, int to, Shuffle shuffle) {}

      @Override
      public <T> void interleave(List<T> a, List<T> b, Shuffle shuffle) {}

      @Override
      public <T> void interleave(T[] a, int fromA, int toA, T[] b, int fromB, int toB, Shuffle shuffle) {}
    });
    public final Interleaver interleaver;

    INTERLEAVERS(Interleaver i) {
      this.interleaver = i;
    }
  }

  Interleaver interleaver;
  List<Object> list;
  Object[] arr;

  /**
   * Previously I had array initialization set to only fill once per trial and
   * was letting all benchmark iterations use the same array. Performance stank
   * as warmup iterations were the fastest as I suspect locality had a part to
   * play. I changed @Setup to Level.Iteration and things got better but then
   * read <a href="https://github.com/openjdk/jmh/blob/master/jmh-samples/src/main/java/org/openjdk/jmh/samples/JMHSample_38_PerInvokeSetup.java">
   * the jmh sample on per invoke setups</a> and settled on copying the
   * array/list for each invocation. I think setting Level.Invocation would
   * suffer for the smaller bench sizes so there is also a baseline Interleaver
   * that does nothing except allow the array/list copy to happen, so you can
   * deduct or compare it from other interleaver results.
   * <p>
   * I also wondered if the type of contents had an effect so there is
   * available the option to try arrays that are all nulls, a single constant,
   * the cached Integers from 0 to 127, or unique integers. Only having
   * unique Integer object references had a slightly higher cost on performance,
   * the other options being all equal.  Compressed Oops perhaps is the reason.
   */
  @Setup(Level.Iteration)
  public void setup() {
    // kill previous array/list creations specifically now, otherwise gc will
    // eventually get triggered during a benchmark run.
    System.gc();
    // choose interleaver
    interleaver = INTERLEAVERS.valueOf(interleaverName).interleaver;

    // choose what gets filled into the collection
    IntStream stream = IntStream.range(0,max);
    list = (switch (COLL_TYPE.valueOf(colType)) {
      case Nulls -> stream.mapToObj(value -> null);
      case Single -> stream.mapToObj(value -> 42);
      case Cached -> stream.mapToObj(value -> value % 128);
      case Unique -> stream.boxed();
    }).collect(Collectors.toCollection(ArrayList::new));
    arr = list.toArray();
  } 

  @Benchmark
  public Object[] OneArrayInShuffle() {
    Object[] a = Arrays.copyOf(arr,arr.length);
    interleaver.interleave(a, Shuffle.IN);
    return a;
  }

  @Benchmark
  public List<Object> OneListInShuffle() {
    var l = new ArrayList<>(list);
    interleaver.interleave(l, Shuffle.IN);
    return l;
  }

  @Benchmark
  public Object[] TwoArrayInShuffle() {
    Object[] a = Arrays.copyOf(arr,arr.length);
    interleaver.interleave(
        a, 0, max / 2,
        a, max / 2, max,
        Shuffle.IN);
    return a;
  }

  @Benchmark
  public List<Object> TwoListInShuffle() {
    var l = new ArrayList<>(list);
    interleaver.interleave(
        l.subList(0, max / 2),
        l.subList(max / 2, l.size()),
        Shuffle.IN);
    return l;
  }
}
