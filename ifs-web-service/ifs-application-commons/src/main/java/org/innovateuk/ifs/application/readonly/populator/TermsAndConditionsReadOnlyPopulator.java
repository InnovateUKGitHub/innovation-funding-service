package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.common.populator.ApplicationTermsModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationTermsPartnerModelPopulator;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.TermsAndConditionsReadOnlyViewModel;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Collections.singleton;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.TERMS_AND_CONDITIONS;
import static org.innovateuk.ifs.util.TermsAndConditionsUtil.TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS;
import static org.innovateuk.ifs.util.TermsAndConditionsUtil.VIEW_TERMS_AND_CONDITIONS_OTHER;

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
    public TermsAndConditionsReadOnlyViewModel populate(CompetitionResource competition, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        return new TermsAndConditionsReadOnlyViewModel(
                data,
                question,
                applicationTermsModelPopulator.populate(data.getUser(), data.getApplication().getId(), question.getId(), true),
                applicationTermsPartnerModelPopulator.populate(data.getApplication(), question.getId()),
                settings.isIncludeAssessment(),
                termsAndConditionsTerminology(competition)
        );
    }

    private String termsAndConditionsTerminology(CompetitionResource competitionResource) {
        if(FundingType.INVESTOR_PARTNERSHIPS == competitionResource.getFundingType()) {
            return TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS;
        }
        return VIEW_TERMS_AND_CONDITIONS_OTHER;
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return singleton(TERMS_AND_CONDITIONS);
    }
}