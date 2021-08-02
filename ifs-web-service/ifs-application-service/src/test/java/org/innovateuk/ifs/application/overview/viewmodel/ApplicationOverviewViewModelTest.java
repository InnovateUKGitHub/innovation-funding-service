package org.innovateuk.ifs.application.overview.viewmodel;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.util.TermsAndConditionsUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationOverviewViewModelTest {

    @Test
    public void termsAndConditionsTerminologyGivenInvestorPartnershipCompetition() {
        // given
        String termsTemplate = "terms-template";
        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.INVESTOR_PARTNERSHIPS)
                .withTermsAndConditions(grantTermsAndConditions)
                .build();
        ApplicationOverviewViewModel viewModel = new ApplicationOverviewViewModel(null,
                competition,
                null, null, null, null, new CompetitionThirdPartyConfigResource());

        // when
        String result = viewModel.getTermsAndConditionsTerminology();

        // then
        assertThat(result).isEqualTo(TermsAndConditionsUtil.TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS);
    }

    @Test
    public void termsAndConditionsTerminologyGivenNotInvestorPartnershipCompetition() {
        // given
        String termsTemplate = "terms-template";
        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .withTermsAndConditions(grantTermsAndConditions)
                .build();
        ApplicationOverviewViewModel viewModel = new ApplicationOverviewViewModel(null,
                competition,
                null, null, null, null, new CompetitionThirdPartyConfigResource());

        // when
        String result = viewModel.getTermsAndConditionsTerminology();

        // then
        assertThat(result).isEqualTo(TermsAndConditionsUtil.TERMS_AND_CONDITIONS_OTHER);
    }

}
