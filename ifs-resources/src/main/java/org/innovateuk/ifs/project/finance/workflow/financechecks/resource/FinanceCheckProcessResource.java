package org.innovateuk.ifs.project.finance.workflow.financechecks.resource;

import org.innovateuk.ifs.workflow.resource.BaseProcessResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckState;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.ZonedDateTime;

/**
 * A resource representing the current state of a Finance Check process
 */
public class FinanceCheckProcessResource extends BaseProcessResource<FinanceCheckState, ProjectUserResource> {

    private boolean canApprove;

    // for JSON marshalling
    FinanceCheckProcessResource() {
        super();
    }

    public FinanceCheckProcessResource(FinanceCheckState currentState, ProjectUserResource participant, UserResource internalParticipant, ZonedDateTime modifiedDate, boolean canApprove) {
        super(currentState, participant, internalParticipant, modifiedDate);
        this.canApprove = canApprove;
    }

    public boolean isCanApprove() {
        return canApprove;
    }

    public void setCanApprove(boolean canApprove) {
        this.canApprove = canApprove;
    }
}
