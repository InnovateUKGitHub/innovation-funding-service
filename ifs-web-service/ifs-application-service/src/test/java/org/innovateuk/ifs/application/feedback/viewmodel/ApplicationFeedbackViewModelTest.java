package org.innovateuk.ifs.application.feedback.viewmodel;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.util.TermsAndConditionsUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationFeedbackViewModelTest {

    @Test
    public void testTermsAndConditionsTerminologyGivenInvestorPartnershipCompetition() {
        // given
        CompetitionResourceBuilder.newCompetitionResource().withFundingType(FundingType.INVESTOR_PARTNERSHIPS).build();
        ApplicationFeedbackViewModel viewModel = new ApplicationFeedbackViewModel(null,
                CompetitionResourceBuilder.newCompetitionResource().withFundingType(FundingType.INVESTOR_PARTNERSHIPS).build(),
                null, null, null, null, false, null, null, null, null, null, null, 1L, false, false);

        // when
        String result = viewModel.getTermsAndConditionsTerminology();

        // then
        assertThat(result).isEqualTo(TermsAndConditionsUtil.TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS);
    }

    @Test
    public void testTermsAndConditionsTerminologyGivenNotInvestorPartnershipCompetition() {
        // given
        CompetitionResourceBuilder.newCompetitionResource().withFundingType(FundingType.GRANT).build();
        ApplicationFeedbackViewModel viewModel = new ApplicationFeedbackViewModel(null,
                CompetitionResourceBuilder.newCompetitionResource().withFundingType(FundingType.GRANT).build(),
                null, null, null, null, false, null, null, null, null, null, null, 1L, false, false);

        // when
        String result = viewModel.getTermsAndConditionsTerminology();

        // then
        assertThat(result).isEqualTo(TermsAndConditionsUtil.TERMS_AND_CONDITIONS_OTHER);
    }

}
