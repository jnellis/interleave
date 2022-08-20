package net.jnellis.interleave

import spock.lang.Shared
import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertEquals

class InterleaversBase extends Specification {

  // comment/uncomment to choose between regular test collections or one big one.
  @Shared
  int[] maxes = [2, 3, 4, 9, 10, 11, 31, 32, 33, 999, 1000, 1001, 999999, 1_000_000]
//  int[] maxes = [10_000_000]

  def odds, evens

  def init(max) {
    odds = new IntRange(1, max).step(2).asList()
    evens = new IntRange(2, max).step(2).asList()
  }

  def oddsThenEvens(max){
    init(max)
    odds.addAll(evens)
    return odds
  }

  def evensThenOdds(max){
    init(max)
    evens.addAll(odds)
    return evens
  }

  def oddsThenFoldedEvens(max){
    init(max)
    Collections.reverse(evens)
    odds.addAll(evens)
    return odds
  }

  def evensThenFoldedOdds(max){
    init(max)
    Collections.reverse(odds)
    evens.addAll(odds)
    return evens
  }

  def getParity(max){
    return (max % 2) == 0 ? "even" : "odd"
  }

  def oneCollectionTest(interleaver, max, col, shuffle, folding) {
    if (max < 10000) println col
    interleaver col, shuffle, folding
    if (max < 10000) println col
    verifySequential col
  }

  def twoCollectionTest(interleaver, max, cola, colb, shuffle, folding) {
    if (max < 10000) println(cola.toString() + ", " + colb.toString())
    interleaver cola, colb, shuffle, folding
    if (max < 10000) println(cola.toString() + ", " + colb.toString())
    verifySequential cola, colb
  }

  def verifySequential(List list) {
    final int seed = list.get(0) - 1
    list.stream()
        .reduce(seed, (i, j) -> {
          assertEquals(i + 1, j)
          return j
        })
  }

  def verifySequential(List a, List b) {
    def min = Math.min(a.size(), b.size())
    a.subList(0, min).addAll(b)
    verifySequential(a.subList(0, min * 2))
  }

  // if lists are uneven size then only verify to double of the lesser sized
  def verifySequential(array, int from = 0, int to = array.length) {
    array = array as Integer[]
    int seed = array[from] - 1
    Arrays.stream(array)
          .skip(from == 0 ? 0 : from - 1)
          .limit(to - from)
          .reduce(seed, (i, j) -> {
            assertEquals(i + 1, j)
            return j
          })
  }

  // if arrays are uneven size then only verify to double of the lesser sized
  def verifySequential(array1, array2) {
    array1 = array1 as Integer[]
    array2 = array2 as Integer[]
    int min = Math.min(array1.length, array2.length)
    verifySequential(array1, 0, min)
    assertEquals(array1[min-1] + 1, array2[0]) // bridging sequence test
    verifySequential(array2, 0, min)
  }

  @Shared
  def getTypes = {
    ["outShuffle": [
         ["even",     [1, 2, 3, 4, "a", "b", "c", "d"],      [1, "a", 2, "b", 3, "c", 4, "d"]],
         ["oddFront", [1, 2, 3, 4, 5, "a", "b", "c", "d"],   [1, "a", 2, "b", 3, "c", 4, "d", 5]],
         ["oddBack",  [1, 2, 3, 4, "a", "b", "c", "d", "e"], [1, "b", 2, "c", 3, "d", 4, "e", "a"]]
     ],
     "inShuffle" : [
         ["even",     [1, 2, 3, 4, "a", "b", "c", "d"],      ["a", 1, "b", 2, "c", 3, "d", 4]],
         ["oddFront", [1, 2, 3, 4, 5, "a", "b", "c", "d"],   [5, 1, "a", 2, "b", 3, "c", 4, "d"]],
         ["oddBack",  [1, 2, 3, 4, "a", "b", "c", "d", "e"], ["a", 1, "b", 2, "c", 3, "d", 4, "e"]]
     ],
     "foldingOutShuffle": [
         ["even",     [1, 2, 3, 4, "d", "c", "b", "a"],      [1, "a", 2, "b", 3, "c", 4, "d"]],
         ["oddFront", [1, 2, 3, 4, 5, "d", "c", "b", "a"],   [1, "a", 2, "b", 3, "c", 4, "d", 5]],
         ["oddBack",  [1, 2, 3, 4, "e", "d", "c", "b", "a"], [1, "a", 2, "b", 3, "c", 4, "d", "e"]]
     ],
     "foldingInShuffle" : [
         ["even",     [1, 2, 3, 4, "d", "c", "b", "a"],      ["a", 1, "b", 2, "c", 3, "d", 4]],
         ["oddFront", [1, 2, 3, 4, 5, "d", "c", "b", "a"],   ["a", 1, "b", 2, "c", 3, "d", 4, 5]],
         ["oddBack",  [1, 2, 3, 4, "e", "d", "c", "b", "a"], ["a", 1, "b", 2, "c", 3, "d", 4, "e"]]
     ],


     "outShuffle2": [
         ["even",     [1, 2, 3, 4],    ["a", "b", "c", "d"],      [1, "a", 2, "b"],    [3, "c", 4, "d"]],
         ["oddFront", [1, 2, 3, 4, 5], ["a", "b", "c", "d"],      [1, "a", 2, "b", 5], [3, "c", 4, "d"]],
         ["oddBack",  [1, 2, 3, 4],    ["a", "b", "c", "d", "e"], [1, "a", 2, "b"],    [3, "c", 4, "d", "e"]]
     ],
     "inShuffle2" : [
         ["even",     [1, 2, 3, 4],    [ "a", "b", "c", "d"],     ["a", 1, "b", 2],    ["c", 3, "d", 4]],
         ["oddFront", [1, 2, 3, 4, 5], ["a", "b", "c", "d"],      ["a", 1, "b", 2, 5], ["c", 3, "d", 4]],
         ["oddBack",  [1, 2, 3, 4],    ["a", "b", "c", "d", "e"], ["a", 1, "b", 2],    ["c", 3, "d", 4, "e"]]
     ],
     "foldingOutShuffle2": [
         ["even",     [1, 2, 3, 4],    ["d", "c", "b", "a"],      [1, "a", 2, "b"],    [3, "c", 4, "d"]],
         ["oddFront", [1, 2, 3, 4, 5], ["d", "c", "b", "a"],      [1, "a", 2, "b", 5], [3, "c", 4, "d"]],
         ["oddBack",  [1, 2, 3, 4],    ["e", "d", "c", "b", "a"], [1, "a", 2, "b"],    [3, "c", 4, "d", "e"]]
     ],
     "foldingInShuffle2" : [
         ["even",     [1, 2, 3, 4],    ["d", "c", "b", "a"],      ["a", 1, "b", 2],    ["c", 3, "d", 4]],
         ["oddFront", [1, 2, 3, 4, 5], ["d", "c", "b", "a"],      ["a", 1, "b", 2, 5], ["c", 3, "d", 4]],
         ["oddBack",  [1, 2, 3, 4],    ["e", "d", "c", "b", "a"], ["a", 1, "b", 2],    ["c", 3, "d", 4, "e"]]
     ]
    ]
  }
}