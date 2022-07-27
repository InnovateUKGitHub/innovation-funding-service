package org.innovateuk.ifs.application.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.innovateuk.ifs.identity.IdentifiableEnum;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.innovateuk.ifs.workflow.resource.State;

@Getter
@AllArgsConstructor
public enum ApplicationEoiEvidenceState implements ProcessState, IdentifiableEnum  {

    NOT_SUBMITTED(63, State.NOT_SUBMITTED, "Submitted"),
    SUBMITTED(64, State.SUBMITTED, "Not Submitted");

    final long id;
    final State backingState;
    private String displayName;

    @Override
    public String getStateName() {
        return backingState.name();
    }
}
