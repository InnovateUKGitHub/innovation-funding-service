package org.innovateuk.ifs.origin;

public enum AssignQuestionOrigin implements BackLinkOrigin {

    QUESTION("/application/{applicationId}/form/question/{questionId}", "Question"),
    OVERVIEW("/application/{applicationId}", "Overview");

    private String title;
    private String originUrl;

    AssignQuestionOrigin(String originUrl, String title) {
        this.originUrl = originUrl;
        this.title = title;
    }

    @Override
    public String getOriginUrl() {
        return originUrl;
    }

    public String getTitle() {
        return title;
    }
}
