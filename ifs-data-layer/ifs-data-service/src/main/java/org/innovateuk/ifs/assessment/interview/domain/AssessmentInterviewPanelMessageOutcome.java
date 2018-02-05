package org.innovateuk.ifs.assessment.interview.domain;

import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * Process outcome for the @link AssessmentInterviewPanel}'s {@code SEND} event.
 */
@Entity
@DiscriminatorValue(value = "assessment-interview-panel-message")
public class AssessmentInterviewPanelMessageOutcome extends ProcessOutcome<AssessmentInterviewPanel> {

    public void setAssessmentInterviewPanel(AssessmentInterviewPanel assessmentInterviewPanel) {
        setProcess(assessmentInterviewPanel);
    }

    public String getSubject() {
        return description;
    }

    public void setSubject(String subject) {
        description = subject;
    }

    public String getMessage() {
        return comment;
    }

    public void setMessage(String message) {
        comment = message;
    }
}