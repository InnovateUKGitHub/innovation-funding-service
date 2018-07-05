package org.innovateuk.ifs.util;


import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrioritySortingTest {

    @Test
    public void sortingEmptyListReturnsEmptyList() {
        List<String> list = new PrioritySorting<>(new ArrayList<String>(), "exceptionElement", Function.identity()).unwrap();
        assertTrue(list.isEmpty());
    }

    @Test
    public void sortingListWorksAsExpectedWithIdentity() {
        List<String> list = new PrioritySorting<>(asList("C", "A", "exceptionElement"), "exceptionElement", Function.identity()).unwrap();
        assertTrue(list.size() == 3);
        assertEquals(list, asList("exceptionElement", "A", "C"));
    }

    @Test
    public void sortingListWorksAsExpectedWithFunction() {
        List<TestResource> orgs = createResources("C", "A", "exceptionElement");
        List<TestResource> list = new PrioritySorting<>(orgs, createResource("exceptionElement"), TestResource::getName).unwrap();
        assertTrue(list.size() == 3);
        Assert.assertEquals(CollectionFunctions.simpleMap(list, TestResource::getName),
                CollectionFunctions.simpleMap(createResources("exceptionElement", "A", "C"), TestResource::getName));
    }

    @Test
    public void sortingListWithoutPriorityStillSortsElementsByField() {
        List<TestResource> orgs = createResources("C", "A", "exceptionElement");
        List<TestResource> list = new PrioritySorting<>(orgs, null, TestResource::getName).unwrap();
        assertTrue(list.size() == 3);
        Assert.assertEquals(CollectionFunctions.simpleMap(list, TestResource::getName),
                CollectionFunctions.simpleMap(createResources("A", "C", "exceptionElement"), TestResource::getName));
    }
    
    private List<TestResource> createResources(String ... names) {
        return stream(names)
                .map(n -> new TestResource(n))
                .collect(Collectors.toList());
    }
    private TestResource createResource(String name) {
        return createResources(name).get(0);
    }

}