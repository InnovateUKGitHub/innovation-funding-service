package org.innovateuk.ifs.application.summary;

public class ApplicationSummarySettings {

    private boolean includeStatuses = false;
    private boolean includeQuestionLinks = false;

    private ApplicationSummarySettings() {}

    public static ApplicationSummarySettings defaultSettings() {
        return new ApplicationSummarySettings();
    }

    public boolean isIncludeStatuses() {
        return includeStatuses;
    }

    public ApplicationSummarySettings setIncludeStatuses(boolean includeStatuses) {
        this.includeStatuses = includeStatuses;
        return this;
    }

    public boolean isIncludeQuestionLinks() {
        return includeQuestionLinks;
    }

    public ApplicationSummarySettings setIncludeQuestionLinks(boolean includeQuestionLinks) {
        this.includeQuestionLinks = includeQuestionLinks;
        return this;
    }
}
