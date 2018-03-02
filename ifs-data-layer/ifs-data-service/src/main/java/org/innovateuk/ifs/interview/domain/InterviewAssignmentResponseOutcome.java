package org.innovateuk.ifs.interview.domain;

import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Process outcome for the @link InterviewAssignment}'s {@code RESPOND} event.
 */
@Entity
@DiscriminatorValue(value = "assessment-interview-panel-response")
public class InterviewAssignmentResponseOutcome extends ProcessOutcome<InterviewAssignment> {

    public void setAssessmentPanelApplicationInvite(InterviewAssignment interviewAssignment) {
        setProcess(interviewAssignment);
    }

    public String getResponse() {
        return comment;
    }

    public void setResponse(String response) {
        this.comment = response;
    }
}