package org.innovateuk.ifs.competitionsetup.stakeholder.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestService;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.competitionsetup.stakeholder.form.InviteStakeholderForm;
import org.innovateuk.ifs.competitionsetup.stakeholder.populator.ManageStakeholderModelPopulator;
import org.innovateuk.ifs.competitionsetup.stakeholder.viewmodel.ManageStakeholderViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static java.util.Collections.*;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.STAKEHOLDER_INVITE_INVALID_EMAIL;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Class for testing public functions of {@link CompetitionSetupStakeholderController}
 */
@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupStakeholderControllerTest extends BaseControllerMockMVCTest<CompetitionSetupStakeholderController> {
    private static final Long COMPETITION_ID = 12L;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ManageStakeholderModelPopulator manageStakeholderModelPopulator;

    @Mock
    private CompetitionSetupStakeholderRestService competitionSetupStakeholderRestService;

    @Override
    protected CompetitionSetupStakeholderController supplyControllerUnderTest() { return new CompetitionSetupStakeholderController(); }

    @Override
    @Before
    public void setUp() {
        super.setUp();
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(true);
    }

    @Test
    public void manageStakeholders() throws Exception {

        String competitionName = "competitionName";

        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withId(COMPETITION_ID)
                .withName(competitionName)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID))
                .thenReturn(restSuccess(competitionResource));

        ManageStakeholderViewModel viewModel = new ManageStakeholderViewModel(COMPETITION_ID, competitionName, emptyList(), emptyList());
        when(manageStakeholderModelPopulator.populateModel(competitionResource))
                .thenReturn(viewModel);

        mockMvc.perform(MockMvcRequestBuilders.get("/competition/setup/{competitionId}/manage-stakeholders", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/manage-stakeholders"))
                .andExpect(model().attribute("model", viewModel))
                .andExpect(model().attribute("form", new InviteStakeholderForm()));
    }

    @Test
    public void inviteStakeholderWhenInviteFails() throws Exception {

        when(competitionSetupStakeholderRestService.inviteStakeholder(any(), eq(COMPETITION_ID)))
                .thenReturn(restFailure(STAKEHOLDER_INVITE_INVALID_EMAIL));

        String competitionName = "competitionName";
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withId(COMPETITION_ID)
                .withName(competitionName)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID))
                .thenReturn(restSuccess(competitionResource));

        ManageStakeholderViewModel viewModel = new ManageStakeholderViewModel(COMPETITION_ID, competitionName, emptyList(), emptyList());
        when(manageStakeholderModelPopulator.populateModel(competitionResource))
                .thenReturn(viewModel);

        mockMvc.perform(MockMvcRequestBuilders.post("/competition/setup/{competitionId}/manage-stakeholders?inviteStakeholder=inviteStakeholder", COMPETITION_ID).
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@asdf.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/manage-stakeholders"));
    }

    @Test
    public void inviteStakeholderSuccess() throws Exception {

        when(competitionSetupStakeholderRestService.inviteStakeholder(any(), eq(COMPETITION_ID))).thenReturn(restSuccess());

        mockMvc.perform(MockMvcRequestBuilders.post("/competition/setup/{competitionId}/manage-stakeholders?inviteStakeholder=inviteStakeholder", COMPETITION_ID).
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@asdf.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/" + COMPETITION_ID + "/manage-stakeholders"));
    }
}


