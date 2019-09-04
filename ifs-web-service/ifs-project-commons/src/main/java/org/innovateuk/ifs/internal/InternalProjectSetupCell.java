package org.innovateuk.ifs.internal;

import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.sections.SectionAccess;

import static org.innovateuk.ifs.sections.SectionAccess.ACCESSIBLE;

public class InternalProjectSetupCell {
    private final ProjectActivityStates projectActivityState;
    private final String url;
    private final Boolean canBeAccessed;
    private final ProjectSetupStage projectSetupStage;

    public InternalProjectSetupCell(ProjectSetupStage projectSetupStage, ProjectActivityStates projectActivityState, String url, SectionAccess canBeAccessed) {
        this.projectActivityState = projectActivityState;
        this.url = url;
        this.canBeAccessed = canBeAccessed == ACCESSIBLE;
        this.projectSetupStage = projectSetupStage;
    }

    public ProjectActivityStates getProjectActivityState() {
        return projectActivityState;
    }

    public String getUrl() {
        return url;
    }

    public Boolean isCanBeAccessed() {
        return canBeAccessed;
    }

    public ProjectSetupStage getProjectSetupStage() {
        return projectSetupStage;
    }
}
