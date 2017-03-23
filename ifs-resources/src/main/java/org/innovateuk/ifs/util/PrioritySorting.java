package org.innovateuk.ifs.util;

import java.util.List;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

/**
 * Generic class that provides a way of sorting a list of element of type T, except for a specified element.
 *
 * @param <T> the type of the values in the list of be sorted
 */
public final class PrioritySorting<T> {
    private static final int BEFORE = -1, AFTER = 1;
    private final List<T> sortedList;

    /**
     * Constructor
     *
     * @param list     - the list to be sorted by comparing the field of the elements
     * @param priority - the element that has sorting priority
     * @param field    - function that defines what to compare between the elements of the list during sorting
     *                 (eg Value::name, or identity to compare the values themselves)
     * @param <E>      - the comparable type used to sort, returned by the field function.
     */
    public <E extends Comparable<? super E>> PrioritySorting(List<T> list, T priority, Function<T, E> field) {
        sortedList = list.stream()
                .sorted((a, b) -> is(a, priority, field) ? BEFORE : is(b, priority, field) ? AFTER
                        : field.apply(a).compareTo(field.apply(b))).collect(toList());
    }

    private <E extends Comparable<? super E>> boolean is(T a, T b, Function<T, E> field) {
        return field.apply(a).equals(ofNullable(b).map(field).orElse(null));
    }

    public List<T> unwrap() {
        return sortedList;
    }

}