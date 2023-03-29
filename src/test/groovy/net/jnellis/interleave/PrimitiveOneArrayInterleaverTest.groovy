package net.jnellis.interleave

import spock.lang.Shared
import spock.lang.Unroll

import java.util.stream.IntStream

/**
 * User: Joe Nellis
 * Date: 3/14/2023 
 * Time: 4:08 AM
 *
 */
class PrimitiveOneArrayInterleaverTest extends InterleaversBase {

  @Shared
  def interleavers = [
      "simple" : PrimitiveArrayInShuffleInterleavers::simple,
      "sequence": PrimitiveArrayInShuffleInterleavers::sequence,
      "permutation": PrimitiveArrayInShuffleInterleavers::permutation,
      "recursive": PrimitiveArrayInShuffleInterleavers::recursive,
      "josephus": PrimitiveArrayInShuffleInterleavers::josephus,
      "shuffle": PrimitiveArrayInShuffleInterleavers::shufflePrime
  ]

  def paramCombinations() {
    [maxes, interleavers.keySet()].combinations()*.flatten()
  }


  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "primitive one array in-shuffle"() {
    given:
    int[] odds = IntStream.rangeClosed(1,max).filter{int i -> (i & 1) === 1}.toArray()
    int[] evens = IntStream.rangeClosed(2,max).filter {int i -> (i & 1) === 0}.toArray()
    int[] arr = new int[odds.length+evens.length]
    System.arraycopy(evens, 0, arr, 0, evens.length)
    System.arraycopy(odds, 0, arr, evens.length, odds.length)

    if (max < 10000) println arr
    interleavers[algo](arr, 0, arr.length)
    if (max < 10000) println arr
    expect:
    verifySequential(arr)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

 
}