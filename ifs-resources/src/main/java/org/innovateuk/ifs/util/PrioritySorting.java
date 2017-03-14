package org.innovateuk.ifs.util;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

/**
 * Generic class that provides a way of sorting a list of element of type T, except for a specified element.
 * @param <T> the type of the values in the list of be sorted
 */
public final class PrioritySorting<T> {
    private final List<T> sortedList;

    /**
     * Constructor
     * @param list - the list to be sorted by comparing the field of the elements
     * @param priority - the element that has sorting priority
     * @param field - function that defines what to compare between the elements of the list during sorting
     *              (eg Value::name, or identity to compare the values themselves)
     * @param <E> - the comparable type used to sort, returned by the field function.
     */
    public <E extends Comparable<? super E>> PrioritySorting(List<T> list, T priority, Function<T, E> field) {
        sortedList = list.stream().sorted((a, b)
                -> field.apply(a).equals(ofNullable(priority).map(field).orElse(null)) ? -1
                    : field.apply(b).equals(ofNullable(priority).map(field).orElse(null)) ? 1
                        : field.apply(a).compareTo(field.apply(b))).collect(toList());
    }

    public List<T> unwrap() {
        return sortedList;
    }

}