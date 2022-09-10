[![Java CI with Gradle](https://github.com/jnellis/interleave/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/jnellis/interleave/actions/workflows/gradle.yml)

# interleave

### A utility class to perform in-place interleaving of elements in a collection.

Based on the idea of shuffling two stacks of playing cards like
the [Faro Shuffle](https://en.wikipedia.org/wiki/Faro_shuffle), wherein cards
are taken one at a time, alternating from each pile to create a new pile of
interleaved cards.

Performing this action using new memory or heap space is rather trivial but doing so
without extra space, in-place is less intuitive.

For example, say we have a numbers and letters in one collection that we wish to pair up.

`[1, 2, 3, 4, 5, a, b, c, d, e]`  -->  `[a, 1, b, 2, c, 3, d, 4, e, 5]`

Or we have two collections we wish to interleave with each other.

`[1, 2, 3, 4]` ,  `[a, b, c, d]`  --> `[a, 1, b, 2]`, `[c, 3, d, e]`

The previous shuffles are called In-Shuffles, but we can do Out-Shuffle also

`[1, 2, 3, 4, 5, a, b, c, d, e]`  -->  `[1, a, 2, b, 3, c, 4, d, 5, e]`

An Out-shuffle has the behavior that the first and last elements remain in the
same place.

## Motivation & Implementation notes

Original motivation came from the following [stack exchange thread](https://cs.stackexchange.com/questions/332/in-place-algorithm-for-interleaving-an-array)
where three different algorithms are presented to perform an in-place interleaving of an array. 
The following three implementations are named in order of appearance in the above article.

* Permutation Interleave - This implementation follows the algorithm detailed in
  [A Simple In-Place Algorithm for In-Shuffle](https://arxiv.org/abs/0805.1598)
* Recursive Interleave - This implementation has aspects from both the Permutation and the Sequence
  interleavers. In essence, it processes elements in powers of 2 at a time but has no restrictions
  on the actual collection size.
* Sequence Interleave - This implementation uses an algorithmic sequence from the
  [Online Encyclopedia of Integer Sequences (A025480)](https://oeis.org/A025480) to determine the
  direct index positions of elements to be swapped. 

All implementations mostly have the same runtime performance with each being slightly better or
worse at various collection sizes. The purported runtime of the Permutation Interleave is O(n) 
but in actuality, all the algorithms are closer to `O(log log n)`. It is only when you get to into
very large memory sized collections does one perform better than the other. For the 
Recursive interleave, there is guaranteed to be some memory pressure in stack allocated index usage.
The case is the same in the Permutation interleave as it currently does recursively call itself 
because it is technically tail call safe but Java does not support this functionality yet. In the 
case of the Sequence Interleaver, performance wanes at higher collection sizes as the algorithm
is susceptible to a number of misses when choosing certain indexes. Yet, the Sequence Interleaver 
can generally be the fastest implementation in many cases. 

The implementation of the A025480 sequence in the Sequence interleaver is slightly different from 
given in the stack exchange article above and may perform better on newer 
CPUs that support [x86 BMI1](https://en.wikipedia.org/wiki/X86_Bit_manipulation_instruction_set) 
which has an intrinsic native call to TZCNT in Integer.numberOfTrailingZeros. 
                                                                                 
## API

The API supports arrays and java.util.List's, interleaving a single collection with 
itself or interleaving two collections across each other. Furthermore, there are four types of 
shuffling patterns: In-Shuffle, Out-Shuffle and a folding version of both of these two shuffles.
Folding shuffles interleave the front with the back of the collection, effectively folding the 
collection at the midpoint. In the case of folding two collections, interleaving pulls 
from the back of the second collection. Consult the 
[javadocs](https://jnellis.github.io/interleave/javadocs) for behavior of each type 
of shuffle. There are a few gotchas or unintuitive behaviors when trying to interleave collections
of an un-even size or two collections with un-equal sizes, but each case is illustrated in the 
[Interleaver](https://jnellis.github.io/interleave/javadocs/net/jnellis/interleave/Interleaver.html)
interface.  

Entry points into the API for a particular implementation can be directly chosen from the class
[Interleavers](https://jnellis.github.io/interleave/javadocs/net/jnellis/interleave/Interleavers.html).
It contains a static instance to each implementation.
                                                  
    String[] namesAndCities = { "Tom", "Dick", "Harry", "New York", "Chicago", "Miami" };
    Interleaver il = Interleavers.SEQUENCE;
    il.interleave(namesAndCities, Shuffle.OUT);
    // namesAndCites is now ["Tom", "New York", "Dick", "Chicago", "Harry", "Miami"]


