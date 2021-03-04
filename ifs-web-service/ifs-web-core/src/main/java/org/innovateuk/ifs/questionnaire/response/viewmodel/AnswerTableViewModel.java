package org.innovateuk.ifs.questionnaire.response.viewmodel;

import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class AnswerTableViewModel {

    private final String questionnaireResponseId;
    private final String title;
    private final List<AnsweredQuestionViewModel> questions;
    private final boolean readonly;
    private final String redirectUrl;

    public AnswerTableViewModel(String questionnaireResponseId, String title, List<AnsweredQuestionViewModel> questions, boolean readonly, String redirectUrl) {
        this.questionnaireResponseId = questionnaireResponseId;
        this.title = title;
        this.questions = questions;
        this.readonly = readonly;
        this.redirectUrl = redirectUrl;
    }

    public String getQuestionnaireResponseId() {
        return questionnaireResponseId;
    }

    public String getTitle() {
        return title;
    }

    public List<AnsweredQuestionViewModel> getQuestions() {
        return questions;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getLink() {
        String url = String.format("/questionnaire/%s", questionnaireResponseId);
        if (!isNullOrEmpty(redirectUrl)) {
            url += "?redirectUrl=" + redirectUrl;
        }
        return url;
    }

    public String getLink(long questionId) {
        String url = String.format("/questionnaire/%s/question/%d", questionnaireResponseId, questionId);
        if (!isNullOrEmpty(redirectUrl)) {
            url += "?redirectUrl=" + redirectUrl;
        }
        return url;
    }
}
