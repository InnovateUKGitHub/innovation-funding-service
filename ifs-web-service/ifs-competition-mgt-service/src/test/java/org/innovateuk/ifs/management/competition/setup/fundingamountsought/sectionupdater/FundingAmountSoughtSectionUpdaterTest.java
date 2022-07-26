package org.innovateuk.ifs.management.competition.setup.fundingamountsought.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionApplicationConfigRestService;
import org.innovateuk.ifs.management.competition.setup.fundingamountsought.form.FundingAmountSoughtForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionApplicationConfigResourceBuilder.newCompetitionApplicationConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FundingAmountSoughtSectionUpdaterTest {

    @InjectMocks
    private FundingAmountSoughtSectionUpdater updater;

    @Mock
    private CompetitionApplicationConfigRestService competitionApplicationConfigRestService;


    @Test
    public void update_withFundingAmountSoughtApplicable() {

        CompetitionApplicationConfigResource competitionApplicationConfigResource =
                newCompetitionApplicationConfigResource()
                        .build();

        CompetitionResource competition = newCompetitionResource().build();
        FundingAmountSoughtForm fundingAmountSoughtForm = new FundingAmountSoughtForm();
        fundingAmountSoughtForm.setFundingAmountSoughtApplicable(true);
        fundingAmountSoughtForm.setFundingAmountSought(new BigDecimal("1000"));

        UserResource loggedInUser = newUserResource().build();

        when(competitionApplicationConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionApplicationConfigResource));
        when(competitionApplicationConfigRestService.update(competition.getId(), competitionApplicationConfigResource)).thenReturn(restSuccess(competitionApplicationConfigResource));

        ServiceResult<Void> result = updater.doSaveSection(competition, fundingAmountSoughtForm, loggedInUser);

        assertTrue(result.isSuccess());
        assertTrue(competitionApplicationConfigResource.isMaximumFundingSoughtEnabled());
        assertEquals(new BigDecimal("1000.00"), competitionApplicationConfigResource.getMaximumFundingSought());

        verify(competitionApplicationConfigRestService).findOneByCompetitionId(competition.getId());
        verify(competitionApplicationConfigRestService).update(competition.getId(), competitionApplicationConfigResource);
    }

    @Test
    public void update_withoutFundingAmountSoughtApplicable() {

        CompetitionApplicationConfigResource competitionApplicationConfigResource =
                newCompetitionApplicationConfigResource()
                        .build();

        CompetitionResource competition = newCompetitionResource().build();
        FundingAmountSoughtForm fundingAmountSoughtForm = new FundingAmountSoughtForm();
        fundingAmountSoughtForm.setFundingAmountSoughtApplicable(false);
        fundingAmountSoughtForm.setFundingAmountSought(null);

        UserResource loggedInUser = newUserResource().build();

        when(competitionApplicationConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionApplicationConfigResource));
        when(competitionApplicationConfigRestService.update(competition.getId(), competitionApplicationConfigResource)).thenReturn(restSuccess(competitionApplicationConfigResource));

        ServiceResult<Void> result = updater.doSaveSection(competition, fundingAmountSoughtForm, loggedInUser);

        assertTrue(result.isSuccess());
        assertFalse(competitionApplicationConfigResource.isMaximumFundingSoughtEnabled());
        assertNull(competitionApplicationConfigResource.getMaximumFundingSought());

        verify(competitionApplicationConfigRestService).findOneByCompetitionId(competition.getId());
        verify(competitionApplicationConfigRestService).update(competition.getId(), competitionApplicationConfigResource);
    }
}
