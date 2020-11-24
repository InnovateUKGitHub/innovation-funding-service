package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.ScoreAssessmentQuestionReadOnlyViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class ScoreAssessmentQuestionReadOnlyPopulator implements QuestionReadOnlyViewModelPopulator<ScoreAssessmentQuestionReadOnlyViewModel> {

    @Override
    public ScoreAssessmentQuestionReadOnlyViewModel populate(CompetitionResource competition, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        return new ScoreAssessmentQuestionReadOnlyViewModel(data, question);
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return Collections.singleton(QuestionSetupType.KTP_ASSESSMENT);
    }
}
