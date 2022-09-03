package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.Util;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * User: Joe Nellis
 * Date: 9/2/2022
 * Time: 6:43 PM
 */
@State(Scope.Benchmark)
public class LogBench {
  @Param({"10", "1000", "100000", "10000000"})
  int i;

  @Benchmark
  public void MathlogImpl(Blackhole blackhole) {
    int result = (int) (Math.log(i) / Math.log(3));
    blackhole.consume(result);
  }

  @Benchmark
  public void TablelogImpl(Blackhole blackhole) {
    int result = Util.ilog3(i);
    blackhole.consume(result);
  }
}
