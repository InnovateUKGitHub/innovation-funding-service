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
        List<Long> assessmentIds1 = Arrays.asList(10L, 50L, 100L);
        List<Long> assessmentIds2 = Arrays.asList(20L, 100L, 200L, 250L);
        List<Long> assessmentIds3 = Arrays.asList(30L, 150L, 300L, 350L, 400L);

        List<AssessmentSubmissionsResource> resources = newAssessmentSubmissionsResource()
                .withAssessmentIds(assessmentIds1, assessmentIds2, assessmentIds3)
                .build(3);

        AssessmentSubmissionsResource resource1 = resources.get(0);
        assertEquals(3, resource1.getAssessmentIds().size());
        assertEquals(10L, resource1.getAssessmentIds().get(0).longValue());
        assertEquals(50L, resource1.getAssessmentIds().get(1).longValue());
        assertEquals(100L, resource1.getAssessmentIds().get(2).longValue());

        AssessmentSubmissionsResource resource2 = resources.get(1);
        assertEquals(4, resource2.getAssessmentIds().size());
        assertEquals(20L, resource2.getAssessmentIds().get(0).longValue());
        assertEquals(100L, resource2.getAssessmentIds().get(1).longValue());
        assertEquals(200L, resource2.getAssessmentIds().get(2).longValue());
        assertEquals(250L, resource2.getAssessmentIds().get(3).longValue());

        AssessmentSubmissionsResource resource3 = resources.get(2);
        assertEquals(5, resource3.getAssessmentIds().size());
        assertEquals(30L, resource3.getAssessmentIds().get(0).longValue());
        assertEquals(150L, resource3.getAssessmentIds().get(1).longValue());
        assertEquals(300L, resource3.getAssessmentIds().get(2).longValue());
        assertEquals(350L, resource3.getAssessmentIds().get(3).longValue());
        assertEquals(400L, resource3.getAssessmentIds().get(4).longValue());
    }
}
