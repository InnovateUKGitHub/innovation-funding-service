package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.sections.SectionStatus;

public class SetupStatusStageViewModel {
    private final String title;
    private final String subtitle;
    private final String url;
    private final SectionStatus status;
    private final SectionAccess access;

    public SetupStatusStageViewModel(String title, String subtitle, String url, SectionStatus status, SectionAccess access) {
        this.title = title;
        this.subtitle = subtitle;
        this.url = url;
        this.status = status;
        this.access = access;
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
}
