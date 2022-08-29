package net.jnellis.interleave

import spock.lang.Specification
import spock.lang.Unroll

class InterleaverUtilitiesTest extends Specification {

  @Unroll
  def "test two list rotate #m places"() {
    expect:
    Util.rotate(a, b, m)
    a + b == expected
    where:
    m  | a      | b               | expected
    3  | [3, 4] | [5, 6, 7, 1, 2] | [7, 1, 2, 3, 4, 5, 6]
    0  | [3, 4] | [5, 6, 7, 1, 2] | [3, 4, 5, 6, 7, 1, 2]
    1  | [3, 4] | [5, 6, 7, 1, 2] | [2, 3, 4, 5, 6, 7, 1]
    5  | [3, 4] | [5, 6, 7, 1, 2] | [5, 6, 7, 1, 2, 3, 4]
    2  | [3, 4] | [5, 6, 7, 1, 2] | [1, 2, 3, 4, 5, 6, 7]
    -5 | [3, 4] | [5, 6, 7, 1, 2] | [1, 2, 3, 4, 5, 6, 7]
    -2 | [3, 4] | [5, 6, 7, 1, 2] | [5, 6, 7, 1, 2, 3, 4]
  }

  @Unroll
  def "test two array rotate #m places"() {
    expect:
    def aa = a.toArray()
    def bb = b.toArray()
    Util.rotate(aa, 1, a.size(),
        bb, 0, b.size() - 1, m)
    aa + bb == expected.toArray()
    where:
    m   | a         | b               | expected
    3   | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 6, 7, 1, 3, 4, 5, 2]
    0   | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 3, 4, 5, 6, 7, 1, 2]
    1   | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 1, 3, 4, 5, 6, 7, 2]
    5   | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 4, 5, 6, 7, 1, 3, 2]
    2   | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 7, 1, 3, 4, 5, 6, 2]
    -3  | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 6, 7, 1, 3, 4, 5, 2]
    -10 | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 7, 1, 3, 4, 5, 6, 2]
    -1  | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 4, 5, 6, 7, 1, 3, 2]
    -5  | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 1, 3, 4, 5, 6, 7, 2]
    -2  | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 5, 6, 7, 1, 3, 4, 2]
  }


  @Unroll
  def "test array rotate #a right #m places"() {
    given:
    def from = 1
    def to = a.length - 1
    expect:
    Util.rotate(a, from, to, m)
    a[from..<to].toArray() == expected.toArray()
    println("a is " + a)
    println()
    where:
    m   | a                                  || expected
    3   | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [4, 5, 6, 2, 3]
    3   | [1, 2, 3, 4, 5, 6] as Integer[]    || [3, 4, 5, 2]
    2   | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [5, 6, 2, 3, 4]
    1   | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [6, 2, 3, 4, 5]
    6   | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [6, 2, 3, 4, 5]
    7   | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [5, 6, 2, 3, 4]
    0   | [1, 2, 3, 4, 5, 6] as Integer[]    || [2, 3, 4, 5]
    58  | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [4, 5, 6, 2, 3]
    -3  | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [5, 6, 2, 3, 4]
    -58 | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [5, 6, 2, 3, 4]
    -5  | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [2, 3, 4, 5, 6]
  }

}
