package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.AssessmentCreateResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentCreateResourceBuilder.newAssessmentCreateResource;
import static org.junit.Assert.assertEquals;

public class AssessmentCreateResourceBuilderTest {

    @Test
    public void testOne() throws Exception {
        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(1L)
                .withAssessorId(2L)
                .build();

        assertEquals(1L, assessmentCreateResource.getApplicationId().longValue());
        assertEquals(2L, assessmentCreateResource.getAssessorId().longValue());
    }

    @Test
    public void testMany() throws Exception {
        List<AssessmentCreateResource> assessmentCreateResources = newAssessmentCreateResource()
                .withApplicationId(1L, 2L)
                .withAssessorId(3L, 4L)
                .build(2);

        assertEquals(1L, assessmentCreateResources.get(0).getApplicationId().longValue());
        assertEquals(2L, assessmentCreateResources.get(1).getApplicationId().longValue());
        assertEquals(3L, assessmentCreateResources.get(0).getAssessorId().longValue());
        assertEquals(4L, assessmentCreateResources.get(1).getAssessorId().longValue());
    }
}
