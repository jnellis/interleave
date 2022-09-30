package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.Util;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * User: Joe Nellis
 * Date: 9/18/2022
 * Time: 4:53 PM
 */
@State(Scope.Benchmark)
public class A025480Bench {

  @Param({"10", "1000", "100000", "10000000"})
  public int MAX;

  @Benchmark
  public void trailingZeroesMethod() {
    for (int fz = 0; fz < MAX; fz++) {
      sink(fz >> (Integer.numberOfTrailingZeros(~fz) + 1));
    }
  }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public static void sink(int val) {}

  @Benchmark
  public void logMethod() {
    for (int fz = 0; fz < MAX; fz++) {
      sink(fz >> (Util.ilog2(~fz & (fz + 1)) + 1));
    }
  }

  @Benchmark
  public void countMethod() {
    for (int i = 0; i < MAX; i++) {
      int fz = i;
      while ((fz & 1) != 0)
        fz >>= 1;
      sink(fz >> 1);
    }
  }
}
