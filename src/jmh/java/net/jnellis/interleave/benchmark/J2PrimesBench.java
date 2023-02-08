package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.Util;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * User: Joe Nellis
 * Date: 11/19/2022
 * Time: 2:06 PM
 */
@State(Scope.Benchmark)
public class J2PrimesBench {

  public static final int max = 1000;

  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public static void sink(boolean val) {}


  @Benchmark
  @OperationsPerInvocation(max)
  public void a025480CyclicCheckBench(){
    for (int i = 2; i < max; i++) {
      sink(Util.isJ2Prime(i));
    }
  }

  @Benchmark
  @OperationsPerInvocation(max)
  public void optimizedA025480CycleBench(){
    for (int i = 2; i < max; i++) {
      if(i % 4 == 1 || i % 4 ==2){
        sink(Util.isJ2Prime(i));
      }
    }
  }

}
