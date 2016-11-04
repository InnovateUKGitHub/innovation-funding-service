package com.worth.ifs.assessment;

import com.worth.ifs.assessment.resource.AssessmentResource;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.resource.AssessmentStates.*;

public class AssessmentStatusComparatorTest {

    @Test
    public void testCompareByStatus() throws Exception {
        List<AssessmentResource> assessmentsCompare = newAssessmentResource()
                .withActivityState(READY_TO_SUBMIT, PENDING, SUBMITTED, OPEN, REJECTED)
                .build(5);

        AssessmentStatusComparator comparator = new AssessmentStatusComparator();

        assessmentsCompare.sort(comparator);

        Assert.assertEquals(PENDING, assessmentsCompare.get(0).getAssessmentState());
        Assert.assertEquals(REJECTED, assessmentsCompare.get(1).getAssessmentState());
        Assert.assertEquals(OPEN, assessmentsCompare.get(2).getAssessmentState());
        Assert.assertEquals(READY_TO_SUBMIT, assessmentsCompare.get(3).getAssessmentState());
        Assert.assertEquals(SUBMITTED, assessmentsCompare.get(4).getAssessmentState());

    }

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
    @Test
    public void testCompareById() throws Exception {

        List<AssessmentResource> assessmentsCompare = newAssessmentResource()
                .withId(5L, 10L, 1L)
                .withActivityState(READY_TO_SUBMIT, READY_TO_SUBMIT, READY_TO_SUBMIT)
                .build(3);

        AssessmentStatusComparator comparator = new AssessmentStatusComparator();

        assessmentsCompare.sort(comparator);


        Assert.assertEquals(1L, assessmentsCompare.get(0).getId().longValue());
        Assert.assertEquals(5L, assessmentsCompare.get(1).getId().longValue());
        Assert.assertEquals(10L, assessmentsCompare.get(2).getId().longValue());

    }


}