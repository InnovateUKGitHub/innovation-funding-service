package org.innovateuk.ifs.workflow.domain;

import org.innovateuk.ifs.identity.Identifiable;
import org.innovateuk.ifs.workflow.resource.State;

public interface ActivityStateEnum<T extends ActivityStateEnum<T>> extends Identifiable {

    State getBackingState();
}
