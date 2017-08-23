package org.innovateuk.ifs.assessment.workflow.guards;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentEvent;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * This is responsible for testing if the competition has defined it's assessment period
 * and if the current date is within that period, allow the transition to take place.
 */
@Component
public class CompetitionInAssessmentGuard implements Guard<AssessmentState, AssessmentEvent> {

    @Override
    public boolean evaluate(StateContext<AssessmentState, AssessmentEvent> context) {
        Assessment assessment = (Assessment) context.getMessageHeader("target");
        Competition competition = assessment.getTarget().getCompetition();

        return competition.getCompetitionStatus() == CompetitionStatus.IN_ASSESSMENT;
    }
}
