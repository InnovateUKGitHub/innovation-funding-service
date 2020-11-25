package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.ScoreAssessmentQuestionReadOnlyViewModel;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ScoreAssessmentQuestionReadOnlyPopulator implements QuestionReadOnlyViewModelPopulator<ScoreAssessmentQuestionReadOnlyViewModel> {

    @Override
    public ScoreAssessmentQuestionReadOnlyViewModel populate(CompetitionResource competition, QuestionResource question,
                                                             ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        return new ScoreAssessmentQuestionReadOnlyViewModel(data, question, allFeedback(data, question, settings), allScores(data, question, settings));
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return Collections.singleton(QuestionSetupType.KTP_ASSESSMENT);
    }

    private List<String> allFeedback(ApplicationReadOnlyData data, QuestionResource question, ApplicationReadOnlySettings settings) {
        if (settings.isIncludeAssessment()) {
            return data.getAssessmentToApplicationAssessment().values()
                    .stream()
                    .map(ApplicationAssessmentResource::getFeedback)
                    .filter(feedbackMap -> feedbackMap.containsKey(question.getId()))
                    .map(feedbackMap -> feedbackMap.get(question.getId()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private List<BigDecimal> allScores(ApplicationReadOnlyData data, QuestionResource question, ApplicationReadOnlySettings settings) {
        if (settings.isIncludeAssessment()) {
            return data.getAssessmentToApplicationAssessment().values()
                    .stream()
                    .map(ApplicationAssessmentResource::getScores)
                    .filter(scoresMap -> scoresMap.containsKey(question.getId()))
                    .map(scoresMap -> scoresMap.get(question.getId()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
