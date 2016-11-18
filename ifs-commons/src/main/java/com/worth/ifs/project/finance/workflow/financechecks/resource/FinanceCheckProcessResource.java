package com.worth.ifs.project.finance.workflow.financechecks.resource;

import com.worth.ifs.commons.workflow.resource.BaseProcessResource;
import com.worth.ifs.project.finance.resource.FinanceCheckState;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.UserResource;

import java.time.LocalDateTime;

/**
 * A resource representing the current state of a Finance Check process
 */
public class FinanceCheckProcessResource extends BaseProcessResource<FinanceCheckState, ProjectUserResource> {

    private boolean canApprove;

    // for JSON marshalling
    FinanceCheckProcessResource() {
        super();
    }

    public FinanceCheckProcessResource(FinanceCheckState currentState, ProjectUserResource participant, UserResource internalParticipant, LocalDateTime modifiedDate, boolean canApprove) {
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
