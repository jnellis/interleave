---
# Feel free to add content and custom Front Matter to this file.
# To modify the layout, see https://jekyllrb.com/docs/themes/#overriding-theme-defaults

layout: default
title: In-place Interleaving in O(n) time
---

# TL;DR
Interleaving two halves or two lists such that they alternate elements is a trivial 
job if you allocate memory for a temporary swap space. But what if you want
to do this operation in-place without needing more space? Can it be done in O(n) time?
Five algorithms are presented to accomplish this feat. 

# Background
The [Faro shuffle](https://en.wikipedia.org/wiki/Faro_shuffle) is a famous deck of cards 
shuffling technique where you split the deck and then shuffle the two halves back together
(hopefully) alternating each cards position from each half exactly. This is sometimes called 
interleaving, a perfect shuffle, or zipping...like a zipper.
I was looking to interleave together two lists but surprisingly Java doesn't have that ability.
There was a (very) brief moment in the Streams api that had such a zip method, but it 
didn't make the cut. Python has a zip method. Then I was curious if it could be done without using a temporary
workspace and I stumbled upon this [stack exchange cs question](https://cs.stackexchange.com/questions/332/in-place-algorithm-for-interleaving-an-array/105263)
that showed three methods for doing it in O(1) space.

I'm going to present these three algorithms and two more I discovered. None of these have names
that I'm aware of, so I just started calling them by the file name I implemented them in.
                                                                      
# In-Shuffle & Out-Shuffle & Folding
There are two main types of interleave, the In-Shuffle and Out-Shuffle, which determine
which half or collection starts the interleaving process. For example an in-shuffle
of ```[a,b,c,d,1,2,3,4]``` would start with splitting the collection and beginning the
interleave with **1**, then **a**, then **2**, then **b** and so on until you get 
```[1,a,2,b,3,c,4,d]```. The distinction of the out-shuffle 
is that first and last elements remain at the same position. The previous example would
yield ```[a,1,b,2,c,3,d,4]```. 

One interesting point is that all out-shuffles can be represented by a smaller in-shuffle 
operation. So for the previous out-shuffle of ```[a,b,c,d,1,2,3,4]``` we could just 
perform the in-shuffle of the segment ```[b,c,d,1,2,3]```. Therefore, all the implementations
just do a in-shuffle with some wrapper that handles shortening the segment if an out-shuffle 
is requested.

Folding is a term I denoted to represent an interleave that 'folds in the middle' such 
that the back of the last half is interleaved with the front of the first half until the 
middle is reached. The takeaway is that folding just reverses the order of the last half 
before the interleave process. It's an extra step that takes O(n/4) time but doesn't 
technically add to the theoretical runtime. Later I found reference to 'folding' being 
called a 'twist' in such that in a faro shuffle, one of the halves of the deck is 
twisted face up to shuffle the cards with half face down and half face up (for whatever
reason you would do this.) 

# Permutation Algorithm 
### by Peiyush Jain, Microsoft Corporation 2004 

This three page [paper](https://arxiv.org/abs/0805.1598) seemed to be popping up everywhere during my research
and lays out the basis for its O(n) status as a series of operations. Here is the main
algorithm for reference.

{% highlight java %}
protected void interleave(final Object[] array, int from, final int to) {
  while (to - from > 1) {
    final int size = to - from;
    if (size < 4) { // optimization for when there's not much left to do.
      Util.swap(array, from, from + 1);
      break;
    }

    final Constants c = Constants.from(size); // step 1
    if (c.m != c.n) {
      Util.rotate(array, from + c.m, from + c.m + c.n, c.m); // step 2
    }

    int _from = from;
    Setter<?> setter =  (i, obj) -> Util.set(array, _from + i, obj);

    for (int k = 0; k < c.k; k++) { // step 3
      cycleLeader(k, c.mod, array[from + Util.POW3[k] - 1], setter);
    }

    from += (2 * c.m);   // step 4
  }
}
{% endhighlight %}

### 1. Finding the highest $3^k$ elements to work on; takes O(log n)
- This is to prepare for the cycle leader permutation.
 {% highlight java %}
record Constants(int n, int k, int mod, int m){
  public static Constants from(int size){
    // Find a 2m = 3^k − 1 such that 3^k ≤ 2n < 3^(k+1)
    int n = size / 2;  // half the size of the entire collection
    int k = Util.ilog3(size); // minimum power of 3 elements to be working on
    int mod = Util.POW3[k]; // cycle modulus
    int m = (mod - 1) >> 1;  // m <= n, 2m is the number of elements moved at a time.
    return new Constants(n, k, mod, m);
  }
}
{% endhighlight %}
- So we're looking for the largest power of three that's less than the size of our 
  collection. From that we get an `m` which will determine how large each half
  will be.

### 2. Rotating the appropriate elements into the workspace; takes O(n)
- Since this algorithm only works on powers of 3 elements at a time, we need to shift 
 some elements out of the workspace. 
`Ex. [1,2,3,4,5,6, a,b,c,d,e,f]` The highest power of 3 is 9 which 
makes the previous constants: n=6, k=2, mod=9, and m=4, which is the number of elements
we'll need from each half. So we want to end up with ```[1,2,3,4, a,b,c,d, 5,6,e,f]```.
That is a cyclic rotation for the segment `[5,6,a,b,c,d]` of -2(left) or +4(right),
however you want to look at it, the work done is the same.

### 3. Perform a cycle leader algorithm for each **k** power of 3; takes O(m)
- So we're focused on the segment `[1,2,3,4,a,b,c,d]`. There are many permutations or
orderings of these elements possible, 8! or 40320. But we only want the permutation 
that looks like `[a,1,b,2,c,3,d,4]`
- What is a cycle leader algorithm? Often permutations are broken up into cycles such that
in order to get from state A to state B, you will have to cyclically swap a number of 
elements, a number of times.  

{% highlight java %}
// To get from  [1,2,3,4,a,b,c,d]  to  [a,1,b,2,c,3,d,4]
// Let's use indexes based at 1 instead of 0 to help see things.
// We move indexes (1->2->4->8->7->5->back to 1) 
//     and indexes (3->6->back to 3)
// These are our two cycles; (1,2,4,8,7,5)(3,6)
{% endhighlight %}

- The question then is how to systematically generate those cycles. 
Here is the cycle leader algorithm.  It juggles a leader value in
the air as it jumps from index position to index position in the cycle.

{% highlight java %}
private static <T> void cycleLeader(final int k, final int mod,
                                    final Object initialValue,
                                    final Setter<T> setter ){
  final long u64c = Long.divideUnsigned(-1L, mod) + 1L;
  final int startIdx = Util.POW3[k];
  int i = startIdx;
  T leader = (T)initialValue;
  do {
    i = Util.fastmod(i * 2, u64c, mod);  // i = (i * 2) % mod
    leader = setter.set(i - 1, leader);
  } while (i != startIdx);
}
{% endhighlight %}

- If you look at step 3 in the main algorithm you see the cycle leader is called `k`
times where k was the exponent of our highest power of three. And it's passed an 
initial value which is the index at some power of three (minus 1 because arrays are
zero based.) It then goes about doubling this index and taking the modulus to come
up with the next index in the cycle, passing it to a setter method that returns the
value that was that next index location to be used in the next iteration. 
- In our example, k=2 so we will have two cycles, one that starts at $3^0=1$ 
and $3^1=3$. Both of these starting indexes are members of the two cycles. 
    - Let's desk check the cycles 
    - `(2*1)%9=2, (2*2)%9=4, (2*4)%9=8, (2*8)%9=7, (2*7)%9=5, (2*5)%9=1` 
    - `(2*3)%9=6, (2*6)%9=3`
  

### 4. Repeat this procedure on the remaining elements that were rotated out; takes T(2(n-m))
- All that's left to do is push the `from` index up by `2m` to shrink the window
  and restart the process on the remaining elements... `[5,6,e,f]`

## Best Case, Worst Case And Reality
The best case for this algorithm is when the collection you are trying to interleave
has a size of $3^k - 1$. This eliminates any unnecessary rotations which is pure 
overhead. Unfortunately in a signed 32-bit integer, there are only twenty such perfect
collection sizes corresponding to powers of three.     

The worst case is when the collection size comes just under one of these perfect sizes 
which automatically ensures `2k` iterations of the main algorithm.

The reality though is that this algorithm performs very poorly at large N size because 
of pointer chasing which makes poor use of LL cache because of the large step sizes of
the cycles at large N. **It is** very fast and performs at O(n) for small N.


