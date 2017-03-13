package org.innovateuk.ifs.organisation.resource;

import java.util.List;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class SortExcept<T> {
    private final List<T> sortedList;

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