package org.innovateuk.ifs.workflow.domain;

import org.innovateuk.ifs.util.enums.Identifiable;
import org.innovateuk.ifs.workflow.resource.State;

public interface ActivityStateEnum<T extends ActivityStateEnum<T>> extends Identifiable<T> {

    State getBackingState();
}
