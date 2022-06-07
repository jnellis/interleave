package net.jnellis.interleave


import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.junit.jupiter.api.Assertions.assertEquals

/**
 * User: Joe Nellis
 * Date: 5/9/2022 
 * Time: 1:50 PM
 **/
class InPlaceInterleaversSpec extends Specification {

  @Shared
  def interleavers = [
      "a025480"    : InPlaceInterleaver::interleave,
      "permutation": PermutationInterleaver::interleave,
      "recursive"  : RecursiveInterleaver::interleave
  ]


  @Shared
  def maxes = [2, 3, 4, 9, 10, 11, 31, 32, 33, 999, 1000, 1001, 999999, 1000000]
//  def maxes = [10, 12,2, 4, 8, 16,32,64,65536,(1<<23)]
  def odds, evens

  def initLists(max) {
    odds = new IntRange(1, max).step(2).asList()
    evens = new IntRange(2, max).step(2).asList()
  }

  def oneListTest(interleaver, list, shuffle, folding) {
    if (list.size() < 10000) println list
    interleaver(list, shuffle, folding)
    if (list.size() < 10000) println list
    verifySequential(list)
  }

  def twoListTest(lista, listb, shuffle, folding) {
    if (lista.size() < 10000) println(lista.toString() + ", " + listb.toString())
    InPlaceInterleaver.interleave(lista, listb, shuffle, folding)
    if (lista.size() < 10000) println(lista.toString() + ", " + listb.toString())
    verifySequential(lista, listb)
  }

  @Unroll("#featureName[#iterationIndex] (#parity length of both lists is #max)")
  def "Two lists out-shuffle"() {
    expect:
    initLists(max)
    twoListTest(odds, evens, false, false)
    if (parity === "odd") {
      initLists(max)
      // try it with larger list on evens side
      odds.removeLast()
      evens.add(-1)
      twoListTest(odds, evens, false, false)
    }
    where:
    max << maxes
    parity = (max % 2) == 0 ? "even" : "odd"
  }

  @Unroll("#featureName[#iterationIndex] (#parity length of both lists is #max)")
  def "Two lists in-shuffle"() {
    expect:
    initLists(max)
    twoListTest(evens, odds, true, false)
    if (parity === "odd") {
      initLists(max)
      // try it with larger list on evens side
      odds.removeLast()
      evens.add(-1)
      twoListTest(evens, odds, true, false)
    }
    where:
    max << maxes
    parity = (max % 2) == 0 ? "even" : "odd"
  }

  @Unroll("#featureName[#iterationIndex] (#parity length of both lists is #max)")
  def "Two lists folding out-shuffle"() {
    expect:
    initLists(max)
    Collections.reverse(evens)
    twoListTest(odds, evens, false, true)
    if (parity === "odd") {
      initLists(max)
      Collections.reverse(evens)
      // try it with larger list on evens side
      odds.removeLast()
      evens.add(-1)
      twoListTest(odds, evens, false, true)
    }
    where:
    max << maxes
    parity = (max % 2) == 0 ? "even" : "odd"
  }

  @Unroll("#featureName[#iterationIndex] (#parity length of both lists is #max)")
  def "Two lists folding in-shuffle"() {
    expect:
    initLists(max)
    Collections.reverse(odds)
    twoListTest(evens, odds, true, true)
    if (parity === "odd") {
      initLists(max)
      // try it with larger list on evens side
      odds.removeLast()
      evens.add(-1)
      Collections.reverse(odds)
      twoListTest(evens, odds, true, true)
    }
    where:
    max << maxes
    parity = (max % 2) == 0 ? "even" : "odd"
  }


  @Unroll("#featureName[#iterationIndex] (#parity length of #max) #algo method")
  def "One list out-shuffle"() {
    expect:
    initLists(max)
    odds.addAll(evens)
    oneListTest(interleavers[algo], odds, false, false)
    where:
    [max, algo] << [maxes, interleavers.keySet()].combinations()*.flatten()
    parity = (max % 2) == 0 ? "even" : "odd"
  }

  @Unroll("#featureName[#iterationIndex] (#parity length of #max) #algo method")
  def "One list in-shuffle"() {
    expect:
    initLists(max)
    evens.addAll(odds)
    oneListTest(interleavers[algo], evens, true, false)
    where:
    [max, algo] << [maxes, interleavers.keySet()].combinations()*.flatten()
    parity = (max % 2) == 0 ? "even" : "odd"
  }

  @Unroll("#featureName[#iterationIndex] (#parity length of #max) #algo method")
  def "One list folding out-shuffle"() {
    expect:
    initLists(max)
    Collections.reverse(evens)
    odds.addAll(evens)
    oneListTest(interleavers[algo], odds, false, true)
    where:
    [max, algo] << [maxes, interleavers.keySet()].combinations()*.flatten()
    parity = (max % 2) == 0 ? "even" : "odd"
  }

  @Unroll("#featureName[#iterationIndex] (#parity length of #max) #algo method")
  def "One list folding in-shuffle"() {
    expect:
    initLists(max)
    Collections.reverse(odds)
    evens.addAll(odds)
    oneListTest(interleavers[algo], evens, true, true)
    where:
    [max, algo] << [maxes, interleavers.keySet()].combinations()*.flatten()
    parity = (max % 2) == 0 ? "even" : "odd"
  }

  int verifySequential(List<Integer> list) {
    final int seed = list.get(0) - 1
    return list.stream()
               .reduce(seed, (i, j) -> {
                 assertEquals(i + 1, j)
                 return j
               })
  }

  // if lists are odd size then only verify to double of the lesser sized
  int verifySequential(List  a, List  b) {
    def min = Math.min(a.size(), b.size())
    a.subList(0, min).addAll(b)
    verifySequential(a.subList(0, min * 2))
  }
}
