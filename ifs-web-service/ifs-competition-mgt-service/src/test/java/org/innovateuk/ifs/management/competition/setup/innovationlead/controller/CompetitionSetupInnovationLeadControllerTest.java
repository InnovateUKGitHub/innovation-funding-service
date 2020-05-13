package org.innovateuk.ifs.management.competition.setup.innovationlead.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupInnovationLeadService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.initialdetail.populator.ManageInnovationLeadsModelPopulator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import static java.lang.Boolean.*;
import static java.lang.Boolean.FALSE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_WITH_ASSESSORS_CANNOT_BE_DELETED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class for testing public functions of {@link CompetitionSetupInnovationLeadController}
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionSetupInnovationLeadControllerTest extends BaseControllerMockMVCTest<CompetitionSetupInnovationLeadController> {

    private static final Long COMPETITION_ID = 12L;
    private static final String URL_PREFIX = "/competition/setup";

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private CompetitionSetupInnovationLeadService competitionSetupInnovationLeadService;

    @Mock
    private ManageInnovationLeadsModelPopulator manageInnovationLeadsModelPopulator;

    @Override
    protected CompetitionSetupInnovationLeadController supplyControllerUnderTest() {
        return new CompetitionSetupInnovationLeadController();
    }

    @Test
    public void manageInnovationLeadWhenInitialDetailsNotComplete() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(FALSE);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/manage-innovation-leads/find"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/" + COMPETITION_ID));

        verify(manageInnovationLeadsModelPopulator, never()).populateModel(any());
    }

    @Test
    public void manageInnovationLead() throws Exception {

        CompetitionResource competitionResource = newCompetitionResource()
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(TRUE);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/manage-innovation-leads/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-innovation-leads-find"));

        verify(manageInnovationLeadsModelPopulator).populateModel(any());
    }

    @Test
    public void manageInnovationLeadOverviewWhenInitialDetailsNotComplete() throws Exception {

        CompetitionResource competitionResource = newCompetitionResource().build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(FALSE);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/manage-innovation-leads/overview"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/" + COMPETITION_ID));

        verify(manageInnovationLeadsModelPopulator, never()).populateModel(any());
    }

    @Test
    public void manageInnovationLeadOverview() throws Exception {

        CompetitionResource competitionResource = newCompetitionResource()
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(TRUE);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/manage-innovation-leads/overview"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-innovation-leads-overview"));

        verify(manageInnovationLeadsModelPopulator).populateModel(any());
    }

    @Test
    public void addInnovationLeadWhenInitialDetailsNotComplete() throws Exception {
        Long innovationLeadUserId = 2L;
        CompetitionResource competitionResource = newCompetitionResource().build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(FALSE);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/add-innovation-lead/" + innovationLeadUserId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/" + COMPETITION_ID));

        verify(competitionSetupInnovationLeadService, never()).addInnovationLead(COMPETITION_ID, innovationLeadUserId);
        verify(manageInnovationLeadsModelPopulator, never()).populateModel(any());
    }

    @Test
    public void addInnovationLead() throws Exception {
        Long innovationLeadUserId = 2L;
        CompetitionResource competitionResource = newCompetitionResource()
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupInnovationLeadService.addInnovationLead(COMPETITION_ID, innovationLeadUserId)).thenReturn(serviceSuccess());
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(TRUE);


        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/add-innovation-lead/" + innovationLeadUserId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-innovation-leads-find"));

        verify(competitionSetupInnovationLeadService).addInnovationLead(COMPETITION_ID, innovationLeadUserId);
        verify(manageInnovationLeadsModelPopulator).populateModel(any());
    }

    @Test
    public void removeInnovationLeadWhenInitialDetailsNotComplete() throws Exception {

        CompetitionResource competitionResource = newCompetitionResource().build();

        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(FALSE);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        Long innovationLeadUserId = 2L;
        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/remove-innovation-lead/" + innovationLeadUserId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/" + COMPETITION_ID));

        verify(competitionSetupInnovationLeadService, never()).removeInnovationLead(COMPETITION_ID, innovationLeadUserId);
        verify(manageInnovationLeadsModelPopulator, never()).populateModel(any());
    }

    @Test
    public void removeInnovationLead() throws Exception {
        Long innovationLeadUserId = 2L;
        CompetitionResource competitionResource = newCompetitionResource()
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupInnovationLeadService.removeInnovationLead(COMPETITION_ID, innovationLeadUserId)).thenReturn(serviceSuccess());
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(TRUE);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/remove-innovation-lead/" + innovationLeadUserId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-innovation-leads-overview"));

        verify(competitionSetupInnovationLeadService).removeInnovationLead(COMPETITION_ID, innovationLeadUserId);
        verify(manageInnovationLeadsModelPopulator).populateModel(any());
    }

    public void removeInnovationLeadFailure() throws Exception {
        Long innovationLeadUserId = 2L;
        CompetitionResource competitionResource = newCompetitionResource()
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupInnovationLeadService.removeInnovationLead(COMPETITION_ID, innovationLeadUserId)).thenReturn(
                serviceFailure(new Error(COMPETITION_WITH_ASSESSORS_CANNOT_BE_DELETED, HttpStatus.BAD_REQUEST)));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/remove-innovation-lead/" + innovationLeadUserId))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(view().name("competition/setup"))
                .andReturn();
    }
}