package org.innovateuk.ifs.origin;

public enum AssignQuestionOrigin implements BackLinkOrigin {

    APPLICATION_QUESTION("/application/{applicationId}/form/question/{questionId}", "Question"),
    APPLICATION("/application/{applicationId}", "Application overview");

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
