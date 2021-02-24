package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

public class SubsidyBasisReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    public SubsidyBasisReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource question) {
        super(data, question);
    }

    @Override
    public String getFragment() {
        return "subsidy-basis";
    }

}

