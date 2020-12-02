package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

import java.math.BigDecimal;
import java.util.List;

public class GenericQuestionReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    private final String displayName;
    private final String question;
    private final String answer;
    private final List<GenericQuestionFileViewModel> appendices;
    private final GenericQuestionFileViewModel templateFile;
    private final String templateDocumentTitle;
    private final long competitionId;
    private final List<String> feedback;
    private final List<BigDecimal> scores;
    private final QuestionResource questionResource;
    private final int inScope;
    private final int totalScope;
    private final boolean hasScope;

    public GenericQuestionReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource questionResource, String displayName, String question, String answer, List<GenericQuestionFileViewModel> appendices, GenericQuestionFileViewModel templateFile, String templateDocumentTitle, List<String> feedback, List<BigDecimal> scores, int inScope, int totalScope, boolean hasScope) {
        super(data, questionResource);
        this.displayName = displayName;
        this.question = question;
        this.answer = answer;
        this.appendices = appendices;
        this.templateFile = templateFile;
        this.templateDocumentTitle = templateDocumentTitle;
        this.competitionId = data.getCompetition().getId();
        this.feedback = feedback;
        this.scores = scores;
        this.questionResource = questionResource;
        this.inScope = inScope;
        this.totalScope = totalScope;
        this.hasScope = hasScope;
    }

    public int getInScope() { return inScope; }

    public int getTotalScope() { return totalScope; }

    public String getDisplayName() {
        return displayName;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public List<GenericQuestionFileViewModel> getAppendices() {
        return appendices;
    }

    public GenericQuestionFileViewModel getTemplateFile() {
        return templateFile;
    }

    public String getTemplateDocumentTitle() {
        return templateDocumentTitle;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public List<String> getFeedback() {
        return feedback;
    }

    public List<BigDecimal> getScores() {
        return scores;
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
        return !feedback.isEmpty();
    }

    @Override
    public boolean hasScore() { return !scores.isEmpty(); }

    public boolean hasScope() {
        return hasScope;
    }

    public boolean hasAssessorResponse() {
        return hasFeedback() && hasScore();
    }

    public int getAssessorMaximumScore() {
        if(!hasScore()) {
            return 0;
        }
        return questionResource.getAssessorMaximumScore();
    };
    
    public BigDecimal getAverageScore() {
        BigDecimal totalScore = BigDecimal.ZERO;
        for (int i = 0; i < scores.size(); i++){
            totalScore = totalScore.add(scores.get(i));
        };

        BigDecimal average = totalScore.divide(BigDecimal.valueOf(scores.size()), 1, BigDecimal.ROUND_HALF_UP);

        return average;
    };
}
