package com.worth.ifs.assessment.builder;

import com.worth.ifs.assessment.resource.AssessmentSubmissionsResource;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;

import static com.worth.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder.newAssessmentSubmissionsResource;

public class AssessmentSubmissionsResourceBuilderTest {

    @Test
    public void buildOne() throws Exception {
        List<Long> assessmentIds = Arrays.asList(10L, 50L, 100L);

        AssessmentSubmissionsResource resource = newAssessmentSubmissionsResource()
                .withAssessmentIds(assessmentIds)
                .build();

        assertEquals(3, resource.getAssessmentIds().size());
        assertEquals(10L, resource.getAssessmentIds().get(0).longValue());
        assertEquals(50L, resource.getAssessmentIds().get(1).longValue());
        assertEquals(100L, resource.getAssessmentIds().get(2).longValue());
    }

    @Test
    public void buildMany() throws Exception {
        List<Long> expectedAssessmentIds1 = Arrays.asList(10L, 50L, 100L);
        List<Long> expectedAssessmentIds2 = Arrays.asList(20L, 100L, 200L, 250L);

        List<AssessmentSubmissionsResource> resources = newAssessmentSubmissionsResource()
                .withAssessmentIds(expectedAssessmentIds1, expectedAssessmentIds2)
                .build(3);

        AssessmentSubmissionsResource resource1 = resources.get(0);
        assertEquals(resource1.getAssessmentIds(), expectedAssessmentIds1);

        AssessmentSubmissionsResource resource2 = resources.get(1);
        assertEquals(resource2.getAssessmentIds(), expectedAssessmentIds2);
    }
}
