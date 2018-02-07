package org.innovateuk.ifs.assessment.interview.domain;

import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Process outcome for the @link AssessmentInterviewPanel}'s {@code RESPOND} event.
 */
@Entity
@DiscriminatorValue(value = "assessment-interview-panel-response")
public class AssessmentInterviewPanelResponseOutcome extends ProcessOutcome<AssessmentInterviewPanel> {

    public void setAssessmentPanelApplicationInvite(AssessmentInterviewPanel assessmentInterviewPanel) {
        setProcess(assessmentInterviewPanel);
    }

    public String getResponse() {
        return comment;
    }

    public void setResponse(String response) {
        this.comment = response;
    }
}