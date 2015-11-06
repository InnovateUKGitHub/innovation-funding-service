package com.worth.ifs.assessment;

import com.worth.ifs.assessment.builder.AssessmentBuilder;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.domain.AssessmentStates;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class AssessmentStatusComparatorTest {

    @Test
    public void testCompareByStatus() throws Exception {
        List<Assessment> assessmentsCompare = AssessmentBuilder.newAssessment()
                .withProcessState(AssessmentStates.ASSESSED.getState(), AssessmentStates.PENDING.getState(), AssessmentStates.SUBMITTED.getState(), AssessmentStates.OPEN.getState(), AssessmentStates.REJECTED.getState())
                .build(5);

        AssessmentStatusComparator comparator = new AssessmentStatusComparator();

        assessmentsCompare.sort(comparator);

        Assert.assertEquals(AssessmentStates.PENDING.getState(), assessmentsCompare.get(0).getProcessStatus());
        Assert.assertEquals(AssessmentStates.REJECTED.getState(), assessmentsCompare.get(1).getProcessStatus());
        Assert.assertEquals(AssessmentStates.OPEN.getState(), assessmentsCompare.get(2).getProcessStatus());
        Assert.assertEquals(AssessmentStates.ASSESSED.getState(), assessmentsCompare.get(3).getProcessStatus());
        Assert.assertEquals(AssessmentStates.SUBMITTED.getState(), assessmentsCompare.get(4).getProcessStatus());

    }

    /**
     * When the AssessmentState is the same, the ordering should be done by ID.
     */
    @Test
    public void testCompareById() throws Exception {

        List<Assessment> assessmentsCompare = AssessmentBuilder.newAssessment()
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