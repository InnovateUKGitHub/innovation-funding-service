package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.common.populator.ApplicationTermsModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationTermsPartnerModelPopulator;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.TermsAndConditionsReadOnlyViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Collections.singleton;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.TERMS_AND_CONDITIONS;

@Component
public class TermsAndConditionsReadOnlyPopulator implements QuestionReadOnlyViewModelPopulator<TermsAndConditionsReadOnlyViewModel> {

    private ApplicationTermsModelPopulator applicationTermsModelPopulator;
    private ApplicationTermsPartnerModelPopulator applicationTermsPartnerModelPopulator;

    public TermsAndConditionsReadOnlyPopulator(ApplicationTermsModelPopulator applicationTermsModelPopulator,
                                               ApplicationTermsPartnerModelPopulator applicationTermsPartnerModelPopulator) {
        this.applicationTermsModelPopulator = applicationTermsModelPopulator;
        this.applicationTermsPartnerModelPopulator = applicationTermsPartnerModelPopulator;
    }

    @Override
    public TermsAndConditionsReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data) {
        return new TermsAndConditionsReadOnlyViewModel(
                data,
                question,
                applicationTermsModelPopulator.populate(data.getModelUser(), data.getApplication().getId(), question.getId(), true),
                applicationTermsPartnerModelPopulator.populate(data.getApplication(), question.getId())
        );
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return singleton(TERMS_AND_CONDITIONS);
    }
}