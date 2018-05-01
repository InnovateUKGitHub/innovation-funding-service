package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.interview.builder.InterviewAssignmentKeyStatisticsResourceBuilder.newInterviewAssignmentKeyStatisticsResource;
import static org.junit.Assert.assertEquals;

/**
 * Builder for {@link InterviewAssignmentKeyStatisticsResource}
 */
public class InterviewAssignmentKeyStatisticsResourceBuilderTest {
    @Test
    public void buildOne() {
        int expectedApplicationsInCompetition = 7;
        int expectedApplicationsAssigned = 11;

        InterviewAssignmentKeyStatisticsResource keyStatisticsResource = newInterviewAssignmentKeyStatisticsResource()
                .withApplicationsInCompetition(expectedApplicationsInCompetition)
                .withApplicationsAssigned(expectedApplicationsAssigned)
                .build();

        assertEquals(expectedApplicationsInCompetition, keyStatisticsResource.getApplicationsInCompetition());
        assertEquals(expectedApplicationsAssigned, keyStatisticsResource.getApplicationsAssigned());
    }

    @Test
    public void buildMany() {
        Integer[] expectedApplicationsInCompetitions = {13, 17};
        Integer[] expectedApplicationsAssigneds = {19, 23};

        List<InterviewAssignmentKeyStatisticsResource> keyStatisticsResources = newInterviewAssignmentKeyStatisticsResource()
                .withApplicationsInCompetition(expectedApplicationsInCompetitions)
                .withApplicationsAssigned(expectedApplicationsAssigneds)
                .build(2);

        InterviewAssignmentKeyStatisticsResource first = keyStatisticsResources.get(0);
        assertEquals((int)expectedApplicationsInCompetitions[0], first.getApplicationsInCompetition());
        assertEquals((int)expectedApplicationsAssigneds[0], first.getApplicationsAssigned());

        InterviewAssignmentKeyStatisticsResource second = keyStatisticsResources.get(1);
        assertEquals((int)expectedApplicationsInCompetitions[1], second.getApplicationsInCompetition());
        assertEquals((int)expectedApplicationsAssigneds[1], second.getApplicationsAssigned());
    }
}