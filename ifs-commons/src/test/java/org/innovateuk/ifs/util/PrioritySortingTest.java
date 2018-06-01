package org.innovateuk.ifs.util;

//import static org.innovateuk.ifs.user.builder.TestResourceBuilder.*;
//import org.innovateuk.ifs.user.resource.TestResource;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PrioritySortingTest {

    @Test
    public void sortingEmptyListReturnsEmptyList() {
        List<String> list = new PrioritySorting<>(new ArrayList<String>(), "exceptionElement", Function.identity()).unwrap();
        assertTrue(list.isEmpty());
    }

    @Test
    public void sortingListWorksAsExpectedWithIdentity() {
        List<String> list = new PrioritySorting<>(Arrays.asList("C", "A", "exceptionElement"), "exceptionElement", Function.identity()).unwrap();
        assertTrue(list.size() == 3);
        assertEquals(list, Arrays.asList("exceptionElement", "A", "C"));
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
        return Arrays.stream(names)
                .map(n -> new TestResource(n))
                .collect(Collectors.toList());
    }
    private TestResource createResource(String name) {
        return createResources(name).get(0);
    }


}
