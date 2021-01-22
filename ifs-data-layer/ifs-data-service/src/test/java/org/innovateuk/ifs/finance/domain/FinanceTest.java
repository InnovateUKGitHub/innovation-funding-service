package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.category.builder.ResearchCategoryBuilder.newResearchCategory;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.domain.builder.GrantClaimMaximumBuilder.newGrantClaimMaximum;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FinanceTest {
    private static Organisation business = newOrganisation().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();

    @Test
    public void getMaximumFundingLevel_notBusiness() {
        Organisation organisation = newOrganisation().withOrganisationType(OrganisationTypeEnum.RESEARCH).build();
        TestFinance finance = new TestFinance();
        finance.setOrganisation(organisation);

        assertThat(finance.getMaximumFundingLevel(), is(equalTo(100)));
    }

    @Test
    public void getMaximumFundingLevel_fullyFunded() {
        Competition competition = spy(newCompetition().build());
        Application application = newApplication()
                .withCompetition(competition)
                .build();
        TestFinance finance = new TestFinance(application);
        finance.setOrganisation(business);
        when(competition.isFullyFunded()).thenReturn(true);

        assertThat(finance.getMaximumFundingLevel(), is(equalTo(100)));
    }


    @Test
    public void getMaximumFundingLevel_nullFundingRules_constant() {
        Competition competition = spy(newCompetition()
                .withGrantClaimMaximums(newGrantClaimMaximum()
                .withMaximum(1)
                .build(3))
                .build());
        Application application = newApplication()
                .withCompetition(competition)
                .build();
        TestFinance finance = new TestFinance(application);
        finance.setOrganisation(business);
        when(competition.isFullyFunded()).thenReturn(false);

        assertThat(finance.getMaximumFundingLevel(), is(equalTo(1)));
    }

    @Test
    public void getMaximumFundingLevel_nullFundingRules_match() {
        ResearchCategory match = newResearchCategory().build();
        Competition competition = spy(newCompetition()
                .withGrantClaimMaximums(newGrantClaimMaximum()
                        .withMaximum(10, 30, 40)
                        .withResearchCategory(match, newResearchCategory().build(), newResearchCategory().build())
                        .withSize(OrganisationSize.LARGE, OrganisationSize.MEDIUM, OrganisationSize.SMALL)
                        .build(3))
                .build());
        Application application = newApplication()
                .withCompetition(competition)
                .withResearchCategory(match)
                .build();
        TestFinance finance = new TestFinance(application);
        finance.setOrganisation(business);
        finance.setOrganisationSize(OrganisationSize.LARGE);
        when(competition.isFullyFunded()).thenReturn(false);

        assertThat(finance.getMaximumFundingLevel(), is(equalTo(10)));
    }

    @Test
    public void getMaximumFundingLevel_fundingRules_constant() {
        Competition competition = spy(newCompetition()
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .withGrantClaimMaximums(newGrantClaimMaximum()
                        .withMaximum(1, 1, 50, 60)
                        .withFundingRules(FundingRules.STATE_AID, FundingRules.STATE_AID, FundingRules.SUBSIDY_CONTROL, FundingRules.SUBSIDY_CONTROL)
                        .build(4))
                .build());
        Application application = newApplication()
                .withCompetition(competition)
                .build();
        TestFinance finance = new TestFinance(application);
        finance.setOrganisation(business);
        finance.setNorthernIrelandDeclaration(true);
        when(competition.isFullyFunded()).thenReturn(false);

        assertThat(finance.getMaximumFundingLevel(), is(equalTo(1)));
    }
    @Test
    public void getMaximumFundingLevel_fundingRules_match() {
        ResearchCategory match = newResearchCategory().build();
        Competition competition = spy(newCompetition()
                .withFundingRules(FundingRules.SUBSIDY_CONTROL)
                .withGrantClaimMaximums(newGrantClaimMaximum()
                        .withMaximum(10, 20, 30, 40)
                        .withResearchCategory(match, match, newResearchCategory().build(), newResearchCategory().build())
                        .withSize(OrganisationSize.LARGE, OrganisationSize.LARGE, OrganisationSize.MEDIUM, OrganisationSize.SMALL)
                        .withFundingRules(FundingRules.SUBSIDY_CONTROL, FundingRules.STATE_AID, FundingRules.SUBSIDY_CONTROL, FundingRules.SUBSIDY_CONTROL)
                        .build(4))
                .build());
        Application application = newApplication()
                .withCompetition(competition)
                .withResearchCategory(match)
                .build();
        TestFinance finance = new TestFinance(application);
        finance.setOrganisation(business);
        finance.setOrganisationSize(OrganisationSize.LARGE);
        finance.setNorthernIrelandDeclaration(true);
        when(competition.isFullyFunded()).thenReturn(false);

        assertThat(finance.getMaximumFundingLevel(), is(equalTo(20)));
    }

    private static class TestFinance extends Finance {
        private final Application application;

        private TestFinance() {
            this.application = null;
        }
        private TestFinance(Application application) {
            this.application = application;
        }

        @Override
        public Application getApplication() {
            return application;
        }
    }
}