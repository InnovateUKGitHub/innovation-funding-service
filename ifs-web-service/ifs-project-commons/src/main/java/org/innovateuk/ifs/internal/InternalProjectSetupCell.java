package org.innovateuk.ifs.internal;

import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.sections.SectionAccess;

public class InternalProjectSetupCell {
    private final ProjectActivityStates projectActivityState;
    private final String url;
    private final SectionAccess canBeAccessed;

    public InternalProjectSetupCell(ProjectActivityStates projectActivityState, String url, SectionAccess canBeAccessed) {
        this.projectActivityState = projectActivityState;
        this.url = url;
        this.canBeAccessed = canBeAccessed;
    }

    public ProjectActivityStates getProjectActivityState() {
        return projectActivityState;
    }

    public String getUrl() {
        return url;
    }

    public SectionAccess isCanBeAccessed() {
        return canBeAccessed;
    }
}
