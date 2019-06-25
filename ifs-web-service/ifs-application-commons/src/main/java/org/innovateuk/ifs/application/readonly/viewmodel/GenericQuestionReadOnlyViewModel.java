package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

public class GenericQuestionReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    private final String displayName;
    private final String question;
    private final String answer;
    private final String appendixFilename;
    private final Long appendixId;
    private final long competitionId;

    public GenericQuestionReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource questionResource, String displayName, String question, String answer, String appendixFilename, Long appendixId) {
        super(data, questionResource);
        this.displayName = displayName;
        this.question = question;
        this.answer = answer;
        this.appendixFilename = appendixFilename;
        this.appendixId = appendixId;
        this.competitionId = data.getCompetition().getId();
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

    public long getCompetitionId() {
        return competitionId;
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
