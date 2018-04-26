package org.innovateuk.ifs.interview.domain;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.*;

/**
 * Process outcome for the @link InterviewAssignment}'s {@code NOTIFY} event.
 */
@Entity
@DiscriminatorValue(value = "assessment-interview-panel-message")
public class InterviewAssignmentMessageOutcome extends ProcessOutcome<InterviewAssignment> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="file_entry_id", referencedColumnName="id")
    private FileEntry feedback;

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

    public FileEntry getFeedback() {
        return feedback;
    }

    public void setFeedback(FileEntry feedback) {
        this.feedback = feedback;
    }
}