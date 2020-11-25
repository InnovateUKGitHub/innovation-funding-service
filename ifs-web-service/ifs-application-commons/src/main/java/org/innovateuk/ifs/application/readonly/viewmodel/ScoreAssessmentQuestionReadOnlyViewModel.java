package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

import java.math.BigDecimal;
import java.util.List;

public class ScoreAssessmentQuestionReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    private final QuestionResource question;
    private final List<String> feedback;
    private final List<BigDecimal> scores;

    public ScoreAssessmentQuestionReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource question,
                                                    List<String> feedback, List<BigDecimal> scores) {
        super(data, question);
        this.question = question;
        this.feedback = feedback;
        this.scores = scores;
    }

    public boolean hasFeedback() {
        return !feedback.isEmpty();
    }

    public List<String> getFeedback() {
        return feedback;
    }

    @Override
    public boolean hasScore() {
        return !scores.isEmpty();
    }

    public List<BigDecimal> getScores() {
        return scores;
    }

    public BigDecimal getAverageScore() {
        BigDecimal totalScore = scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = totalScore.divide(BigDecimal.valueOf(scores.size()), 1, BigDecimal.ROUND_HALF_UP);
        return average;
    };

    public int getAssessorMaximumScore() {
        return question.getAssessorMaximumScore();
    };

    @Override
    public String getFragment() {
        return "score-assessment";
    }

    @Override
    public boolean isDisplayCompleteStatus() {
        return false;
    }
}
