[![Java CI with Gradle](https://github.com/jnellis/interleave/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/jnellis/interleave/actions/workflows/gradle.yml)

# interleave

### A utility class to perform in-place interleaving of elements in a collection.

NOTE: Currently this project is using jdk19 with `--enable-preview` and will either
be changed to a multi-release jar or simply wait it out until they are no longer 
preview features. If you need functionality for jdk8 or in between versions or porting then 
make an issue and I will try to add guidance. 

Based on the idea of shuffling two stacks of playing cards like
the [Faro Shuffle](https://en.wikipedia.org/wiki/Faro_shuffle), wherein cards
are taken one at a time, alternating from each pile to create a new pile of
interleaved cards.

Performing this action using new memory or heap space is rather trivial but doing so
without extra space, in-place is less intuitive.

For example, say we have numbers then letters in one collection that we wish to pair up.

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
The following three methods are named in order of appearance in the above article along 
with two more different methods which I discovered later.

* Permutation Interleave - This implementation follows the algorithm detailed in
  [A Simple In-Place Algorithm for In-Shuffle](https://arxiv.org/abs/0805.1598)
* Recursive Interleave - This implementation has aspects from both the Permutation and the Sequence
  interleavers. In essence, it processes elements in powers of 2 at a time but has no restrictions
  on the actual collection size.
* Sequence Interleave - This implementation uses an algorithmic sequence from the
  [Online Encyclopedia of Integer Sequences (A025480)](https://oeis.org/A025480) to determine the
  direct index positions of elements to be swapped. 
* Josephus Interleave - This implementation combines the main method from the Sequence Interleave
  for the first half of the collection and a cycle leader method similar to the Permutation
  Interleave for the second half. 
* Shuffle Prime Interleave - This implementation is a straight cycle leader similar to 
  Permutation Interleave. 
                                    
All implementations mostly have the same runtime performance with each being slightly better or
worse at various collection sizes. The purported theoretical runtime of the Permutation Interleave 
is O(n) but in reality, all the algorithms fall somewhere between a lower bound of _n&nbsp;log(log&nbsp;n)_ 
and a upperbound of _n&nbsp;log&nbsp;n_. Locality plays a huge part and except for the Sequence Interleaver,
things start to fall apart pretty quickly once you start to exhaust LLC cache space.
                                                                                 
## API

The API supports arrays and java.util.List's, interleaving a single collection with 
itself or interleaving two collections across each other. Furthermore, there are four types of 
shuffling patterns: In-Shuffle, Out-Shuffle and a folding version of both of these two shuffles.
Folding shuffles interleave the front with the back of the collection, effectively folding the 
collection at the midpoint. In the case of folding two collections, interleaving pulls 
from the back of the second collection. Consult the 
[javadocs](https://jnellis.github.io/interleave/javadoc) for behavior of each type 
of shuffle. There are a few gotchas or unintuitive behaviors when trying to interleave collections
of an un-even size or two collections with un-equal sizes, but each case is illustrated in the 
[Interleaver](https://jnellis.github.io/interleave/javadoc/net/jnellis/interleave/Interleaver.html)
interface.

Entry points into the API for a particular implementation can be directly chosen from the class
[Interleavers]().
It contains a static instance to each implementation.
                                                  
    String[] namesAndCities = { "Tom", "Dick", "Harry", "New York", "Chicago", "Miami" };
    Interleaver il = Interleavers.SEQUENCE;
    il.interleave(namesAndCities, Shuffle.OUT);
    // namesAndCites is now ["Tom", "New York", "Dick", "Chicago", "Harry", "Miami"]

Additionally, at this time,  there are variants for primitive arrays (ints, longs, floats, doubles,
chars, and bytes) but only for InShuffle. See [PrimitiveArrayInShuffleInterleavers](https://jnellis.github.io/interleave/javadoc/net/jnellis/interleave/PrimitiveArrayInShuffleInterleavers.html)
Under the hood, all interleave implementations are InShuffle as it's a minor tweak to change an 
OutShuffle to an InShuffle. Folding variants incur a reversal operation overhead for simplicity.

## Benchmarking

This a gradle project so to compile, build and run you should be able to run 
`.\gradlew build`. In the `build.gradle` file there is a code block labeled `jmh`
which contains a mapping to common jmh runtime args.  The two main benchmarks
are `InPlaceInterleaverBench` and `PrimitiveInterleaverBench`.

    jmh {
    include = [".*One.*InShuffle"]
    iterations = 10
    warmupIterations = 10 
    timeUnit = 'ns'
    timeOnIteration = '1s'
    warmup = '1s'  
    benchmarkParameters = [
      'max': [ 1000],
      // 'arrayType': ["ints"],
      'colType': ["Unique"],
      'interleaverName':["shuffle"]
    ]
    ....
    }

These are the main parameters you'll want to adjust depending on the benchmark 
being run. `include` accepts a regex, to either a class name or a method annotated
with `@Benchmark`. For example the above regex would match all one list or one 
array in-shuffle (folding or not) benchmarks for all three implementations. Most 
of the rest of these parameters are adequate for all tests. BenchmarkParameters 
map to a set of runtime parameters you can change to choose array sizes, collection
type being stored, a particular implementation. See the source code to see what
is possible. To run the benchmark you must run the jmh task 

`.\gradlew jmh`

At the completion 
of a benchmark, a report will be made in a `docs/jmh/{date}/{time}/` which you 
can view in a browser. There is a button in the report page that you can switch
the data view to logarithmic scale in order to view the report results easier.


