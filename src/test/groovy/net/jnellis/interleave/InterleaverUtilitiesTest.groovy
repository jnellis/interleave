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

  def "test find2m"() {
    expect:
    PermutationInterleaver.find2m(twoN as int) == twoM
    where:
    twoN << [3, 4, 9, 15, 26, 27, 28, 81, 82, 83]
    twoM << [2, 2, 8, 8, 8, 26, 26, 80, 80, 80]

  }

  @Unroll
  def "test rotate #a right #m places"() {
    given:
    def n = a.size()
    def expected = new ArrayList(a[(n - (m % n))..<n])
    println("expected is " + expected)
    expected.addAll(List.copyOf(a[0..<(n - (m % n))]))
    println("expected is " + expected)
    expect:
    Util.rotateRight(a,m)
//    PermutationInterleaver.rotateViaTripleReverse(a, m)
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
  def "test fastmod with 2^#exp % #k"() {
    expect:
    BigInteger expected = BigInteger.TWO.modPow(BigInteger.valueOf(exp), BigInteger.valueOf(k))
    int val = (int)Util.fastmod(2, exp, k)
    val == expected.intValue()
    where:
    exp | k
    55  | 3**6
    434 | 3**9
    600 | 3**19
    900 | 3**19
    1800| 3**19
  }

}
