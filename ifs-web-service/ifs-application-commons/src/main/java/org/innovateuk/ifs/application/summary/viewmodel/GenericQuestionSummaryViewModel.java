package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.form.resource.QuestionResource;

public class GenericQuestionSummaryViewModel extends AbstractQuestionSummaryViewModel implements NewQuestionSummaryViewModel {

    private final String displayName;
    private final String question;
    private final String answer;
    private final String appendixFilename;
    private final Long appendixId;

    public GenericQuestionSummaryViewModel(ApplicationSummaryData data, QuestionResource questionResource, String displayName, String question, String answer, String appendixFilename, Long appendixId) {
        super(data, questionResource);
        this.displayName = displayName;
        this.question = question;
        this.answer = answer;
        this.appendixFilename = appendixFilename;
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

    public Long getAppendixId() {
        return appendixId;
    }

    @Override
    public String getName() {
        return displayName;
    }

    @Override
    public String getFragment() {
        return "generic";
    }

}
