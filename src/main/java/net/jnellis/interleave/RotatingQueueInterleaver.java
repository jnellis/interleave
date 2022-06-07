package net.jnellis.interleave;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;

/**
 * User: Joe Nellis Date: 11/6/2021 Time: 9:01 PM
 */
public class RotatingQueueInterleaver {

  private RotatingQueueInterleaver(){}

  /**
   * todo
   * @param a list a
   * @param b      list b
   * @param shuffle if true, perform an in-shuffle, otherwise performa out-shuffle
   * @return new list of intereleaved elements
   * @param <T>  type of element the list holds.
   */
  public static <T> List<T> interleave(List<T> a, List<T> b, boolean shuffle) {

    if (b.size() > a.size() || (a.size() == b.size() && shuffle)) {
      List<T> temp = a;
      a = b;
      b = temp;
    }
    int num_items = a.size() + b.size();
    final List<T> result = new ArrayList<>(num_items);
    final Deque<ListIterator<T>> iterators = new ArrayDeque<>();
    // put list iterators in a queue to be pulled and rotated back in.
    Arrays.asList(a, b)
          .forEach(l -> {
            if (!l.isEmpty())
              iterators.push(l.listIterator());
          });

    // when all list iterators have reached the end, then stop.
    while (!iterators.isEmpty()) {
      ListIterator<T> list = iterators.pollLast();
      result.add(list.next());
      // only put the list iterator back in the queue if it still has elements
      if (list.hasNext()) {
        iterators.push(list);
      }
    }
    return result;
  }

  /**
   * Interleave any number of lists into a new list. Does not affect original
   * lists.
   *
   * Lists do not have to be the same length. It will continue to visit
   * each remaining list, pulling the next element and placing it into the
   * new list, until all lists have been processed.
   *
   * @param lists List of lists to interleave together
   *               @param <T>  type of element the list holds.
   * @return A new list of interleaved elements
   */
  @SafeVarargs
  public static <T> List<T> interleave(final List<T>... lists) {
    int numItems = 0;
    for (List<T> list : lists) {
      numItems += list.size();
    }
    final List<T> result = new ArrayList<>();
    final Deque<ListIterator<T>> iterators = new ArrayDeque<>();
    Arrays.asList(lists)
          .forEach(l -> {
            if (!l.isEmpty())
              iterators.push(l.listIterator());
          });
    // when all list iterators have reached the end, then stop.
    while (!iterators.isEmpty()) {
      ListIterator<T> list = iterators.pollLast();
      result.add(list.next());
      // only put the list iterator back in the queue if it still has elements
      if (list.hasNext()) {
        iterators.push(list);
      }
    }
    return result;
  }

  /**
   * Interleave any number of lists into a new list. Does not affect original
   * lists.
   *
   * Lists do not have to be the same length. It will continue to visit
   * each remaining list, pulling the next element and placing it into the
   * new list, until all lists have been processed.
   *
   * @param spacer a spacer element to place in lieu of missing element
   * @param lists List of lists to interleave together
   *               @param <T>  type of element the list holds.
   * @return A new list of interleaved elements
   */
  @SafeVarargs
  public static <T> List<T> interleave(T spacer, final List<T>... lists){

    return Collections.emptyList();
  }
}
