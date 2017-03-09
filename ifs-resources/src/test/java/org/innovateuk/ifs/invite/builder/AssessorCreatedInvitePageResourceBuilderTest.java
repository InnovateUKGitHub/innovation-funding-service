package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.AssessorCreatedInvitePageResource;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInvitePageResourceBuilder.newAssessorCreatedInvitePageResource;
import static org.junit.Assert.assertEquals;

public class AssessorCreatedInvitePageResourceBuilderTest {

    private int[] numbers = {1, 2};
    private List[] contents = {asList(1, 2, 3), asList(4, 5, 6)};
    private int[] sizes = {20, 40};
    private long[] totalElements = {200L, 800L};
    private int[] totalPages = {10, 20};

    @Test
    public void buildOne() throws Exception {
        AssessorCreatedInvitePageResource resource = newAssessorCreatedInvitePageResource()
                .withContent(contents[0])
                .withNumber(numbers[0])
                .withSize(sizes[0])
                .withTotalElements(totalElements[0])
                .withTotalPages(totalPages[0])
                .build();

        assertEquals(contents[0], resource.getContent());
        assertEquals(numbers[0], resource.getNumber());
        assertEquals(sizes[0], resource.getSize());
        assertEquals(totalElements[0], resource.getTotalElements());
        assertEquals(totalPages[0], resource.getTotalPages());
    }

    @Test
    public void buildMany() throws Exception {
        List<AssessorCreatedInvitePageResource> resources = newAssessorCreatedInvitePageResource()
                .withContent(contents[0], contents[1])
                .withNumber(numbers[0], numbers[1])
                .withSize(sizes[0], sizes[1])
                .withTotalElements(totalElements[0], totalElements[1])
                .withTotalPages(totalPages[0], totalPages[1])
                .build(2);

        assertEquals(contents[0], resources.get(0).getContent());
        assertEquals(numbers[0], resources.get(0).getNumber());
        assertEquals(sizes[0], resources.get(0).getSize());
        assertEquals(totalElements[0], resources.get(0).getTotalElements());
        assertEquals(totalPages[0], resources.get(0).getTotalPages());

        assertEquals(contents[1], resources.get(1).getContent());
        assertEquals(numbers[1], resources.get(1).getNumber());
        assertEquals(sizes[1], resources.get(1).getSize());
        assertEquals(totalElements[1], resources.get(1).getTotalElements());
        assertEquals(totalPages[1], resources.get(1).getTotalPages());
    }
}
