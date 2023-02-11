package net.jnellis.interleave

import spock.lang.Shared
import spock.lang.Unroll

class OneArrayInterleaverTest extends InterleaversBase {


  @Shared
  def interleavers = [
      "a025480"    : Interleavers.SEQUENCE::interleave,
      "permutation": Interleavers.PERMUTATION::interleave,
      "recursive"  : Interleavers.RECURSIVE::interleave,
      "josephus"   : Interleavers.JOSEPHUS::interleave,
      "shuffle"    : Interleavers.SHUFFLE::interleave
  ]

  def paramCombinations() {
    [maxes, interleavers.keySet()].combinations()*.flatten()
  }

  @Unroll('#featureName[#iterationIndex] #algo #variant')
  def "one array out-shuffle, unexpected behavior example"() {
    println data
    interleavers[algo](collection, Shuffle.OUT)
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
  def "one array in-shuffle, unexpected behavior example"() {
    println data
    interleavers[algo](collection, Shuffle.IN)
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
  def "one array folding out-shuffle, unexpected behavior example"() {
    println data
    interleavers[algo](collection, Shuffle.OUT_FOLDING)
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
  def "one array folding in-shuffle, unexpected behavior example"() {
    println data
    interleavers[algo](collection, Shuffle.IN_FOLDING)
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
    oneCollectionTest(interleavers[algo], max, arr, Shuffle.OUT)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One array in-shuffle"() {
    given:
    def arr = evensThenOdds(max).toArray()
    expect:
    oneCollectionTest(interleavers[algo], max, arr, Shuffle.IN)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One array folding out-shuffle"() {
    given:
    def arr = oddsThenFoldedEvens(max).toArray()
    expect:
    oneCollectionTest(interleavers[algo], max, arr, Shuffle.OUT_FOLDING)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of #max) #algo method")
  def "One array folding in-shuffle"() {
    given:
    def arr = evensThenFoldedOdds(max).toArray()
    expect:
    oneCollectionTest(interleavers[algo], max, arr, Shuffle.IN_FOLDING)
    where:
    [max, algo] << paramCombinations()
    parity = getParity(max)
  }


}
