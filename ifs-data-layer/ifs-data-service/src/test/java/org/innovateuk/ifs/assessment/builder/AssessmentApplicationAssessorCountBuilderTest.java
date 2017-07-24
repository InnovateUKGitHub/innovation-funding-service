package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentApplicationAssessorCount;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentApplicationAssessorCountBuilder.newAssessmentApplicationAssessorCount;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.junit.Assert.*;

public class AssessmentApplicationAssessorCountBuilderTest {

    @Test
    public void buildOne() throws Exception {
        Application application = newApplication().build();
        Assessment assessment = newAssessment().build();
        int assessorCount = 10;

        AssessmentApplicationAssessorCount count = newAssessmentApplicationAssessorCount()
                .withApplication(application)
                .withAssessment(assessment)
                .withAssessorCount(assessorCount)
                .build();

        assertEquals(application, count.getApplication());
        assertEquals(assessment, count.getAssessment());
        assertEquals(assessorCount, count.getAssessorCount());
    }

    @Test
    public void buildMany() throws Exception {
        Application[] applications = newApplication().buildArray(2, Application.class);
        Assessment[] assessments = newAssessment().buildArray(2, Assessment.class);
        Integer[] assessorCounts = {10, 20};

        List<AssessmentApplicationAssessorCount> counts = newAssessmentApplicationAssessorCount()
                .withApplication(applications)
                .withAssessment(assessments)
                .withAssessorCount(assessorCounts)
                .build(2);

        assertEquals(applications[0], counts.get(0).getApplication());
        assertEquals(assessments[0], counts.get(0).getAssessment());
        assertEquals(assessorCounts[0].intValue(), counts.get(0).getAssessorCount());

        assertEquals(applications[1], counts.get(1).getApplication());
        assertEquals(assessments[1], counts.get(1).getAssessment());
        assertEquals(assessorCounts[1].intValue(), counts.get(1).getAssessorCount());
    }
}
