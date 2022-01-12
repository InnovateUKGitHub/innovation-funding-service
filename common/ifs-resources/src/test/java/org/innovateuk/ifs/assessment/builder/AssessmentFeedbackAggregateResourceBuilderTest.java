package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.assessment.builder.AssessmentFeedbackAggregateResourceBuilder.newAssessmentFeedbackAggregateResource;
import static org.junit.Assert.assertEquals;

public class AssessmentFeedbackAggregateResourceBuilderTest {

    @Test
    public void buildOne() throws Exception {
        BigDecimal expectedAvgScore = BigDecimal.ONE;
        List<String> expectedFeedback = asList("f1", "f2");

        AssessmentFeedbackAggregateResource aggregateResource = newAssessmentFeedbackAggregateResource()
                .withAvgScore(expectedAvgScore)
                .withFeedback(expectedFeedback)
                .build();

        assertEquals(expectedAvgScore, aggregateResource.getAvgScore());
        assertEquals(expectedFeedback, aggregateResource.getFeedback());
    }

    @Test
    public void buildMany() throws Exception {
        BigDecimal[] expectedAvgScore = { BigDecimal.ONE, BigDecimal.TEN};
        List<String>[] expectedFeedback = new List[]{asList("f1", "f2"), asList("f3", "f4")};

        List<AssessmentFeedbackAggregateResource> aggregateResources = newAssessmentFeedbackAggregateResource()
                .withAvgScore(expectedAvgScore)
                .withFeedback(expectedFeedback)
                .build(2);

        for (int i = 0; i < 2; i++) {
            assertEquals(expectedAvgScore[i], aggregateResources.get(i).getAvgScore());
            assertEquals(expectedFeedback[i], aggregateResources.get(i).getFeedback());
        }
    }

}
