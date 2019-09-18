package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.sections.SectionStatus;

public class SetupStatusStageViewModel {

    private final ProjectSetupStage stage;
    private final String title;
    private final String subtitle;
    private final String url;
    private final SectionStatus status;
    private final SectionAccess access;
    private final String statusOverride;

    public SetupStatusStageViewModel(ProjectSetupStage stage, String title, String subtitle, String url, SectionStatus status, SectionAccess access) {
        this.stage = stage;
        this.title = title;
        this.subtitle = subtitle;
        this.url = url;
        this.status = status;
        this.access = access;
        this.statusOverride = null;
    }

    public SetupStatusStageViewModel(ProjectSetupStage stage, String title, String subtitle, String url, SectionStatus status, SectionAccess access, String statusOverride) {
        this.stage = stage;
        this.title = title;
        this.subtitle = subtitle;
        this.url = url;
        this.status = status;
        this.access = access;
        this.statusOverride = statusOverride;
    }

    public ProjectSetupStage getStage() {
        return stage;
    }

    public String getTitle() {
        return title + (getAccess().isNotRequired() ? " (not required)" : "");
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getUrl() {
        return url;
    }

    public SectionStatus getStatus() {
        return status;
    }

    public SectionAccess getAccess() {
        return access;
    }

    public String getStatusOverride() {
        return statusOverride;
    }
}
