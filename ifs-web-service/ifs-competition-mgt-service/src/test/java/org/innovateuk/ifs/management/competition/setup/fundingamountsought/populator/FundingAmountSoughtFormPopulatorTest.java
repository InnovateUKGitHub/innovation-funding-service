package org.innovateuk.ifs.management.competition.setup.fundingamountsought.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionApplicationConfigRestService;
import org.innovateuk.ifs.management.competition.setup.fundingamountsought.form.FundingAmountSoughtForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionApplicationConfigResourceBuilder.newCompetitionApplicationConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FundingAmountSoughtFormPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private FundingAmountSoughtFormPopulator fundingAmountSoughtFormPopulator;

    @Mock
    private CompetitionApplicationConfigRestService competitionApplicationConfigRestService;

    @Before
    public void setup() {
        fundingAmountSoughtFormPopulator = new FundingAmountSoughtFormPopulator();
    }

    @Test
    public void populateForm() {

        CompetitionApplicationConfigResource competitionApplicationConfig = newCompetitionApplicationConfigResource()
                .withMaximumFundingSoughtEnabled(true)
                .withMaximumFundingSought(new BigDecimal("5000"))
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionApplicationConfig(competitionApplicationConfig)
                .build();

        FundingAmountSoughtForm form = new FundingAmountSoughtFormPopulator().populateForm(competition);

        when(competitionApplicationConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionApplicationConfig));

        assertThat(form.getFundingAmountSoughtApplicable()).isTrue();
        assertEquals(form.getFundingAmountSought(), new BigDecimal(5000));
    }

    @Test
    public void sectionToFill() {
        assertThat(fundingAmountSoughtFormPopulator.sectionToFill()).isEqualTo(CompetitionSetupSection.FUNDING_AMOUNT_SOUGHT);
    }
}
