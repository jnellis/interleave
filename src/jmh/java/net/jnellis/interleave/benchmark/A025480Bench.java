package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.Util;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Benchmark that computes different implementations of the sequence A025480.
 * <p>
 * Because this method executes in the nanosecond range, I think it makes more
 * sense to use @OperationsPerInvocation and loop inside each bench method.
 * <p>
 * A couple of gotchas if you are thinking about altering this benchmark:
 * <p>
 * 1. The {@link #trailingZerosMethod} is an intrinsic operation and thus is
 * machine dependent, meaning that on cpu's that don't
 * support <a href="https://en.wikipedia.org/wiki/X86_Bit_manipulation_instruction_set">
 * BMI1</a> the TZCNT instruction will fall back to using BSF (bit scan
 * forward) and if that isn't available then the bit manipulation code in the
 * method itself (see the source code for
 * {@link Integer#numberOfTrailingZeros(int)}.)  On x86-64 machines after 2014
 * it is most likely going to use the hardware instruction TZCNT. You can verify
 * what is used by profiling with {@code -prof perfasm} and
 * {@code -jvmArgs -XX:+PrintAssembly}.
 * <p>
 * 2. The compareAndShiftMethod runs well if it hits an even number, thus
 * skipping its loop and doing a single one bit shift. Providing an even mix of
 * numbers is thus important to accurate measurement.  Unlike the trailingZero
 * method which has a fixed overhead but is branchless, it's possible to tune
 * compareAndShift with select input. Counting numbers seems
 * like the best input. If you use the result of the function plus a constant
 * and re-input that into the method then you get a function dependent on the
 * constant. This is how
 * it is used as a cycle leader in the Josephus algorithm but for the Sequence
 * algorithm its more akin to counting numbers as input. Here's an example of
 * the benchmark tried. Where k is some constant or @Param.
 * <p>
 * <pre>
 * {@code   @Benchmark
 *   @OperationsPerInvocation(MAX)
 *   public void compareAndShiftMethod(Blackhole blackhole) {
 *     int fz = k;
 *     for (int i = 0; i < MAX; i++){
 *       while ((fz & 1) != 0)
 *         fz >>= 1;
 *       fz = (fz >> 1) + k; // or change k to i
 *     }
 *     blackhole.consume(fz);
 *   }
 * }</pre>
 *
 * <p>
 * 3. The {@link #logMethod} is more of a 'me too' baseline which involves
 * a call to {@link Integer#numberOfLeadingZeros(int)} which has similar
 * intrinsic implications. There is a branch for zero check, and a few more cycles
 * of instruction overhead than the {@link #trailingZerosMethod}.
 * </p>
 *
 * @see <a href="https://oeis.org/A025480">A025480 @OEIS.org</a>
 */
@State(Scope.Benchmark)
public class A025480Bench {

//  @Param({"10", "1000", "100000", "10000000"})
  public static final int MAX = 10_000;

  @Benchmark
  @Measurement
  @OperationsPerInvocation(MAX)
  public void trailingZerosMethod(Blackhole blackhole) {
    for (int fz = 0; fz < MAX; fz++) {
      blackhole.consume(fz >> (Integer.numberOfTrailingZeros(~fz) + 1));
    }
  }

  @Benchmark
  @OperationsPerInvocation(MAX)
  public void trailingZerosMethodOpt(Blackhole blackhole) {
    for (int fz = 0; fz < MAX; fz++) {
      blackhole.consume(
          // "half of all numbers are even" optimization?
          (fz & 1) == 0 ? fz >> 1
                        : fz >> (Integer.numberOfTrailingZeros(~fz) + 1)
      );
    }
  }

  @Benchmark
  @OperationsPerInvocation(MAX)
  public void logMethod(Blackhole blackhole) {
    for (int fz = 0; fz < MAX; fz++) {
      blackhole.consume(fz >> (Util.ilog2(~fz & (fz + 1)) + 1));
    }
  }

  @Benchmark
  @OperationsPerInvocation(MAX)
  public void compareAndShiftMethod(Blackhole blackhole) {
    for (int i = 0; i < MAX; i++){
      int fz = i;
      while ((fz & 1) != 0)
        fz >>= 1;
      blackhole.consume(fz >> 1);
    }
  }
}
