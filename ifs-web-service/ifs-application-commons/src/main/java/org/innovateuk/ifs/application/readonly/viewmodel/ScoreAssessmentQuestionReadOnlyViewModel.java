package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

public class ScoreAssessmentQuestionReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    public ScoreAssessmentQuestionReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource question) {
        super(data, question);
    }

    @Override
    public String getFragment() {
        return "ktp-assessment";
    }

    @Override
    public boolean isDisplayCompleteStatus() {
        return false;
    }
}
