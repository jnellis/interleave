package net.jnellis.interleave;

import java.util.List;

/**
 * Interleave two or more lists.  Performs an out-shuffle unless specified to
 * perform an in-shuffle. An out-shuffle keeps the first element of the first
 * list at the first position and the last element of the second list at the
 * last last position, assuming both lists are equal lengths.
 *
 * <pre>
 * {@code
 *     List<Integer> list = Arrays.asList(1,2,3,4,5,6);
 *     net.jnellis.interleave.Interleavers.interleave(list);
 *
 * }</pre>
 *
 * <a
 * href="https://cs.stackexchange.com/questions/332/in-place-algorithm-for-interleaving-an-array/105263#105263">https://cs.stackexchange.com/questions/332/in-place-algorithm-for-interleaving-an-array/105263#105263</a>
 */
public class Interleavers {
  private Interleavers(){}

//  public static <T> List<T> StitchedList(List<T>... lists) {
//    int[] sizeMap = new int[lists.length];
//    sizeMap[0] = 0;
//    for (int i = 1; i <= lists.length; i++) {
//      sizeMap[i] = lists[i].size() + sizeMap[i-1];
//    }
//
//    return new AbstractList<T>() {
//      @Override
//      public T get(int index) {
//        return b.get(getIndex(index));
//      }
//
//      private int getIndex(int index) {
//        return b.size() - 1 - index;
//      }
//
//      @Override
//      public T set(int index, T element) {
//        return b.set(getIndex(index), element);
//      }
//
//      @Override
//      public Iterator<T> iterator() {
//        return new ReverseListIterator<>(b);
//      }
//
//      @Override
//      public ListIterator<T> listIterator() {
//        return new ListIteratorWrapper<>(new ReverseListIterator<>(b));
//      }
//
//      @Override
//      public int size() {
//        return b.size();
//      }
//    };
//
//  }

  /**
   * Perform a memory efficient in-place interleaving of two lists.
   *
   * @param a         first list
   * @param b         second list
   * @param inShuffle if false, perform an out-shuffle, where the first and last
   *                  elements will be the first element of the first list and
   *                  the last element of the second list. If true, perform an
   *                  inshuffle, which starts with the second lists first
   *                  element.
   * @param <T>       type of element the list holds.
   */
  public static <T> void inPlaceInterleave(List<T> a,
                                           List<T> b,
                                           boolean inShuffle) {

  }

  /**
   * Perform a memory efficient in-place interleaving of the front and back half
   * of a list.
   *
   * @param a         list to be split and interleaved. If the list is odd
   *                  lengthed and the wish is to have full interleaving without
   *                  any elements remaining sequential then ensure that
   *                  inShuffle is false.
   * @param inShuffle if false, perform an out-shuffle, where the first and last
   *                  elements will be the first element of the first list and
   *                  the last element of the second list. If true, perform an
   *                  inshuffle, which starts with the second lists first
   *                  element.
   * @param <T>       type of element the list holds.
   */
  public static <T> void inPlaceInterleave(List<T> a, boolean inShuffle) {
    InPlaceInterleaver.interleave(a, inShuffle, false);
  }

  /**
   * Perform a memory efficient in-place interleaving of the front and back half
   * of a list.
   *
   * @param a          List to be interleaved.
   * @param splitIndex The index to split the list at for the second half of the
   *                   list. If the resulting two lengths are not equal then
   *                   there will be leftover sequential elements of the longer
   *                   segment.
   * @param inShuffle  if true, start the interleaving with the sublist starting
   *                   as splitIndex
   * @param <T>        type of element the list holds.
   */
  public static <T> void inPlaceInterleave(List<T> a,
                                           int splitIndex,
                                           boolean inShuffle) {

  }

  /**
   * Interleave two lists into a new list.
   *
   * @param a         first list
   * @param b         second list
   * @param inShuffle if true, start the interleaving with the second list.
   *                  otherwise, perform an out-shuffle.
   * @param <T>       type of element the list holds.
   * @return A new list with references to elements of lists a and b
   * interleaved.
   */
  public static <T> List<T> interleave(List<T> a,
                                       List<T> b,
                                       boolean inShuffle) {
    return RotatingQueueInterleaver.interleave(a, b, inShuffle);
  }

  /**
   * Creates a new list from interleaving the elements of multiple lists.
   * The order is an out shuffling, where the first element comes from the
   * first list. If all lists are of the same size then the last element
   * will come from the last list.
   *
   * For lists that of unequal sizes, the interleaving continues placing the
   * next element from the next list with available elements.
   *
   * For maintaining the interleaving stride for uneven lists,
   * {@link RotatingQueueInterleaver#interleave(Object, List[])}   interleave(spacer,lists)}
   *
   * @param lists Lists to be interleaved
   * @return  new list of interleaved elements
   * @param <T>  type of element the list holds.
   */
  @SafeVarargs
  public static <T> List<T> interleave(List<T>... lists) {
    return RotatingQueueInterleaver.interleave(lists);
  }
  
  @SafeVarargs
  public static <T> List<T> interleave(T spacer, List<T>... lists){
    return RotatingQueueInterleaver.interleave(spacer, lists);
  }

  /**
   * Performs an out-shuffle interleave on the given list, in place, without
   * using extra space.
   *
   * @param list list to be interleaved with bottom half of itself
   * @return same list, for convenience.
   * @param <T>  type of element the list holds.
   */
  public static <T> List<T> interleave(List<T> list) {
    InPlaceInterleaver.interleave(list, false, false);
    return list;
  }
}
