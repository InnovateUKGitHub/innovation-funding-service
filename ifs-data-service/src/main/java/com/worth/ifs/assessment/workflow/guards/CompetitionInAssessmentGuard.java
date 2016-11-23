package com.worth.ifs.assessment.workflow.guards;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.MilestoneType;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * This is responsible for testing if the competition has defined it's assessment period
 * and if the current date is within that period, allow the transition to take place.
 */
@Component
public class CompetitionInAssessmentGuard implements Guard<AssessmentStates, AssessmentOutcomes> {

    @Override
    public boolean evaluate(StateContext<AssessmentStates, AssessmentOutcomes> context) {
        Assessment assessment = (Assessment) context.getMessageHeader("assessment");
        Competition competition = assessment.getTarget().getCompetition();

        return competition.isMilestoneReached(MilestoneType.ASSESSORS_NOTIFIED) &&
                !competition.isMilestoneReached(MilestoneType.ASSESSMENT_CLOSED);
    }
}
