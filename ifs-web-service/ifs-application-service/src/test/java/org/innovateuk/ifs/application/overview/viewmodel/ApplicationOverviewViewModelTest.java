package org.innovateuk.ifs.application.overview.viewmodel;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.util.TermsAndConditionsUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationOverviewViewModelTest {

    @Test
    public void termsAndConditionsTerminologyGivenInvestorPartnershipCompetition() {
        // given
        CompetitionResourceBuilder.newCompetitionResource().withFundingType(FundingType.INVESTOR_PARTNERSHIPS).build();
        ApplicationOverviewViewModel viewModel = new ApplicationOverviewViewModel(null,
                CompetitionResourceBuilder.newCompetitionResource().withFundingType(FundingType.INVESTOR_PARTNERSHIPS).build(),
                null, null, null, null);

        // when
        String result = viewModel.getTermsAndConditionsTerminology();

        // then
        assertThat(result).isEqualTo(TermsAndConditionsUtil.TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS);
    }

    @Test
    public void termsAndConditionsTerminologyGivenNotInvestorPartnershipCompetition() {
        // given
        CompetitionResourceBuilder.newCompetitionResource().withFundingType(FundingType.GRANT).build();
        ApplicationOverviewViewModel viewModel = new ApplicationOverviewViewModel(null,
                CompetitionResourceBuilder.newCompetitionResource().withFundingType(FundingType.GRANT).build(),
                null, null, null, null);

        // when
        String result = viewModel.getTermsAndConditionsTerminology();

        // then
        assertThat(result).isEqualTo(TermsAndConditionsUtil.TERMS_AND_CONDITIONS_OTHER);
    }

}
