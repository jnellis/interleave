package net.jnellis.interleave


import spock.lang.Specification

import static org.junit.jupiter.api.Assertions.assertEquals

class InterleaversBase extends Specification {

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

}