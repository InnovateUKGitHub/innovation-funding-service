package org.innovateuk.ifs.project.organisationdetails;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.organisationdetails.select.controller.SelectOrganisationController;
import org.innovateuk.ifs.project.organisationdetails.select.viewmodel.SelectOrganisationViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SelectOrganisationControllerTest extends BaseControllerMockMVCTest<SelectOrganisationController> {

    @Mock
    private ProjectService projectService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    private long competitionId = 1L;
    private long projectId = 2L;
    private long organisationId = 3L;
    private String projectName = "projName";

    @Override
    protected SelectOrganisationController supplyControllerUnderTest() {
        return new SelectOrganisationController();
    }

    @Test
    public void getSelectOrganisation() throws Exception {
        ProjectResource project = new ProjectResource();
        project.setId(projectId);
        project.setName(projectName);

        when(projectService.getById(projectId)).thenReturn(project);

        List<PartnerOrganisationResource> partners = getPartnerList(false);

        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(new RestResult(restSuccess(partners)));
        when(pendingPartnerProgressRestService.getPendingPartnerProgress(anyLong(), anyLong()))
            .thenReturn(restFailure(GENERAL_NOT_FOUND));

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/project/" + projectId + "/organisation/select"))
            .andExpect(status().isOk())
            .andReturn();

        SelectOrganisationViewModel selectViewModel = (SelectOrganisationViewModel) result.getModelAndView().getModel().get("model");
        String view = "project/organisationdetails/select-organisation";

        assertEquals(view, result.getModelAndView().getViewName());
        assertEquals(competitionId, selectViewModel.getCompetitionId());
        assertTrue(selectViewModel.getPartnerOrganisations().get(0).isLeadOrganisation());
        assertTrue(selectViewModel.getPartnerOrganisations().get(2).getOrganisationName()
            .compareTo(selectViewModel.getPartnerOrganisations().get(1).getOrganisationName()) >= 0);
        assertEquals(projectId, selectViewModel.getProjectId());
        assertEquals(projectName, selectViewModel.getProjectName());
    }

    @Test
    public void getSelectOrganisationWithNoPartnersWithGrowthTable() throws Exception {
        ProjectResource project = new ProjectResource();
        project.setId(projectId);
        project.setName(projectName);

        when(projectService.getById(projectId)).thenReturn(project);

        List<PartnerOrganisationResource> partners = getPartnerList(true);

        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(new RestResult(restSuccess(partners)));
        when(pendingPartnerProgressRestService.getPendingPartnerProgress(anyLong(), anyLong()))
            .thenReturn(restFailure(GENERAL_NOT_FOUND));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(newCompetitionResource().withFundingType(FundingType.GRANT).withIncludeProjectGrowthTable(true).build()));

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/project/" + projectId + "/organisation/select"))
            .andExpect(status().is3xxRedirection())
            .andReturn();

        SelectOrganisationViewModel selectViewModel = (SelectOrganisationViewModel) result.getModelAndView().getModel().get("model");
        String url = String.format("redirect:/competition/%d/project/%d/organisation/%d/details/with-growth-table",
            competitionId, projectId, partners.get(0).getOrganisation());

        assertEquals(url, result.getModelAndView().getViewName());
        assertEquals(competitionId, selectViewModel.getCompetitionId());
        assertTrue(selectViewModel.getPartnerOrganisations().get(0).isLeadOrganisation());
        assertEquals(projectId, selectViewModel.getProjectId());
        assertEquals(projectName, selectViewModel.getProjectName());
    }

    @Test
    public void postSelectOrganisationWithoutGrowthTable() throws Exception {
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(newCompetitionResource().withFundingType(FundingType.GRANT).withIncludeProjectGrowthTable(false).build()));

        MvcResult result = mockMvc.perform(post("/competition/" + competitionId + "/project/" + projectId + "/organisation/select")
            .param("organisationId", String.valueOf(organisationId)))
            .andExpect(status().is3xxRedirection())
            .andReturn();

        String url = String.format("redirect:/competition/%d/project/%d/organisation/%d/details/without-growth-table",
            competitionId, projectId, organisationId);
        assertEquals(url, result.getModelAndView().getViewName());
    }

    private List<PartnerOrganisationResource> getPartnerList(boolean onePartner) {
        PartnerOrganisationResource lead = new PartnerOrganisationResource();
        lead.setOrganisationName("Z");
        lead.setOrganisation(1L);
        lead.setLeadOrganisation(true);
        PartnerOrganisationResource aPartner = new PartnerOrganisationResource();
        aPartner.setOrganisationName("A");
        aPartner.setOrganisation(2L);
        PartnerOrganisationResource bPartner = new PartnerOrganisationResource();
        bPartner.setOrganisation(3L);
        bPartner.setOrganisationName("B");

        return onePartner ? Arrays.asList(lead) : Arrays.asList(bPartner, aPartner, lead);
    }
}