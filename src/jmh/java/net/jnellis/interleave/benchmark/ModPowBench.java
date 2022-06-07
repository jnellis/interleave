package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.Util;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * User: Joe Nellis Date: 5/29/2022 Time: 1:57 PM
 */
@State(Scope.Benchmark)
public class ModPowBench {

  @Param({"55","434","600","900"})
  public int exp;

  @Param({"2","6","9","19"})
  public int k;

  public int mod;
  
  @Setup
  public void setup() {
    mod = (int) Math.pow(3, k);
  }

  @Benchmark
  public long bigIntegerModPow(){
    return Util.bigIntModPow(2, exp, mod);
  }

  @Benchmark
  public long fastModPow(){
    return Util.fastmod(2, exp, mod);
  }


}
