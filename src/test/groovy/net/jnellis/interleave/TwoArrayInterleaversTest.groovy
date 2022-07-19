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
class TwoArrayInterleaversTest extends InterleaversBase {

  @Shared
  def interleavers = [
      "a025480": InPlaceInterleaver::interleave,
//      "permutation": PermutationInterleaver::interleave,
//      "recursive"  : RecursiveInterleaver::interleave
  ]

  @Shared
  int[] maxes = [2, 3, 4, 9, 10, 11, 31, 32, 33, 999, 1000, 1001, 999999, 1000000]

  def paramCombinations() {
    [maxes, interleavers.keySet()].combinations()*.flatten()
  }

  @Unroll("#featureName[#iterationIndex] (#parity, length of both lists is #max) #algo method")
  def "Two array out-shuffle"() {
    given:
    init(max)
    expect:
    twoCollectionTest(interleavers[algo], max, odds.toArray(), evens.toArray(), false, false)
    if (parity === "odd") {
      init(max)
      // try it with larger list on evens side
      odds.removeLast()
      evens.add(-1)

      twoCollectionTest(interleavers[algo], max, odds.toArray(), evens.toArray(), false, false)
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
    twoCollectionTest(interleavers[algo], max, evens.toArray(), odds.toArray(), true, false)
    if (parity === "odd") {
      init(max)
      // try it with larger list on evens side
      odds.removeLast()
      evens.add(-1)
      twoCollectionTest(interleavers[algo], max, evens.toArray(), odds.toArray(), true, false)
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
    twoCollectionTest(interleavers[algo], max, odds.toArray(), evens.toArray(), false, true)
    if (parity === "odd") {
      init(max)
      Collections.reverse(evens)
      // try it with larger list on evens side
      odds.removeLast()
      evens.add(-1)
      twoCollectionTest(interleavers[algo], max, odds.toArray(), evens.toArray(), false, true)
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
    twoCollectionTest(interleavers[algo], max, evens, odds, true, true)
    if (parity === "odd") {
      init(max)
      // try it with larger list on evens side
      odds.removeLast()
      evens.add(-1)
      Collections.reverse(odds)
      twoCollectionTest(interleavers[algo], max, evens, odds, true, true)
    }
    where:
    [max, algo] << paramCombinations()
    parity = (max % 2) == 0 ? "even" : "odd"
  }

}