package org.innovateuk.ifs.assessment.period.domain;

import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.junit.Before;
import org.junit.Test;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodBuilder.newAssessmentPeriod;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.junit.Assert.assertTrue;

public class AssessmentPeriodTest {

    private AssessmentPeriod assessmentPeriod;

    @Before
    public void setup() {
        assessmentPeriod = newAssessmentPeriod()
                .withMilestones(newMilestone()
                        .withType(MilestoneType.ASSESSORS_NOTIFIED)
                        .withDate(now().minusDays(1))
                        .build(1))
                .build();
    }

    @Test
    public void isOpen() {
        assertTrue(assessmentPeriod.isOpen());
    }

    @Test
    public void isInAssessment() {
        assertTrue(assessmentPeriod.isInAssessment());
    }

    @Test
    public void isAssessmentClosed() {
        assessmentPeriod = newAssessmentPeriod()
                .withMilestones(newMilestone()
                        .withType(MilestoneType.ASSESSORS_NOTIFIED, MilestoneType.ASSESSMENT_CLOSED)
                        .withDate(now().minusDays(1), now().minusDays(1))
                        .build(2))
                .build();

        assertTrue(assessmentPeriod.isAssessmentClosed());
    }
}
