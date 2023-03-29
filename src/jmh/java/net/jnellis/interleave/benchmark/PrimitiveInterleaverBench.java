package net.jnellis.interleave.benchmark;

import net.jnellis.interleave.PrimitiveArrayInShuffleInterleavers;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.lang.reflect.Array;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * User: Joe Nellis
 * Date: 3/17/2023
 * Time: 3:15 PM
 */
@State(Scope.Benchmark)
public class PrimitiveInterleaverBench {
  @Param({"baseline", "simple", "sequence", "permutation", "recursive", "josephus", "shuffle" })
  public String interleaverName;

  @Param({"10", "100", "1000", "10000", "100000", "1000000", "10000000"})
  public int max;

  @Param({"ints","longs","doubles","floats","chars","bytes"})
  public String arrayType;

  enum ARRAY_TYPE { ints(int.class), longs(long.class), doubles(double.class),
    floats(float.class), chars(char.class), bytes(byte.class);

    ARRAY_TYPE(Class<?> clazz){
      Class<?> componentType = clazz;
    }
  }
  @FunctionalInterface
  interface PrimitiveArrayOperation{
    void on(Object array, int from, int to);
  }

  enum INTERLEAVERS {
    sequence(PrimitiveArrayInShuffleInterleavers::sequence),
    permutation(PrimitiveArrayInShuffleInterleavers::permutation),
    recursive(PrimitiveArrayInShuffleInterleavers::recursive),
    josephus(PrimitiveArrayInShuffleInterleavers::josephus),
    shuffle(PrimitiveArrayInShuffleInterleavers::shufflePrime),
    simple(PrimitiveArrayInShuffleInterleavers::simple),
    baseline( // just acquire and copy back
        (Object array, int from, int to)->{
          var type = array.getClass().componentType();
          assert type != null : "Parameter array must be an array object.";
          int size = ((to - from) / 2)<<1;
          var temp = Array.newInstance(type, size);

          System.arraycopy(array, from, temp, 0, size);
        }
    );
    public final PrimitiveArrayOperation interleaver;

    INTERLEAVERS(PrimitiveArrayOperation i) {
      this.interleaver = i;
    }
  }

  PrimitiveArrayOperation interleaveOp;
  Object array;

  @Setup(Level.Iteration)
  public void setup() {
    // kill previous array/list creations specifically now, otherwise gc will
    // eventually get triggered during a benchmark run.
    System.gc();
    // choose interleaver
    interleaveOp = INTERLEAVERS.valueOf(interleaverName).interleaver;

    // choose what gets filled into the collection
    array = (switch (ARRAY_TYPE.valueOf(arrayType)) {
      case ints -> IntStream.range(0, max).toArray();
      case longs -> LongStream.range(0,max).toArray();
      case doubles -> DoubleStream.iterate(0.0, i -> i + 1).limit(max).toArray();
      case floats -> {
        float[] f = new float[max];
        for (int i = 0; i < f.length; i++) { f[i] = (float)i; }
        yield f;
      }
      case chars ->{
        char[] ch = new char[max];
        for (int i = 0; i < ch.length; i++) { ch[i] = (char)i; }
        yield ch;
      }
      case bytes ->{
        byte[] b = new byte[max];
        for (int i = 0; i < b.length; i++) { b[i] = (byte)i;}
        yield b;
      }
    });
  }

  @Benchmark
  public Object OnePrimitiveArrayInShuffle() {
    this.interleaveOp.on(array, 0, max);
    return array;
  }
}
