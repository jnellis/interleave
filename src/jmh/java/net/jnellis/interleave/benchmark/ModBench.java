package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.Util;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * User: Joe Nellis
 * Date: 10/2/2022
 * Time: 9:41 PM
 */
@State(Scope.Benchmark)
public class ModBench {
  @Param({"10", "1000", "100000", "10000000"})
  public int MAX;

  @Benchmark
  public void compareFirst() {
    int k = Util.ilog3(MAX);
    int mod = Util.powersOf3(k);
    int startIdx = 1;
    for (int i = 0; i < k; i++) {
      int idx = startIdx;
      do {
        idx <<= 1;
        if (idx >= mod)
          idx %= mod;
        sink(idx);
      } while (idx != startIdx);
      startIdx *= 3;
    }
  }

  @Benchmark
  public void control() {
    int k = Util.ilog3(MAX);
    int mod = Util.powersOf3(k);
    int startIdx = 1;
    for (int i = 0; i < k; i++) {
      int idx = startIdx;
      do {
        idx = (idx * 2) % mod;
        sink(idx);
      } while (idx != startIdx);
      startIdx *= 3;
    }
  }

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public static void sink(int val) {}
}
