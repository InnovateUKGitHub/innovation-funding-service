package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

public class GenericQuestionReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    private final String displayName;
    private final String question;
    private final String answer;
    private final String appendixFilename;
    private final String appendixUrl;
    private final Long appendixId;
    private final String templateDocumentFilename;
    private final String templateDocumentUrl;
    private final String templateDocumentTitle;
    private final Long templateDocumentId;
    private final long competitionId;
    private final String feedback;
    private final String score;

    public GenericQuestionReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource questionResource, String displayName, String question, String answer, String appendixFilename, String appendixUrl, Long appendixId, String templateDocumentFilename, String templateDocumentUrl, String templateDocumentTitle, Long templateDocumentId, String feedback, String score) {
        super(data, questionResource);
        this.displayName = displayName;
        this.question = question;
        this.answer = answer;
        this.appendixFilename = appendixFilename;
        this.appendixUrl = appendixUrl;
        this.appendixId = appendixId;
        this.templateDocumentFilename = templateDocumentFilename;
        this.templateDocumentUrl = templateDocumentUrl;
        this.templateDocumentTitle = templateDocumentTitle;
        this.templateDocumentId = templateDocumentId;
        this.competitionId = data.getCompetition().getId();
        this.feedback = feedback;
        this.score = score;
    }

    public String getDisplayName() {
        return displayName;
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

    public String getAppendixUrl() {
        return appendixUrl;
    }

    public Long getAppendixId() {
        return appendixId;
    }

    public String getTemplateDocumentFilename() {
        return templateDocumentFilename;
    }

    public String getTemplateDocumentUrl() {
        return templateDocumentUrl;
    }

    public String getTemplateDocumentTitle() {
        return templateDocumentTitle;
    }

    public Long getTemplateDocumentId() {
        return templateDocumentId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getScore() {
        return score;
    }


    @Override
    public String getName() {
        return displayName;
    }

    @Override
    public String getFragment() {
        return "generic";
    }

    public boolean hasFeedback() {
        return feedback != null;
    }
    public boolean hasScore() {
        return score != null;
    }
    public boolean hasAssessorResponse() {
        return hasFeedback() && hasScore();
    }
}
