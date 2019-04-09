package org.innovateuk.ifs.application.summary.viewmodel;

public class GenericQuestionSummaryViewModel implements NewQuestionSummaryViewModel {

    private final String name;
    private final String question;
    private final String answer;
    private final String appendixFilename;
    private final long applicationId;
    private final long questionId;
    private final Long appendixId;

    public GenericQuestionSummaryViewModel(String name, String question, String answer, String appendixFilename, long applicationId, long questionId, Long appendixId) {
        this.name = name;
        this.question = question;
        this.answer = answer;
        this.appendixFilename = appendixFilename;
        this.applicationId = applicationId;
        this.questionId = questionId;
        this.appendixId = appendixId;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAppendixFilename() {
        return appendixFilename;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public Long getAppendixId() {
        return appendixId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFragment() {
        return "generic";
    }
}
