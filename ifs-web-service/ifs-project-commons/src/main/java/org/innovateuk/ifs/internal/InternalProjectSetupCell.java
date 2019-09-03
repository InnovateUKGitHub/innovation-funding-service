package org.innovateuk.ifs.internal;

import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.sections.SectionAccess;

import static org.innovateuk.ifs.sections.SectionAccess.ACCESSIBLE;

public class InternalProjectSetupCell {
    private final ProjectActivityStates projectActivityState;
    private final String url;
    private final Boolean canBeAccessed;

    public InternalProjectSetupCell(ProjectActivityStates projectActivityState, String url, SectionAccess canBeAccessed) {
        this.projectActivityState = projectActivityState;
        this.url = url;
        this.canBeAccessed = canBeAccessed == ACCESSIBLE;
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
}
