package org.innovateuk.ifs.interview.domain;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.*;

/**
 * Process outcome for the @link InterviewAssignment}'s {@code RESPOND} event.
 */
@Entity
@DiscriminatorValue(value = "assessment-interview-panel-response")
public class InterviewAssignmentResponseOutcome extends ProcessOutcome<InterviewAssignment> {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="file_entry_id", referencedColumnName="id")
    private FileEntry fileResponse;

    public void setAssessmentPanelApplicationInvite(InterviewAssignment interviewAssignment) {
        setProcess(interviewAssignment);
    }

    public String getResponse() {
        return comment;
    }

    public void setResponse(String response) {
        this.comment = response;
    }

    public FileEntry getFileResponse() {
        return fileResponse;
    }

    public void setFileResponse(FileEntry fileResponse) {
        this.fileResponse = fileResponse;
    }
}