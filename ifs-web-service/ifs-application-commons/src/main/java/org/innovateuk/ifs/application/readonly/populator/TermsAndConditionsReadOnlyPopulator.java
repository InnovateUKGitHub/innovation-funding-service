package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.common.populator.ApplicationTermsModelPopulator;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.TermsAndConditionsReadOnlyViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Collections.singleton;

@Component
public class TermsAndConditionsReadOnlyPopulator implements QuestionReadOnlyViewModelPopulator<TermsAndConditionsReadOnlyViewModel> {

    private ApplicationTermsModelPopulator applicationTermsModelPopulator;

    public TermsAndConditionsReadOnlyPopulator(ApplicationTermsModelPopulator applicationTermsModelPopulator) {
        this.applicationTermsModelPopulator = applicationTermsModelPopulator;
    }

    @Override
    public TermsAndConditionsReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data) {
        return new TermsAndConditionsReadOnlyViewModel(
                data, question, applicationTermsModelPopulator.populate(data.getUser(), data.getApplication().getId(), question.getId())
        );
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return singleton(QuestionSetupType.TERMS_AND_CONDITIONS);
    }
}