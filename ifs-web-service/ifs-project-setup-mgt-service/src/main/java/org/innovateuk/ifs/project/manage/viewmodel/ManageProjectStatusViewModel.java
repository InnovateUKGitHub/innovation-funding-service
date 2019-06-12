package org.innovateuk.ifs.project.manage.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectState;

import java.util.List;

import static org.innovateuk.ifs.project.resource.ProjectState.COMPLETED_OFFLINE;
import static org.innovateuk.ifs.project.resource.ProjectState.HANDLED_OFFLINE;
import static org.innovateuk.ifs.project.resource.ProjectState.WITHDRAWN;

public class ManageProjectStatusViewModel {

    private final long competitionId;
    private final long projectId;
    private final String projectName;
    private final List<ProjectState> allowableStates;
    private final ProjectState state;





    
    public boolean isCompleteOffline() {
        return COMPLETED_OFFLINE.equals(state);
    }

    public boolean isHandledOffline() {
        return HANDLED_OFFLINE.equals(state);
    }

    public boolean isWithdrawn() {
        return WITHDRAWN.equals(state);
    }


}
