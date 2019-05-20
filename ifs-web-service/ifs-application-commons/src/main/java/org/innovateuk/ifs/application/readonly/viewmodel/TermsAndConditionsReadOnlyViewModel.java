package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

public class TermsAndConditionsReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    public TermsAndConditionsReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource question) {
        super(data, question);
    }

    @Override
    public String getFragment() {
        return "terms-and-conditions";
    }

    @Override
    public boolean shouldDisplayMarkAsComplete() {
        return false;
    }

}