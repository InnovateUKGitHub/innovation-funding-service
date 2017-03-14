package org.innovateuk.ifs.util;

import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.*;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

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
        List<OrganisationResource> orgs = newOrganisationResource().withName("C", "A", "exceptionElement").build(3);
        List<OrganisationResource> list = new PrioritySorting<>(orgs, newOrganisationResource()
                .withName("exceptionElement").build(), OrganisationResource::getName).unwrap();
        assertTrue(list.size() == 3);
        assertEquals(CollectionFunctions.simpleMap(list, OrganisationResource::getName),
                CollectionFunctions.simpleMap(newOrganisationResource().withName("exceptionElement", "A", "C")
                        .build(3), OrganisationResource::getName));
    }

    @Test
    public void sortingListWithoutPriorityStillSortsElementsByField() {
        List<OrganisationResource> orgs = newOrganisationResource().withName("C", "A", "exceptionElement").build(3);
        List<OrganisationResource> list = new PrioritySorting<>(orgs, null, OrganisationResource::getName).unwrap();
        assertTrue(list.size() == 3);
        assertEquals(CollectionFunctions.simpleMap(list, OrganisationResource::getName),
                CollectionFunctions.simpleMap(newOrganisationResource().withName("A", "C", "exceptionElement")
                        .build(3), OrganisationResource::getName));
    }


}
