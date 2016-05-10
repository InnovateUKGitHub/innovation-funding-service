package com.worth.ifs.assessment;

import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessmentStates;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;

public class AssessmentStatusComparatorTest {

    @Ignore
    @Test
    public void testCompareByStatus() throws Exception {
        List<AssessmentResource> assessmentsCompare = newAssessmentResource()
                .withProcessState(AssessmentStates.ASSESSED.getState(), AssessmentStates.PENDING.getState(), AssessmentStates.SUBMITTED.getState(), AssessmentStates.OPEN.getState(), AssessmentStates.REJECTED.getState())
                .build(5);

        AssessmentStatusComparator comparator = new AssessmentStatusComparator();

        assessmentsCompare.sort(comparator);

        Assert.assertEquals(AssessmentStates.PENDING.getState(), assessmentsCompare.get(0).getStatus());
        Assert.assertEquals(AssessmentStates.REJECTED.getState(), assessmentsCompare.get(1).getStatus());
        Assert.assertEquals(AssessmentStates.OPEN.getState(), assessmentsCompare.get(2).getStatus());
        Assert.assertEquals(AssessmentStates.ASSESSED.getState(), assessmentsCompare.get(3).getStatus());
        Assert.assertEquals(AssessmentStates.SUBMITTED.getState(), assessmentsCompare.get(4).getStatus());

    }

    @Ignore
    @Test
    public void testCompareNullValues() throws Exception {
        List<AssessmentResource> assessmentsCompare = newAssessmentResource()
                .withId(5L, 10L, 1L)
                .build(3);

        AssessmentStatusComparator comparator = new AssessmentStatusComparator();

        assessmentsCompare.sort(comparator);

        Assert.assertEquals(1L, assessmentsCompare.get(0).getId().longValue());
        Assert.assertEquals(5L, assessmentsCompare.get(1).getId().longValue());
        Assert.assertEquals(10L, assessmentsCompare.get(2).getId().longValue());
    }

    @Ignore
    @Test
    public void testCompareNullValues2() throws Exception {
        List<AssessmentResource> assessmentsCompare = newAssessmentResource()
                .build(3);

        AssessmentStatusComparator comparator = new AssessmentStatusComparator();

        assessmentsCompare.sort(comparator);


    }

    /**
     * When the AssessmentState is the same, the ordering should be done by ID.
     */
    @Ignore
    @Test
    public void testCompareById() throws Exception {

        List<AssessmentResource> assessmentsCompare = newAssessmentResource()
                .withId(5L, 10L, 1L)
                .withProcessState(AssessmentStates.ASSESSED.getState(), AssessmentStates.ASSESSED.getState(), AssessmentStates.ASSESSED.getState())
                .build(3);

        AssessmentStatusComparator comparator = new AssessmentStatusComparator();

        assessmentsCompare.sort(comparator);


        Assert.assertEquals(1L, assessmentsCompare.get(0).getId().longValue());
        Assert.assertEquals(5L, assessmentsCompare.get(1).getId().longValue());
        Assert.assertEquals(10L, assessmentsCompare.get(2).getId().longValue());

    }


}