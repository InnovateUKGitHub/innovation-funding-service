package org.innovateuk.ifs.util;

import java.util.List;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Generic class that provides a way of sorting a list of element of type T, except for a specified element.
 * @param <T> the type of the values in the list of be sorted
 */
public final class SortExcept<T> {
    private final List<T> sortedList;

    /**
     * Constructor
     * @param list - the list to be sorted by comparing the field of the elements
     * @param exception - the element that is not be sorted as the others but to be added at the head of the list.
     * @param field - function that defines what to compare between the elements of the list during sorting
     *              (eg Value::name, or identity to compare the values themselves)
     * @param <E> - the comparable type used to sort, returned by the field function.
     */
    public <E extends Comparable<? super E>> SortExcept(List<T> list, T exception, Function<T, E> field) {
        sortedList = list.stream()
                .filter(a -> !field.apply(a).equals(field.apply(exception)))
                .sorted(comparing(field)).collect(toList());
        sortedList.add(0, exception);
    }

    public List<T> unwrap() {
        return sortedList;
    }
}