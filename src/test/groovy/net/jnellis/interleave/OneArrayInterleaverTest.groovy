package net.jnellis.interleave

import spock.lang.Shared
import spock.lang.Unroll

class OneArrayInterleaverTest extends InterleaversBase {


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

  @Unroll('#featureName[#iterationIndex] #algo #variant')
  def "one list out-shuffle, unexpected behavior example"() {
    println data
    interleavers[algo](collection, false, false)
    expect:
    collection == expected
    println data
    where:
    [data, algo] << [getTypes()["outShuffle"], interleavers.keySet()].combinations()
    collection = new ArrayList(data[1]).toArray()
    expected = data[2]
    variant = data[0]
  }

  @Unroll('#featureName[#iterationIndex] #algo #variant')
  def "one list in-shuffle, unexpected behavior example"() {
    println data
    interleavers[algo](collection, true, false)
    expect:
    collection == expected
    println data
    where:
    [data, algo] << [getTypes()["inShuffle"], interleavers.keySet()].combinations()
    collection = new ArrayList(data[1]).toArray()
    expected = data[2]
    variant = data[0]
  }

  @Unroll('#featureName[#iterationIndex] #algo #variant')
  def "one list folding out-shuffle, unexpected behavior example"() {
    println data
    interleavers[algo](collection, false, true)
    expect:
    collection == expected
    println data
    where:
    [data, algo] << [getTypes()["foldingOutShuffle"], interleavers.keySet()].combinations()
    collection = new ArrayList(data[1]).toArray()
    expected = data[2]
    variant = data[0]
  }

  @Unroll('#featureName[#iterationIndex] #algo #variant')
  def "one list folding in-shuffle, unexpected behavior example"() {
    println data
    interleavers[algo](collection, true, true)
    expect:
    collection == expected
    println data
    where:
    [data, algo] << [getTypes()["foldingInShuffle"], interleavers.keySet()].combinations()
    collection = new ArrayList(data[1]).toArray()
    expected = data[2]
    variant = data[0]
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One array out-shuffle"() {
    given:
    def arr = oddsThenEvens(max).toArray()
    expect:
    oneCollectionTest(interleavers[algo], max, arr, false, false)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One array in-shuffle"() {
    given:
    def arr = evensThenOdds(max).toArray()
    expect:
    oneCollectionTest(interleavers[algo], max, arr, true, false)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One array folding out-shuffle"() {
    given:
    def arr = oddsThenFoldedEvens(max).toArray()
    expect:
    oneCollectionTest(interleavers[algo], max, arr, false, true)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One array folding in-shuffle"() {
    given:
    def arr = evensThenFoldedOdds(max).toArray()
    expect:
    oneCollectionTest(interleavers[algo], max, arr, true, true)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }


}
