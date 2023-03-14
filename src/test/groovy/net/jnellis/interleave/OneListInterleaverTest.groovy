package net.jnellis.interleave

import spock.lang.Shared
import spock.lang.Unroll

class OneListInterleaverTest extends InterleaversBase {

  @Shared
  def interleavers = [
      "a025480"    : Interleavers.SEQUENCE::interleave,
      "permutation": Interleavers.PERMUTATION::interleave,
      "recursive"  : Interleavers.RECURSIVE::interleave,
      "josephus"   : Interleavers.JOSEPHUS::interleave,
      "shuffle"    : Interleavers.SHUFFLE::interleave,
      "simple"   : Interleavers.SIMPLE::interleave
  ]

  def paramCombinations() {
    [maxes, interleavers.keySet()].combinations()*.flatten()
  }

  @Unroll('#featureName[#iterationIndex] #algo #variant')
  def "one list out-shuffle, unexpected behavior example"() {
    println data
    interleavers[algo](collection, Shuffle.OUT)
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
    interleavers[algo](collection, Shuffle.IN)
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
    interleavers[algo](collection, Shuffle.OUT_FOLDING)
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
    interleavers[algo](collection, Shuffle.IN_FOLDING)
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
    oneCollectionTest(interleavers[algo], max, list, Shuffle.OUT)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One list in-shuffle"() {
    given:
    def list = evensThenOdds(max)
    expect:
    oneCollectionTest(interleavers[algo], max, list, Shuffle.IN)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One list folding out-shuffle"() {
    given:
    def list = oddsThenFoldedEvens(max)
    expect:
    oneCollectionTest(interleavers[algo], max, list, Shuffle.OUT_FOLDING)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One list folding in-shuffle"() {
    given:
    def list = evensThenFoldedOdds(max)
    expect:
    oneCollectionTest(interleavers[algo], max, list, Shuffle.IN_FOLDING)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

}