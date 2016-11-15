package com.worth.ifs.assessment.builder;

import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static org.junit.Assert.assertEquals;

public class ProcessOutcomeResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        String expectedOutcome = "outcome";
        String expectedDescription = "description";
        String expectedComment = "comment";
        String expectedOutcomeType = "outcomeType";

        ProcessOutcomeResource processOutcomeResource = newProcessOutcomeResource()
                .with(id(expectedId))
                .withOutcome(expectedOutcome)
                .withDescription(expectedDescription)
                .withComment(expectedComment)
                .withOutcomeType(expectedOutcomeType)
                .build();

        assertEquals(expectedId, processOutcomeResource.getId());
        assertEquals(expectedOutcome, processOutcomeResource.getOutcome());
        assertEquals(expectedDescription, processOutcomeResource.getDescription());
        assertEquals(expectedComment, processOutcomeResource.getComment());
        assertEquals(expectedOutcomeType, processOutcomeResource.getOutcomeType());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        String[] expectedOutcomes = {"outcome 1", "outcome 2"};
        String[] expectedDescriptions = {"description 1", "description 2"};
        String[] expectedComments = {"comment 1", "comment 2"};
        String[] expectedOutcomeTypes = {"outcomeType 1", "outcomeType 2"};
        Integer[] expectedIndexes = {0, 1};

        List<ProcessOutcomeResource> processOutcomeResources = newProcessOutcomeResource()
                .withId(expectedIds)
                .withOutcome(expectedOutcomes)
                .withDescription(expectedDescriptions)
                .withComment(expectedComments)
                .withOutcomeType(expectedOutcomeTypes)
                .build(2);

        ProcessOutcomeResource first = processOutcomeResources.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedOutcomes[0], first.getOutcome());
        assertEquals(expectedDescriptions[0], first.getDescription());
        assertEquals(expectedComments[0], first.getComment());
        assertEquals(expectedOutcomeTypes[0], first.getOutcomeType());

        ProcessOutcomeResource second = processOutcomeResources.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedOutcomes[1], second.getOutcome());
        assertEquals(expectedDescriptions[1], second.getDescription());
        assertEquals(expectedComments[1], second.getComment());
        assertEquals(expectedOutcomeTypes[1], second.getOutcomeType());
    }

}