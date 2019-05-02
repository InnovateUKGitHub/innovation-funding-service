package org.innovateuk.ifs.application.readonly;

public class ApplicationReadOnlySettings {

    private boolean includeStatuses = false;
    private boolean includeQuestionLinks = false;

    private ApplicationReadOnlySettings() {}

    public static ApplicationReadOnlySettings defaultSettings() {
        return new ApplicationReadOnlySettings();
    }

    public boolean isIncludeStatuses() {
        return includeStatuses;
    }

    public ApplicationReadOnlySettings setIncludeStatuses(boolean includeStatuses) {
        this.includeStatuses = includeStatuses;
        return this;
    }

    public boolean isIncludeQuestionLinks() {
        return includeQuestionLinks;
    }

    public ApplicationReadOnlySettings setIncludeQuestionLinks(boolean includeQuestionLinks) {
        this.includeQuestionLinks = includeQuestionLinks;
        return this;
    }
}
