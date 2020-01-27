package org.innovateuk.ifs.project.organisationdetails;

import java.util.Arrays;
import java.util.List;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.service.YourOrganisationRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.organisationdetails.controller.SelectOrganisationController;
import org.innovateuk.ifs.project.organisationdetails.viewmodel.SelectOrganisationViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SelectOrganisationControllerTest extends BaseControllerMockMVCTest<SelectOrganisationController> {

    @Mock
    ProjectService projectService;

    @Mock
    PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    YourOrganisationRestService yourOrganisationRestService;

    long competitionId = 1L;
    long projectId = 2L;
    long organisationId = 3L;
    String projectName = "projName";

    @Override
    protected SelectOrganisationController supplyControllerUnderTest() {
        return new SelectOrganisationController();
    }

    @Before
    public void setup() {

    }

    @Test
    public void getSelectOrganisation() throws Exception {
        ProjectResource project = new ProjectResource();
        project.setId(projectId);
        project.setName(projectName);

        when(projectService.getById(projectId)).thenReturn(project);

        List<PartnerOrganisationResource> partners = getPartnerList();

        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(new RestResult(restSuccess(partners)));

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/project/" + projectId + "/organisation/select"))
            .andExpect(status().isOk())
            .andReturn();

        SelectOrganisationViewModel selectViewModel = (SelectOrganisationViewModel) result.getModelAndView().getModel().get("model");
        String url = "project/select-organisation";

        assertEquals(url, result.getModelAndView().getViewName());
        assertEquals(competitionId, selectViewModel.getCompetitionId());
        assertTrue(selectViewModel.getPartnerOrganisations().get(0).isLeadOrganisation());
        assertTrue(selectViewModel.getPartnerOrganisations().get(2).getOrganisationName()
            .compareTo(selectViewModel.getPartnerOrganisations().get(1).getOrganisationName()) >= 0);
        assertEquals(projectId, selectViewModel.getProjectId());
        assertEquals(projectName, selectViewModel.getProjectName());
    }

    @Test
    public void postSelectOrganisationWithGrowthTable() throws Exception {
        when(yourOrganisationRestService.isIncludingGrowthTable(competitionId)).thenReturn(serviceSuccess(true));

        MvcResult result = mockMvc.perform(post("/competition/" + competitionId + "/project/" + projectId + "/organisation/select")
            .param("organisationId", String.valueOf(organisationId)))
            .andExpect(status().is3xxRedirection())
            .andReturn();

        String url = "redirect:/competition/1/project/2/organisation/3/details/with-growth-table";
        assertEquals(url, result.getModelAndView().getViewName());
    }

    @Test
    public void postSelectOrganisationWithoutGrowthTable() throws Exception {
        when(yourOrganisationRestService.isIncludingGrowthTable(competitionId)).thenReturn(serviceSuccess(false));

        MvcResult result = mockMvc.perform(post("/competition/" + competitionId + "/project/" + projectId + "/organisation/select")
            .param("organisationId", String.valueOf(organisationId)))
            .andExpect(status().is3xxRedirection())
            .andReturn();

        String url = "redirect:/competition/1/project/2/organisation/3/details/without-growth-table";
        assertEquals(url, result.getModelAndView().getViewName());
    }

    private List<PartnerOrganisationResource> getPartnerList() {
        PartnerOrganisationResource lead = new PartnerOrganisationResource();
        lead.setOrganisationName("Z");
        lead.setLeadOrganisation(true);
        PartnerOrganisationResource aPartner = new PartnerOrganisationResource();
        aPartner.setOrganisationName("A");
        PartnerOrganisationResource bPartner = new PartnerOrganisationResource();
        bPartner.setOrganisationName("B");

        return Arrays.asList(bPartner, aPartner, lead);
    }
}