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
    collection = new ArrayList(data[1])
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
    collection = new ArrayList(data[1])
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
    collection = new ArrayList(data[1])
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
    collection = new ArrayList(data[1])
    expected = data[2]
    variant = data[0]
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