package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.Interleavers;
import net.jnellis.interleave.Shuffle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.Arrays;

/**
 * Billion elements, what happened to 'micro' benchmarks?
 */
@State(Scope.Benchmark)
public class InterleaverLargeNBench {

  final Object[] arr = new Object[1_000_000_000];
  final int max = 1_000_000_000;
  Integer[] ints = {1,2,3,4};

  @Setup(Level.Trial)
  public void setup(){
    Arrays.setAll(arr, i -> ints[i%ints.length] );
    System.out.println("<setup complete!>");
  }

  @Benchmark
  public Object[] LargeMarge(){
    Interleavers.SHUFFLE.interleave(arr, Shuffle.IN);
    return arr;
  }
}
