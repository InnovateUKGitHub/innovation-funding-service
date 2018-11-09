package org.innovateuk.ifs.util;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Optional.empty;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.range;

/**
 * Utility class to provide useful reusable Functions around Collections throughout the codebase
 */
public final class CollectionFunctions {

    private CollectionFunctions() {
    }

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(CollectionFunctions.class);


    /**
     * Flatten the given 2-dimensional {@link Collection} into a 1-dimensional List
     *
     * @param toFlatten
     * @param <T>
     * @return 1-dimensional list
     */
    public static <T> List<T> flattenLists(Collection<? extends Collection<T>> toFlatten){
        return flattenLists(toFlatten, Function.identity());
    }

    public static <S, T> List<T> flattenLists(Collection<S> toFlatten, Function<S, ? extends Collection<T>> mapper) {
        return toFlatten.stream().filter(Objects::nonNull).map(mapper).flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * Flatten the given 2-dimensional Set into a 1-dimensional Set
     *
     * @param lists
     * @param <T>
     * @return 1-dimensional list
     */
    public static <T> Set<T> flattenSets(Set<Set<T>> lists) {
        return lists.stream()
                .filter(l -> l != null)
                .flatMap(Collection::stream)
                .collect(toSet());
    }

    /**
     * Combine the given Lists into a single List
     *
     * @param lists
     * @param <T>
     * @return combined List containing the elements of the given Lists, in the original list order
     */
    @SafeVarargs
    public static <T> List<T> combineLists(List<T>... lists) {
        return doCombineLists(lists);
    }

    public static <T> Predicate<T> and(Predicate<? super T>... predicates){
        return t -> {
            for (Predicate<? super T> predicate : predicates){
                if (!predicate.test(t)){
                    return false;
                }
            }
            return true;
        };
    }

    private static <T> List<T> doCombineLists(List<T>... lists) {
        return flattenLists(Arrays.asList(lists));
    }

    /**
     * Combine the given List and varargs array into a single List
     *
     * @param list
     * @param otherElements
     * @param <T>
     * @return combined List containing the elements of the given Lists, in the original list order
     */
    @SafeVarargs
    public static <T> List<T> combineLists(List<T> list, T... otherElements) {
        List<T> startingList = new ArrayList<>();

        if (list != null && !list.isEmpty()) {
            startingList.addAll(list);
        }

        return doCombineLists(startingList, asList(otherElements));
    }

    /**
     * Combine the given element and varargs array into a single List
     *
     * @param firstElement
     * @param otherElements
     * @param <T>
     * @return combined List containing the elements of the given Lists, in the original list order
     */
    @SafeVarargs
    public static <T> List<T> combineLists(T firstElement, T... otherElements) {
        return doCombineLists(singletonList(firstElement), asList(otherElements));
    }

    /**
     * Combine the given element and list into a single List
     *
     * @param firstElement
     * @param otherElements
     * @param <T>
     * @return combined List containing the elements of the given Lists, in the original list order
     */
    public static <T> List<T> combineLists(T firstElement, List<T> otherElements) {
        return doCombineLists(singletonList(firstElement), otherElements);
    }


    /**
     * Provides a forEach method as per the default List.forEach() method, but with the addition of having an index
     * provided as well
     *
     * @param consumer
     * @param <T>
     * @return a Consumer that can then be applied to Lists to apply the consumer function provided
     */
    public static <T> Consumer<List<T>> forEachConsumer(BiConsumer<Integer, T> consumer) {
        return list -> IntStream.range(0, list.size()).forEach(i -> consumer.accept(i, list.get(i)));
    }

    /**
     * Provides a forEach method as per the default List.forEach() method, but with the addition of having an index
     * provided as well
     *
     * @param consumer
     * @param <T>
     */
    public static <T> void forEachWithIndex(List<T> list, BiConsumer<Integer, T> consumer) {
        forEachConsumer(consumer).accept(list);
    }

    /**
     * Given 2 Lists, this method will iterate through both lists, presenting the consumer with the equivalent elements
     * in from each list at index 0, 1, 2 etc
     *
     * @param consumer
     * @param <T>
     */
    public static <T, S> void zip(List<T> list1, List<S> list2, BiConsumer<T, S> consumer) {
        forEachWithIndex(list1, (i, item1) -> consumer.accept(item1, list2.get(i)));
    }

    /**
     * Given 2 Lists, this method will iterate through both lists, presenting the consumer with the equivalent elements
     * in from each list at index 0, 1, 2 etc
     *
     * @param consumer
     * @param <T>
     */
    public static <T, S> void zipWithIndex(List<T> list1, List<S> list2, TriConsumer<T, S, Integer> consumer) {
        forEachWithIndex(list1, (i, item1) -> consumer.accept(item1, list2.get(i), i));
    }

    /**
     * Given 2 Lists, this method will iterate through both lists, presenting the consumer with the equivalent elements
     * in from each list at index 0, 1, 2 etc
     *
     * @param biFunction
     * @param <T>
     */
    public static <T, R, S> List<R> zipAndMap(List<T> list1, List<S> list2, BiFunction<T, S, R> biFunction) {
        return zipAndMapWithIndex(list1, list2, (o1, o2, i) -> biFunction.apply(o1, o2));
    }

    /**
     * Given 2 Lists, this method will iterate through both lists, presenting the consumer with the equivalent elements
     * in from each list at index 0, 1, 2 etc
     *
     * @param triFunction
     * @param <T>
     */
    public static <T, R, S> List<R> zipAndMapWithIndex(List<T> list1, List<S> list2, TriFunction<T, S, Integer, R> triFunction) {

        List<R> resultList = new ArrayList<>();
        zipWithIndex(list1, list2, (o1, o2, index) -> resultList.add(triFunction.apply(o1, o2, index)));
        return resultList;
    }

    /**
     * Provides a forEach method as per the default List.forEach() method, but with the addition of having an index
     * provided as well and the ability to return values
     *
     * @param function
     * @param <T>
     * @return a Function that can then be applied to Lists to apply the function provided in order to produce a result
     */
    public static <T, R> Function<List<T>, List<R>> forEachFunction(BiFunction<Integer, T, R> function) {
        return list -> IntStream.range(0, list.size()).mapToObj(i -> function.apply(i, list.get(i))).collect(toList());
    }

    /**
     * Provides a forEach method as per the default List.forEach() method, but with the addition of having an index
     * provided as well and the ability to return values
     *
     * @param function
     * @param <T>
     * @return a Function that can then be applied to Lists to apply the function provided in order to produce a result
     */
    public static <T, R> List<R> mapWithIndex(List<T> list, BiFunction<Integer, T, R> function) {
        return forEachFunction(function).apply(list);
    }

    /**
     * Returns a new List with the original list's contents reversed.  Leaves the original list intact.  Returns
     * an empty list if a null or empty list is supplied.
     *
     * @param original
     * @param <T>
     * @return
     */
    public static <T> List<T> reverse(List<T> original) {
        List<T> reversed = original != null ? new ArrayList<>(original) : new ArrayList<>();
        Collections.reverse(reversed);
        return reversed;
    }

    /**
     * Return the one and only element in the given list, otherwise throw an IllegalArgumentException
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T getOnlyElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("No elements were available in list " + list + ", so cannot return only element");
        }

        if (list.size() > 1) {
            throw new IllegalArgumentException("More than one element was available in list " + list + ", so cannot return only element");
        }

        return list.get(0);
    }

    /**
     * Return the one and only element in the given list, or empty if none exists
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> Optional<T> getOnlyElementOrEmpty(List<T> list) {
        if (list == null || list.isEmpty()) {
            return empty();
        }

        if (list.size() > 1) {
            throw new IllegalArgumentException("More than one element was available in list " + list + ", so cannot return only element");
        }

        return Optional.of(list.get(0));
    }

    public static <T, R> R[] simpleMapArray(T[] array, Function<T, R> mappingFn, Class<R> clazz) {
        if (array == null || array.length == 0){
            return (R[]) Array.newInstance(clazz, 0);
        } else {
            R[] result = (R[]) Array.newInstance(clazz, array.length);
            for (int index = 0; index < array.length; index++){
                result[index] = mappingFn.apply(array[index]);
            }
            return result;
        }


    }

    /**
     * A simple wrapper around a 1-stage mapping function, to remove boilerplate from production code
     */
    public static <S, T, R> List<R> simpleMap(Map<S, T> map, BiFunction<S, T, R> mappingFn) {
        return simpleMap(map.entrySet(), entry -> mappingFn.apply(entry.getKey(), entry.getValue()));
    }

    /**
     * A simple wrapper around a 1-stage mapping function, to remove boilerplate from production code
     *
     * @param list
     * @param mappingFn
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> simpleMap(T[] list, Function<T, R> mappingFn) {
        return simpleMap(asList(list), mappingFn);
    }

    /**
     * A simple wrapper around a 1-stage mapping function, to remove boilerplate from production code
     *
     * Unlike {@link CollectionFunctions#simpleMap}, this method also provides the index of the item being mapped.
     *
     * @param list
     * @param mappingFn
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> simpleMapWithIndex(T[] list, BiFunction<T, Integer, R> mappingFn) {
        return simpleMapWithIndex(asList(list), mappingFn);
    }

    public static <T, R> Map<T, R> simpleFilter(Map<T, R> map, Predicate<T> filterFn) {
        return  simpleFilter(map, (k,v) -> filterFn.test(k));
    }

    public static <T, R> Map<T, R> simpleFilterNot(Map<T, R> map, Predicate<T> filterFn) {
        return  simpleFilter(map, filterFn.negate());
    }

    public static <T, R, S> Map<S, Map<T,R>> simpleGroupBy(Map<T, R> map, Function<T, S> filterFn) {
        Map<S, List<Entry<T, R>>> intermediate = map.entrySet().stream().collect(groupingBy(e -> filterFn.apply(e.getKey())));
        Map<S, Map<T, R>> result = simpleMapKeyAndValue(intermediate, identity(), value -> simpleToMap(value, Entry::getKey, Entry::getValue));
        return result;
    }

    /**
     * A simple wrapper around a 1-stage mapping function, to remove boilerplate from production code
     *
     * @param map
     * @param filterFn
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> Map<T, R> simpleFilter(Map<T, R> map, BiPredicate<T, R> filterFn) {
        return map.entrySet().stream().filter(entry -> filterFn.test(entry.getKey(), entry.getValue())).collect(
                toMap(Entry::getKey, Entry::getValue));
    }

    /**
     * A simple wrapper around a 1-stage mapping function, to remove boilerplate from production code
     */
    public static <S, T, R, U> Map<R, U> simpleMapKeyAndValue(Map<S, T> map, Function<S, R> keyMappingFn, Function<T, U> valueMappingFn) {

        List<Pair<R, U>> list = simpleMap(map.entrySet(), entry ->
                Pair.of(keyMappingFn.apply(entry.getKey()), valueMappingFn.apply(entry.getValue())));

        return simpleToMap(list, Pair::getKey, Pair::getValue);
    }

    /**
     * A simple wrapper, producing a Linked Hash Map, around a 1-stage mapping function, to remove boilerplate from production code
     */
    public static <S, T, R, U> Map<R, U> simpleLinkedMapKeyAndValue(Map<S, T> map, Function<S, R> keyMappingFn, Function<T, U> valueMappingFn) {

        List<Pair<R, U>> list = simpleMap(map.entrySet(), entry ->
                Pair.of(keyMappingFn.apply(entry.getKey()), valueMappingFn.apply(entry.getValue())));

        return simpleToLinkedMap(list, Pair::getKey, Pair::getValue);
    }

    public static <S, T, R, U> Map<R, U> simpleMapEntry(Map<S, T> map, Function<Entry<S, T>, R> keyMapping, Function<Entry<S,T>, U> valueMapping) {
        Map<R, U> result = map.entrySet().stream().collect(toMap(keyMapping, valueMapping));
        return result;
    }

    /**
     * A simple wrapper around a 1-stage mapping function, to remove boilerplate from production code
     */
    public static <S, T, R> Map<R, T> simpleMapKey(Map<S, T> map, Function<S, R> mappingFn) {
        return simpleMapKeyAndValue(map, mappingFn, value -> value);
    }

    /**
     * A simple wrapper, producing a Linked Hash Map, around a 1-stage mapping function, to remove boilerplate from production code
     */
    public static <S, T, R> Map<R, T> simpleLinkedMapKey(Map<S, T> map, Function<S, R> mappingFn) {
        return simpleLinkedMapKeyAndValue(map, mappingFn, value -> value);
    }

    /**
     * A simple wrapper around a 1-stage mapping function, to remove boilerplate from production code
     */
    public static <S, T, R> Map<S, R> simpleMapValue(Map<S, T> map, Function<T, R> mappingFn) {
        return simpleMapKeyAndValue(map, key -> key, mappingFn);
    }

    /**
     * A simple wrapper, producing a Linked Hash Map, around a 1-stage mapping function, to remove boilerplate from production code
     */
    public static <S, T, R> Map<S, R> simpleLinkedMapValue(Map<S, T> map, Function<T, R> mappingFn) {
        return simpleLinkedMapKeyAndValue(map, key -> key, mappingFn);
    }

    /**
     * A simple wrapper around a 1-stage mapping function, to remove boilerplate from production code
     */
    public static <T, R> Set<R> simpleMapSet(Set<T> list, Function<T, R> mappingFn) {
        if (null == list || list.isEmpty()) {
            return emptySet();
        }
        return list.stream().map(mappingFn).collect(toSet());
    }

    /**
     * A simple wrapper around a 1-stage mapping function, to remove boilerplate from production code.
     * The resultant collection is a Set which means that duplicates from the input list will be removed.
     */
    public static <T, R> Set<R> simpleMapSet(List<T> list, Function<T, R> mappingFn) {
        if (null == list || list.isEmpty()) {
            return emptySet();
        }
        return list.stream().map(mappingFn).collect(toSet());
    }

    /**
     * Map over an array of values, collecting the results into a {@link Set}.
     */
    public static <T, R> Set<R> simpleMapSet(T[] array, Function<T, R> mappingFn) {
        if (null == array || array.length == 0) {
            return emptySet();
        }

        return newHashSet(simpleMap(asList(array), mappingFn));
    }

    /**
     * A simple wrapper around a 1-stage mapping function, to remove boilerplate from production code
     *
     * @param collection
     * @param mappingFn
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> simpleMap(Collection<T> collection, Function<T, R> mappingFn) {
        if (collection == null || collection.isEmpty()) {
            return emptyList();
        }
        return collection.stream().map(mappingFn).collect(toList());
    }


    /**
     * A simple wrapper around a 1-stage mapping function, to remove boilerplate from production code.
     *
     * Unlike {@link CollectionFunctions#simpleMap}, this method also provides the index of the item being
     * mapped.
     *
     * @param collection
     * @param mappingFn
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> List<R> simpleMapWithIndex(Collection<T> collection, BiFunction<T, Integer, R> mappingFn) {
        if (collection == null || collection.isEmpty()) {
            return emptyList();
        }

        List<T> asList = new ArrayList<>(collection);

        return IntStream.range(0, collection.size()).
                mapToObj(i -> mappingFn.apply(asList.get(i), i)).
                collect(toList());
    }

    /**
     * A shortcut to sort a list by natural order
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T extends Comparable> List<T> sort(Collection<T> list) {
        if (list == null || list.isEmpty()) {
            return emptyList();
        }

        List<T> sorted = new ArrayList<>(list);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * A shortcut to sort a list by natural order
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> List<T> sort(Collection<T> list, Comparator<T> comparator) {
        if (list == null || list.isEmpty()) {
            return emptyList();
        }

        List<T> sorted = new ArrayList<>(list);
        sorted.sort(comparator);
        return sorted;
    }

    /**
     * A andOnSuccess collector that preserves order
     *
     * @param keyMapper
     * @param valueMapper
     * @param <T>
     * @param <K>
     * @param <U>
     * @return
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {
        return toMap(keyMapper,
                valueMapper,
                (u, v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); },
                LinkedHashMap::new);
    }

    /**
     * A simple function to convert a list of items to a Map given a key generating function and a value generating function
     *
     * @param keyMapper
     * @param valueMapper
     * @param <T>
     * @param <K>
     * @param <U>
     * @return
     */
    public static <T, K, U> Map<K, U> simpleToMap(
            Collection<T> list,
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {

        if (list == null || list.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<K,U> map = new HashMap<K,U>();
        list.forEach(item -> map.put(keyMapper.apply(item), valueMapper.apply(item)));
        return map;
    }

    /**
     * A simple function to convert a list of items to a Map given a key generating function and a value generating function
     *
     * @param keyMapper
     * @param valueMapper
     * @param <T>
     * @param <K>
     * @param <U>
     * @return
     */
    public static <T, K, U> Map<K, List<U>> simpleToMultiMap(
            Collection<T> list,
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {

        if (list == null || list.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<K,List<U>> map = new HashMap<>();
        list.forEach(item -> {

            K key = keyMapper.apply(item);
            U value = valueMapper.apply(item);
            List<U> valueList = map.get(key);
            if (valueList == null) {
                valueList = new ArrayList<>();
                map.put(key, valueList);
            }
            valueList.add(value);
        });
        return map;
    }

    /**
     * A simple function to convert a list of items to a Linked Hash Map given a key generating function and a value generating function
     *
     * @param keyMapper
     * @param valueMapper
     * @param <T>
     * @param <K>
     * @param <U>
     * @return
     */
    public static <T, K, U> Map<K, U> simpleToLinkedMap(
            List<T> list,
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper) {

        if (list == null || list.isEmpty()) {
            return Collections.emptyMap();
        }

        return list.stream().collect(toMap(keyMapper, valueMapper, throwingMerger(), LinkedHashMap::new));
    }

    /**
     * A simple function to convert an array of items to a LinkedHashSet with the given mapping function.
     */
    public static <T, R> LinkedHashSet<R> simpleToLinkedHashSet(T[] array,
                                                                Function<T, R> mappingFn) {
        if (array == null) {
            return new LinkedHashSet<>();
        }
        return simpleToLinkedHashSet(asList(array), mappingFn);
    }

    /**
     * A simple function to convert a collection of items to a LinkedHashSet with the given mapping function.
     */
    public static <T, R> LinkedHashSet<R> simpleToLinkedHashSet(Collection<T> collection,
                                                                Function<T, R> mappingFn) {
        if (collection == null || collection.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return collection.stream().map(mappingFn).collect(toCollection(LinkedHashSet::new));
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }

    /**
     * A simple function to convert a list of items to a Map of keys against the original elements themselves, given a key generating function
     *
     * @param keyMapper
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> Map<K, T> simpleToMap(
            List<T> list,
            Function<? super T, ? extends K> keyMapper) {

        return simpleToMap(list, keyMapper, identity());
    }

    /**
     * A collector that maps a collection of pairs into a andOnSuccess.
     *
     * @param <R>
     * @param <T>
     * @return
     */
    public static <R, T> Map<R, T> pairsToMap(List<Pair<R, T>> pairs) {
        return simpleToMap(pairs, Pair::getKey, Pair::getValue);
    }

    /**
     * A collector that maps a collection of pairs into a andOnSuccess.
     *
     * @param <R>
     * @param <T>
     * @return
     */
    public static <R, T> Collector<Pair<R, T>, ?, Map<R, T>> pairsToMapCollector() {
        return toMap(Pair::getLeft, Pair::getRight);
    }

    /**
     * Function that takes a andOnSuccess entry and returns the value. Useful for collecting over collections of entry sets
     *
     * @param <R>
     * @param <T>
     * @return
     */
    public static <R, T> Function<Entry<R, T>, T> mapEntryValue() {
        return Entry::getValue;
    }

    /**
     * A simple wrapper around a 1-stage filter function, to remove boilerplate from production code
     *
     * @param list
     * @param filterFn
     * @param <T>
     * @return
     */
    public static <T> List<T> simpleFilter(Collection<? extends T> list, Predicate<T> filterFn) {
        if (list == null || list.isEmpty()) {
            return emptyList();
        }
        return list.stream().filter(filterFn).collect(toList());
    }

    /**
     * A simple wrapper around a 1-stage filter function, to remove boilerplate from production code
     *
     * @param array
     * @param filterFn
     * @param <T>
     * @return
     */
    public static <T> List<T> simpleFilter(T[] array, Predicate<T> filterFn) {
        if (array == null) {
            return emptyList();
        }
        return simpleFilter(asList(array), filterFn);
    }

    /**
     * A simple wrapper around a NEGATED 1-stage filter function, to remove boilerplate from production code
     *
     * @param list
     * @param filterFn
     * @param <T>
     * @return
     */
    public static <T> List<T> simpleFilterNot(Collection<? extends T> list, Predicate<T> filterFn) {
        return simpleFilter(list, element -> !filterFn.test(element));
    }

    /**
     * A simple wrapper around a 1-stage filter function, to remove boilerplate from production code
     *
     * @param list
     * @param filterFn
     * @param <T>
     * @return
     */
    public static <T> Optional<T> simpleFindFirst(Collection<T> list, Predicate<T> filterFn) {
        if (list == null || list.isEmpty()) {
            return empty();
        }
        return list.stream().filter(filterFn).findFirst();
    }

    /**
     * A simple wrapper around a 1-stage find-first function that takes an array rather than a Collection, to remove
     * boilerplate from production code
     *
     * @param array
     * @param filterFn
     * @param <T>
     * @return
     */
    public static <T> Optional<T> simpleFindFirst(T[] array, Predicate<T> filterFn) {
        if (array == null) {
            return empty();
        }
        return simpleFindFirst(asList(array), filterFn);
    }

    /**
     * A simple wrapper around a 1-stage filter function, to remove boilerplate from production code
     *
     * @param list
     * @param filterFn
     * @param <T>
     * @return
     */
    public static <T> T simpleFindFirstMandatory(Collection<T> list, Predicate<T> filterFn) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Cannot find a mandatory matching value in an empty list");
        }
        return simpleFindFirst(list, filterFn).orElseThrow(() ->
                new IllegalArgumentException("Was unable to find a matching mandatory result in list"));
    }

    /**
     * A simple wrapper around an anyMatch function, to remove boilerplate from production code
     *
     * @param list
     * @param filterFn
     * @param <T>
     * @return
     */
    public static <T> boolean simpleAnyMatch(Collection<T> list, Predicate<T> filterFn) {
        if (list == null || list.isEmpty()) {
            return false;
        }
        return list.stream().anyMatch(filterFn);
    }

    /**
     * A simple wrapper around an anyMatch function that takes an array, to remove boilerplate from production code
     *
     * @param array
     * @param filterFn
     * @param <T>
     * @return
     */
    public static <T> boolean simpleAnyMatch(T[] array, Predicate<T> filterFn) {

        if (array == null || array.length == 0) {
            return false;
        }

        return simpleAnyMatch(asList(array), filterFn);
    }

    /**
     * A simple wrapper around a String joining function.  Returns a string of the given list, separated by the given
     * joinString
     *
     * @param list
     * @param joinString
     * @param <T>
     * @return
     */
    public static <T> String simpleJoiner(Collection<T> list, String joinString) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream().map(element -> element != null ? element.toString() : "").collect(joining(joinString));
    }

    public static <T> String simpleJoiner(List<T> list, Function<T, String> transformer, String joinString) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return simpleJoiner(list.stream().map(transformer).collect(toList()), joinString);
    }

    /**
     * Return the one and only element from the list supplied, or throw an IllegalArgumentException
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T onlyElement(List<T> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        throw new IllegalArgumentException("Only expected a single element, but found " + list.size() + " - " + list);
    }

    /**
     * Return the one and only element from the list supplied, or return null
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T onlyElementOrNull(List<T> list) {
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    /**
     * Convert the given varargs to a LinkedHashSet
     *
     * @param items
     * @param <T>
     * @return
     */
    public static <T> Set<T> asLinkedSet(T... items) {
        LinkedHashSet<T> set = new LinkedHashSet<>();
        asList(items).forEach(set::add);
        return set;
    }

    /**
     * Given a list, this method will return a new list with the element at the given index removed
     *
     * @param list
     * @param index
     * @param <T>
     * @return
     */
    public static <T> List<T> removeElement(List<T> list, int index) {
        List<T> copy = new ArrayList<>(list);
        copy.remove(index);
        return copy;
    }

    /**
     * Given a list, this method will remove any duplicate entries from it and return the list in the same order as it was given
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> List<T> removeDuplicates(Collection<T> list) {
        if (list == null) {
            return emptyList();
        }
        return list.stream().distinct().collect(toList());
    }

    /**
     * Given a list, this method will remove any duplicate entries from it and return the list in the same order as it was given
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> Set<T> removeDuplicates(Set<T> list) {
        if (list == null) {
            return emptySet();
        }
        return list.stream().distinct().collect(toSet());
    }

    /**
     * Partitions a list based upon a given predicate, and returns the two lists in a Pair, with the "true" list being the
     * key (left) and the "false" list being the value (right)
     *
     * @param list
     * @param test
     * @param <T>
     * @return
     */
    public static <T> Pair<List<T>, List<T>> simplePartition(List<T> list, Predicate<T> test) {

        if (list == null || list.isEmpty()) {
            return Pair.of(emptyList(), emptyList());
        }

        Map<Boolean, List<T>> partitioned = list.stream().collect(partitioningBy(test));
        return Pair.of(partitioned.get(true), partitioned.get(false));
    }

    /**
     * Wrap a {@link BinaryOperator} with null checks
     *
     * @param notNullSafe
     * @param <T>
     * @return
     */
    public static <T> BinaryOperator<T> nullSafe(final BinaryOperator<T> notNullSafe) {
        return (t1, t2) -> {
            if (t1 != null && t2 != null) {
                return notNullSafe.apply(t1, t2);
            } else if (t1 != null) {
                return t1;
            } else if (t2 != null) {
                return t2;
            }
            return null;
        };
    }

    /**
     * Given a list of elements, this method will find all possible permutations of those elements.
     * <p>
     * E.g. given (1, 2, 3), possible permutations are (1, 2, 3), (2, 1, 3), (3, 1, 2) etc...
     *
     * @param excludedWords
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> findPermutations(List<T> excludedWords) {
        List<List<List<T>>> allPermutations = mapWithIndex(excludedWords, (i, currentWord) -> findPermutations(emptyList(), currentWord, removeElement(excludedWords, i)));
        return flattenLists(allPermutations);
    }

    public static <T, R> List<Pair<T, R>> asListOfPairs(Object... entries) {

        if (entries.length % 2 != 0) {
            throw new IllegalArgumentException("Should have an even number of names and values in list");
        }

        List<Pair<T, R>> list = new ArrayList<>();

        for (int i = 0; i < entries.length; i += 2) {
            T key = (T) entries[i];
            R value = (R) entries[i + 1];
            list.add(Pair.of(key, value));
        }

        return list;
    }

    private static <T> List<List<T>> findPermutations(List<T> permutationStringSoFar, T currentWord, List<T> remainingWords) {

        List<T> newPermutationStringSoFar = combineLists(permutationStringSoFar, currentWord);

        if (remainingWords.isEmpty()) {
            return singletonList(newPermutationStringSoFar);
        }

        List<List<List<T>>> furtherPermutations = mapWithIndex(remainingWords, (i, remainingWord) -> findPermutations(newPermutationStringSoFar, remainingWord, removeElement(remainingWords, i)));
        return flattenLists(furtherPermutations);
    }

    public static final <T, R> boolean containsAll(Collection<T> containing, Collection<R> contained, BiFunction<T,R,Boolean> equalsFunction){
        if (containing == null && contained != null) {
            return false;
        } else if (contained == null) {
            return true;
        }
        boolean notContained = contained.stream().filter(containedItem ->
                    !containing.stream().filter(containingItem -> equalsFunction.apply(containingItem, containedItem)).findAny().isPresent()
        ).findAny().isPresent();
        return !notContained;
    }

    public static final <R, S, T> boolean containsAll(Collection<T> containing, Function<T, S> transformer1, Collection<R> contained, Function<R, S> transformer2) {
        if (containing == null && contained != null) {
            return false;
        } else if (contained == null) {
            return true;
        }
        List<S> transformedContaining = containing.stream().map(transformer1).collect(toList());
        List<S> transformedContained = contained.stream().map(transformer2).collect(toList());
        return transformedContaining.containsAll(transformedContained);
    }

    public static <T extends Comparable<T>> List<T> sorted(Collection<T> toSort){
        List<T> sorted = new ArrayList<T>(toSort);
        Collections.sort(sorted);
        return sorted;
    }

    public static final <R, S, T> SortedMap<T, List<R>> toSortedMapWithList(List<S> orderedList, Function<S, T> keyTransform, Function<S, R> valueTransform) {
        SortedMap<T, List<R>> orderedMap = new TreeMap<>();
        if (orderedList != null) {
            orderedList.stream().forEachOrdered(s -> {
                        if (s != null) {
                            T key = keyTransform.apply(s);
                            R value = valueTransform.apply(s);
                            if (!orderedMap.containsKey(key)) {
                                orderedMap.put(key, new ArrayList<>());
                            }
                            orderedMap.get(key).add(value);
                        }
                    }
            );
        }
        return orderedMap;
    }

    public static final <R, S, T> SortedMap<T, R> toSortedMap(List<S> orderedList, Function<S, T> keyTransform, Function<S, R> valueTransform) {
        return orderedList.stream().collect(Collectors.toMap(keyTransform, valueTransform,
                (v1,v2) ->{ throw new RuntimeException(String.format("Duplicate key for values %s and %s", v1, v2));},
                TreeMap::new));
    }


    public static <S, T> T unique(Collection<S> collectionToSearch, Function<S, T> property) {
        List<T> distinct = collectionToSearch.stream().map(property).distinct().collect(toList());
        if (distinct.size() != 1) {
            throw new IllegalArgumentException("Collection to search:" + collectionToSearch + " does not have a unique property:" + property);
        } else {
            return distinct.get(0);
        }
    }

    public static <S> boolean matchAll(Collection<S> collectionToMatch, Predicate<S> predicate){
        return collectionToMatch.stream().allMatch(predicate);
    }

    /**
     * A method that a list of length n with t the value of every element.
     * @param n - times to replicate t
     * @param <T>
     * @return
     */
    public static <T> List<T> nOf(int n, T t) {
        return range(0, n).mapToObj(x -> t).collect(Collectors.toList());
    }

    public static <T> List<T> union(List<? extends T> one, List<? extends T> two){
        List<T> union = new ArrayList<>();
        union.addAll(one);
        union.addAll(two);
        return union;
    }

    public static <T> List<T> flattenOptional(Collection<Optional<T>> toFlatten){
        return  simpleMap(simpleFilter(toFlatten, Optional::isPresent), Optional::get);
    }
}
