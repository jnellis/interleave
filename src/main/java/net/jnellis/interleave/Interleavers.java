package net.jnellis.interleave;

import org.apache.commons.collections4.iterators.ListIteratorWrapper;
import org.codehaus.groovy.runtime.ReverseListIterator;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Interleave two or more lists.  Performs an out-shuffle unless specified to perform
 * an in-shuffle. An out-shuffle keeps the first element of the first list at
 * the first position and the last element of the second list at the last
 * last position, assuming both lists are equal lengths.
 *
 * <pre>
 * {@code
 *     List<Integer> list = Arrays.asList(1,2,3,4,5,6);
 *     net.jnellis.interleave.Interleavers.interleave(list);
 *
 * }</pre>
 *
 * <a href="https://cs.stackexchange.com/questions/332/in-place-algorithm-for-interleaving-an-array/105263#105263">https://cs.stackexchange.com/questions/332/in-place-algorithm-for-interleaving-an-array/105263#105263</a>
 */
public class Interleavers {

	public static <T> List<T> reverseAccessList(List<T> b) {
		return  new AbstractList<T>() {

							@Override
							public T get(int index) {
								return b.get(getIndex(index));
							}

							private int getIndex(int index) {
								return b.size() - 1 - index;
							}

							@Override
							public T set(int index, T element) {
								return b.set(getIndex(index), element);
							}

							@Override
							public Iterator<T> iterator() {
								return new ReverseListIterator<>(b);
							}

							@Override
							public ListIterator<T> listIterator() {
								return new ListIteratorWrapper<>(new ReverseListIterator<>(b));
							}

							@Override
							public int size() {
								return b.size();
							}
						};

	}

	/**
	 * Perform a memory efficient in-place interleaving of two lists.
	 * @param a first list
	 * @param b second list
	 * @param inShuffle if false, perform an out-shuffle, where the first and last
	 *                elements will be the first element of the first list and
	 *                the last element of the second list. If true, perform an
	 *                inshuffle, which starts with the second lists first element.
	 *
	 *   
	 */
	public static <T> void inPlaceInterleave(List<T> a, List<T> b, boolean inShuffle){

	}

	/**
	 * Perform a memory efficient in-place interleaving of the front and back half
	 * of a list.
	 * @param a list to be split and interleaved. If the list is odd lengthed
	 *          and the wish is to have full interleaving without any elements
	 *          remaining sequential then ensure that inShuffle is false.
	 * @param inShuffle if false, perform an out-shuffle, where the first and last
	 *                elements will be the first element of the first list and
	 *                the last element of the second list. If true, perform an
	 *                inshuffle, which starts with the second lists first element.
	 *
	 */
	public static <T> void inPlaceInterleave(List<T> a, boolean inShuffle){

	}

	/**
	 * Perform a memory efficient in-place interleaving of the front and back
	 * half of a list.
	 * @param a List to be interleaved.
	 * @param splitIndex The index to split the list at for the second half of the
	 *                   list. If the resulting two lengths are not equal then
	 *                   there will be leftover sequential elements of the longer
	 *                   segment.
	 * @param inShuffle if true, start the interleaving with the sublist starting
	 *                  as splitIndex
	 */
	public static <T> void inPlaceInterleave(List<T> a, int splitIndex, boolean inShuffle){

	}

	/**
	 * Interleave two lists into a new list.
	 * @param a first list
	 * @param b second list
	 * @param inShuffle if true, start the interleaving with the second list.
	 *
	 * @return A new list with references to elements of lists a and b interleaved.
	 */
	public static <T> List<T> interleave(List<T> a, List<T> b, boolean inShuffle){
		return new ArrayList<>();
	}

	@SafeVarargs
	public static <T> List<T> interleave(List<T>... lists){
		return new ArrayList<>();
	}

	public static <T> List<T> interleave(List<T> list){
		return new ArrayList<>();
	}
}
