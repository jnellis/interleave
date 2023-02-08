package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.Util;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
 
@State(Scope.Benchmark)
public class FastmodBench {

  public static final int divisor = 91193;  // a prime
  public static final int pd = Math.abs(divisor);
  public static final long u64c = Long.divideUnsigned(-1L, pd) + 1L +
      ((pd & (pd - 1L)) == 0 ? 1L : 0);
  final int max = 100000;
  int[] val;

  int prod = 1;
  int checkResult = 1;

  @Setup(Level.Trial)
  public void setup() {
    checkResult = prod;
    for (int i = 0; i < max ; i++) {
      checkResult = (checkResult << 8) % divisor;
    }
    System.out.println("checkResult = " + this.checkResult);
  }

  @TearDown(Level.Trial)
  public void checkSum() {
    if (prod != checkResult) {
      System.out.println("prod = " + prod);
      throw new IllegalStateException("Product is not the same for all tests.");
    }
  }

  @Benchmark
  @OperationsPerInvocation(max)
  public int fastmodLemireReduction() {
    prod = 1;
    for (int i = 0; i < max; i++) {
      prod = Util.fastmod(prod << 8, u64c, pd);
    }
    return prod;
  }

  @Benchmark
  @OperationsPerInvocation(max)
  public int slowMod() {
    prod = 1;
    for (int i = 0; i < max; i++) {
      prod = (prod << 8) % divisor;
    }
    return prod;
  }
}
