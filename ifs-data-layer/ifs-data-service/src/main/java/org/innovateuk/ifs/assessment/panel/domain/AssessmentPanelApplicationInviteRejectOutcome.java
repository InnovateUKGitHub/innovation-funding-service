package org.innovateuk.ifs.assessment.panel.domain;

import org.innovateuk.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


/**
 * Process outcome for the {@code }AssessmentPanelApplicationInvite}s {@code REJECT} event.
 */
@Entity
@DiscriminatorValue(value = "assessment-panel-application-invite-reject")
public class AssessmentPanelApplicationInviteRejectOutcome extends ProcessOutcome<AssessmentPanelApplicationInvite> {

    public void setAssessmentPanelApplicationInvite(AssessmentPanelApplicationInvite assessmentPanelApplicationInvite) {
        setProcess(assessmentPanelApplicationInvite);
    }

    public String getRejectionComment() {
        return comment;
    }

    public void setRejectionComment(String rejectComment) {
        this.comment = rejectComment;
    }
}