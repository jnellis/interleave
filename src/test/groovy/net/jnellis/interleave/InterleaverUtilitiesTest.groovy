package net.jnellis.interleave

import spock.lang.Specification
import spock.lang.Unroll

class InterleaverUtilitiesTest extends Specification {

  def "test interleave"() {
    expect:
    PermutationInterleaver.interleave(a as List<Object>)
    a == b
    where:
    a << ([[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l'],
           [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm']] as Serializable)
    b << [['a', 1, 'b', 2, 'c', 3, 'd', 4, 'e', 5, 'f', 6, 'g', 7, 'h', 8, 'i', 9, 'j', 10, 'k',
           11, 'l', 12],
          ['a', 1, 'b', 2, 'c', 3, 'd', 4, 'e', 5, 'f', 6, 'g', 7, 'h', 8, 'i', 9, 'j', 10, 'k',
           11, 'l', 12, 'm']]
  }

  def "test two list rotate"() {
    expect:
    Util.rotateRight(a, b, m)
    a + b == expected
    where:
    m | a      | b               | expected
    3 | [3, 4] | [5, 6, 7, 1, 2] | [7, 1, 2, 3, 4, 5, 6]
    0 | [3, 4] | [5, 6, 7, 1, 2] | [3, 4, 5, 6, 7, 1, 2]
    1 | [3, 4] | [5, 6, 7, 1, 2] | [2, 3, 4, 5, 6, 7, 1]
    5 | [3, 4] | [5, 6, 7, 1, 2] | [5, 6, 7, 1, 2, 3, 4]
    2 | [3, 4] | [5, 6, 7, 1, 2] | [1, 2, 3, 4, 5, 6, 7]
  }

  def "test two array rotate"() {
    expect:
    def aa = a.toArray()
    def bb = b.toArray()
    Util.rotateRight(aa, 1, a.size(),
        bb, 0, b.size() - 1, m)
    aa + bb == expected.toArray()
    where:
    m | a         | b               | expected
    3 | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 6, 7, 1, 3, 4, 5, 2]
    0 | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 3, 4, 5, 6, 7, 1, 2]
    1 | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 1, 3, 4, 5, 6, 7, 2]
    5 | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 4, 5, 6, 7, 1, 3, 2]
    2 | [0, 3, 4] | [5, 6, 7, 1, 2] | [0, 7, 1, 3, 4, 5, 6, 2]
  }

  @Unroll
  def "test list rotate #a right #m places"() {
    given:
    def n = a.size()
    def expected = new ArrayList(a[(n - (m % n))..<n])
    println("expected is " + expected)
    expected.addAll(List.copyOf(a[0..<(n - (m % n))]))
    println("expected is " + expected)
    expect:
//    Util.rotateRight(a,m)
    Util.rotateViaTripleReverse(a, m, false)
    a == expected
    println("a is " + a)
    println()
    where:
    m  | a
    3  | [1, 2, 3, 4, 5, 6, 7]
    3  | [1, 2, 3, 4, 5, 6]
    2  | [1, 2, 3, 4, 5, 6, 7]
    1  | [1, 2, 3, 4, 5, 6, 7]
    6  | [1, 2, 3, 4, 5, 6, 7]
    7  | [1, 2, 3, 4, 5, 6, 7]
    0  | [1, 2, 3, 4, 5, 6]
    58 | [1, 2, 3, 4, 5, 6, 7]
  }

  @Unroll
  def "test array rotate #a right #m places"() {
    given:
    def from = 1
    def to = a.length - 1
    expect:
    Util.rotateViaTripleReverse(a, from, to, m, false)
    a[from..<to].toArray() == expected.toArray()
    println("a is " + a)
    println()
    where:
    m  | a                                  || expected
    3  | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [4, 5, 6, 2, 3]
    3  | [1, 2, 3, 4, 5, 6] as Integer[]    || [3, 4, 5, 2]
    2  | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [5, 6, 2, 3, 4]
    1  | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [6, 2, 3, 4, 5]
    6  | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [6, 2, 3, 4, 5]
    7  | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [5, 6, 2, 3, 4]
    0  | [1, 2, 3, 4, 5, 6] as Integer[]    || [2, 3, 4, 5]
    58 | [1, 2, 3, 4, 5, 6, 7] as Integer[] || [4, 5, 6, 2, 3]
  }

  @Unroll
  def "test fastmod with 2^#exp % #k"() {
    expect:
    BigInteger expected = BigInteger.TWO.modPow(BigInteger.valueOf(exp), BigInteger.valueOf(k))
    int val = (int) Util.fastmod(2, exp, k)
    val == expected.intValue()
    where:
    exp  | k
    55   | 3**6
    434  | 3**9
    600  | 3**19
    900  | 3**19
    1800 | 3**19
  }

}
