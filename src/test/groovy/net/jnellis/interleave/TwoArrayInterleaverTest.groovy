package net.jnellis.interleave

import spock.lang.Shared
import spock.lang.Unroll

/**
 *  NOTE ON TWO LIST/ARRAY TESTS:
 * Two list/array tests are run twice if one of the collections' length is odd.
 * In each of these cases, the odd sized list is tested as both the first
 * parameter and the second parameter. The "sequential-ness" of the two
 * collections is only tested up to the size of the smaller sized collection.
 *
 */
class TwoArrayInterleaverTest extends InterleaversBase {

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
  def "two array out-shuffle, unexpected behavior example"() {
    println collection1 + " " + collection2
    interleavers[algo](collection1, collection2, Shuffle.OUT)
    expect:
    println collection1 + " " + collection2
    collection1 == expected1
    collection2 == expected2
    where:
    [data, algo] << [getTypes()["outShuffle2"], interleavers.keySet()].combinations()
    collection1 = new ArrayList(data[1]).toArray()
    collection2 = new ArrayList(data[2]).toArray()
    expected1 = data[3]
    expected2 = data[4]
    variant = data[0]
  }

  @Unroll('#featureName[#iterationIndex] #algo #variant')
  def "two array in-shuffle, unexpected behavior example"() {
    println collection1 + " " + collection2
    interleavers[algo](collection1, collection2, Shuffle.IN)
    expect:
    println collection1 + " " + collection2
    collection1 == expected1
    collection2 == expected2
    where:
    [data, algo] << [getTypes()["inShuffle2"], interleavers.keySet()].combinations()
    collection1 = new ArrayList(data[1]).toArray()
    collection2 = new ArrayList(data[2]).toArray()
    expected1 = data[3]
    expected2 = data[4]
    variant = data[0]
  }

  @Unroll('#featureName[#iterationIndex] #algo #variant')
  def "two array folding out-shuffle, unexpected behavior example"() {
    println collection1 + " " + collection2
    interleavers[algo](collection1, collection2, Shuffle.OUT_FOLDING)
    expect:
    println collection1 + " " + collection2
    collection1 == expected1
    collection2 == expected2
    where:
    [data, algo] << [getTypes()["foldingOutShuffle2"], interleavers.keySet()].combinations()
    collection1 = new ArrayList(data[1]).toArray()
    collection2 = new ArrayList(data[2]).toArray()
    expected1 = data[3]
    expected2 = data[4]
    variant = data[0]
  }

  @Unroll('#featureName[#iterationIndex] #algo #variant')
  def "two array folding in-shuffle, unexpected behavior example"() {
    println collection1 + " " + collection2
    interleavers[algo](collection1, collection2, Shuffle.IN_FOLDING)
    expect:
    println collection1 + " " + collection2
    collection1 == expected1
    collection2 == expected2
    where:
    [data, algo] << [getTypes()["foldingInShuffle2"], interleavers.keySet()].combinations()
    collection1 = new ArrayList(data[1]).toArray()
    collection2 = new ArrayList(data[2]).toArray()
    expected1 = data[3]
    expected2 = data[4]
    variant = data[0]
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of both lists is #max) #algo method")
  def "Two array out-shuffle"() {
    given:
    init(max)
    expect:
    twoCollectionTest(interleavers[algo], max, odds.toArray(), evens.toArray(), Shuffle.OUT)
    if (parity === "odd") {
      init(max)
      // try it with larger list on evens side
      odds.removeLast()
      evens.add(-1)

      twoCollectionTest(interleavers[algo], max, odds.toArray(), evens.toArray(), Shuffle.OUT)
    }
    where:
    [max, algo] << paramCombinations()
    parity = (max % 2) == 0 ? "even" : "odd"
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of both lists is #max) #algo method")
  def "Two array in-shuffle"() {
    given:
    init(max)
    expect:
    twoCollectionTest(interleavers[algo], max, evens.toArray(), odds.toArray(), Shuffle.IN)
    if (parity === "odd") {
      init(max)
      // try it with larger list on evens side
      odds.removeLast()
      evens.add(-1)
      twoCollectionTest(interleavers[algo], max, evens.toArray(), odds.toArray(), Shuffle.IN)
    }
    where:
    [max, algo] << paramCombinations()
    parity = (max % 2) == 0 ? "even" : "odd"
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of both lists is #max) #algo method")
  def "Two array folding out-shuffle"() {
    given:
    init(max)
    expect:
    Collections.reverse(evens)
    twoCollectionTest(interleavers[algo], max, odds.toArray(), evens.toArray(), Shuffle.OUT_FOLDING)
    if (parity === "odd") {
      init(max)
      // try it with larger list on evens side
      odds.removeLast()
      evens.add(-1)
      Collections.reverse(evens)
      twoCollectionTest(interleavers[algo], max, odds.toArray(), evens.toArray(), Shuffle.OUT_FOLDING)
    }
    where:
    [max, algo] << paramCombinations()
    parity = (max % 2) == 0 ? "even" : "odd"
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of both lists is #max) #algo method")
  def "Two array folding in-shuffle"() {
    given:
    init(max)
    expect:
    Collections.reverse(odds)
    twoCollectionTest(interleavers[algo], max, evens.toArray(), odds.toArray(), Shuffle.IN_FOLDING)
    if (parity === "odd") {
      init(max)
      // try it with larger list on evens side
      odds.removeLast()
      evens.add(-1)
      Collections.reverse(odds)
      twoCollectionTest(interleavers[algo], max, evens.toArray(), odds.toArray(), Shuffle.IN_FOLDING)
    }
    where:
    [max, algo] << paramCombinations()
    parity = (max % 2) == 0 ? "even" : "odd"
  }

}