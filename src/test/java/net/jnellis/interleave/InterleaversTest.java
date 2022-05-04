package net.jnellis.interleave;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * User: Joe Nellis Date: 11/13/2021 Time: 9:01 PM
 */
@DisplayName("Interleaving")
class InterleaversTest {

	List<Integer> evens, odds;

	void initLists(int max) {
		odds = IntStream.iterate(1, x -> x + 2)
		                .limit(max)
		                .boxed()
		                .collect(Collectors.toList());
		evens = IntStream.iterate(2, x -> x + 2)
		                 .limit(max)
		                 .boxed()
		                 .collect(Collectors.toList());
	}

	@ParameterizedTest
	@ValueSource(ints={0,1,2,3,9,10,11,31,32,33,1_000_000,1_000_001 })
	void rotatingQueueInterleave(int max){
		initLists(max);
		List<Integer> result = RotatingQueueInterleaver.interleave(odds, evens, false);
//		System.out.println(result);
		verifySequential(result);
	}

	@ParameterizedTest
	@ValueSource(ints={0,1,2,3,9,10,11,32,1_000_000, 1_000_001})
	void interleave(int max) {
		initLists(max);
		odds.addAll(evens);
//		System.out.println(odds);
		InPlaceInterleaver.interleave(odds, false, false);
//		System.out.println(odds);
		verifySequential(odds);
	}

	void verifySequential(List<Integer> list) {
		list.stream()
		    .reduce(0, (i, j) -> {
			    assertEquals(i + 1, j);
			    return j;
		    });
	}

	@ParameterizedTest
	@ValueSource(ints={0,1,2,3,9,10,11,32,1_000_000, 1_000_001})
	void foldingInterleave(int max) {
		initLists(max);
		Collections.reverse(evens);
		odds.addAll(evens);
//		System.out.println(odds);
		InPlaceInterleaver.interleave(odds, false, true);
//		System.out.println(odds);
		verifySequential(odds);
	}

	@ParameterizedTest
	@ValueSource(ints={0,1,2,3,9,10,11,32,1_000_000, 1_000_001})
	void inShuffleInterleave(int max) {
		initLists(max);
		evens.addAll(odds);
//		System.out.println(evens);
		InPlaceInterleaver.interleave(evens, true, false);
//		System.out.println(evens);
		verifySequential(evens);
	}

	@ParameterizedTest
	@ValueSource(ints={0,1,2,3,9,10,11,32,1_000_000, 1_000_001})
	void foldingInShuffleInterleave(int max) {
		initLists(max);
		Collections.reverse(odds);
		evens.addAll(odds);
//		System.out.println(evens);
		InPlaceInterleaver.interleave(evens, true, true);
//		System.out.println(evens);
		verifySequential(evens);
	}

}