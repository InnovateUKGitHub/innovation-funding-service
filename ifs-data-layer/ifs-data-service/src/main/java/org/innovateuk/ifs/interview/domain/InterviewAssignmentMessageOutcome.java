package org.innovateuk.ifs.interview.domain;

import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Process outcome for the @link InterviewAssignment}'s {@code NOTIFY} event.
 */
@Entity
@DiscriminatorValue(value = "assessment-interview-panel-message")
public class InterviewAssignmentMessageOutcome extends ProcessOutcome<InterviewAssignment> {

    public void setAssessmentInterviewPanel(InterviewAssignment interviewAssignment) {
        setProcess(interviewAssignment);
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