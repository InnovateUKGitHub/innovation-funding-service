package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder.newAssessmentTotalScoreResource;
import static org.junit.Assert.assertEquals;

public class AssessmentTotalScoreResourceBuilderTest {

    @Test
    public void buildOne() throws Exception {
        int expectedTotalScoreGiven = 57;
        int expectedTotalScorePossible = 200;

        AssessmentTotalScoreResource assessmentTotalScoreResource = newAssessmentTotalScoreResource()
                .withTotalScoreGiven(expectedTotalScoreGiven)
                .withTotalScorePossible(expectedTotalScorePossible)
                .build();

        assertEquals(expectedTotalScoreGiven, assessmentTotalScoreResource.getTotalScoreGiven());
        assertEquals(expectedTotalScorePossible, assessmentTotalScoreResource.getTotalScorePossible());
    }

    @Test
    public void buildMany() {
        Integer[] expectedTotalScoreGivens = {55, 57};
        Integer[] expectedTotalScorePossibles = {100, 200};

        List<AssessmentTotalScoreResource> assessmentTotalScoreResources = newAssessmentTotalScoreResource()
                .withTotalScoreGiven(expectedTotalScoreGivens)
                .withTotalScorePossible(expectedTotalScorePossibles)
                .build(2);

        AssessmentTotalScoreResource first = assessmentTotalScoreResources.get(0);
        assertEquals(expectedTotalScoreGivens[0].intValue(), first.getTotalScoreGiven());
        assertEquals(expectedTotalScorePossibles[0].intValue(), first.getTotalScorePossible());

        AssessmentTotalScoreResource second = assessmentTotalScoreResources.get(1);
        assertEquals(expectedTotalScoreGivens[1].intValue(), second.getTotalScoreGiven());
        assertEquals(expectedTotalScorePossibles[1].intValue(), second.getTotalScorePossible());
    }
}
