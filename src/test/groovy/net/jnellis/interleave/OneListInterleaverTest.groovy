package net.jnellis.interleave

import spock.lang.Shared
import spock.lang.Unroll

class OneListInterleaverTest extends InterleaversBase {

  @Shared
  def interleavers = [
      "a025480"    : InPlaceInterleaver::interleave,
      "permutation": PermutationInterleaver::interleave,
      "recursive"  : RecursiveInterleaver::interleave
  ]

  @Shared
  int[] maxes = [2, 3, 4, 9, 10, 11, 31, 32, 33, 999, 1000, 1001, 999999, 1000000]
 
  def paramCombinations() {
    [maxes, interleavers.keySet()].combinations()*.flatten()
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One list out-shuffle"() {
    given:
    def list = oddsThenEvens(max)
    expect:
    oneCollectionTest(interleavers[algo], max, list, false, false)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One list in-shuffle"() {
    given:
    def list = evensThenOdds(max)
    expect:
    oneCollectionTest(interleavers[algo], max, list, true, false)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One list folding out-shuffle"() {
    given:
    def list = oddsThenFoldedEvens(max)
    expect:
    oneCollectionTest(interleavers[algo], max, list, false, true)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One list folding in-shuffle"() {
    given:
    def list = evensThenFoldedOdds(max)
    expect:
    oneCollectionTest(interleavers[algo], max, list, true, true)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

}