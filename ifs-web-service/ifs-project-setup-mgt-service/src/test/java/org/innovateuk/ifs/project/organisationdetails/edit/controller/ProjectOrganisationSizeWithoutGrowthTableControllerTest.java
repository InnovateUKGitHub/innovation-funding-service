package org.innovateuk.ifs.project.organisationdetails.edit.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormPopulator;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.RequestBuilder;

import java.math.BigDecimal;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.OrganisationFinancesWithoutGrowthTableResourceBuilder.newOrganisationFinancesWithoutGrowthTableResource;
import static org.innovateuk.ifs.finance.resource.OrganisationSize.LARGE;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectOrganisationSizeWithoutGrowthTableControllerTest extends BaseControllerMockMVCTest<ProjectOrganisationSizeWithoutGrowthTableController> {


    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Mock
    private YourOrganisationWithoutGrowthTableFormPopulator viewModelPopulator;

    private static final long projectId = 3L;
    private static final long organisationId = 5L;
    private OrganisationFinancesWithoutGrowthTableResource organisationFinancesWithoutGrowthTableResource;
    private ProjectResource projectResource;
    private OrganisationResource organisationResource;
    private static final String VIEW_WITHOUT_GROWTH_TABLE_PAGE = "project/organisationdetails/edit-organisation-size-without-growth-table";

    @Override
    protected ProjectOrganisationSizeWithoutGrowthTableController supplyControllerUnderTest() {
        return new ProjectOrganisationSizeWithoutGrowthTableController();
    }

    @Before
    public void setup() {
        projectResource = newProjectResource()
                .withId(projectId)
                .build();
        organisationResource = newOrganisationResource()
                .withId(organisationId)
                .withName("SmithZone Ltd")
                .build();
        organisationFinancesWithoutGrowthTableResource = newOrganisationFinancesWithoutGrowthTableResource()
                .withOrganisationSize(OrganisationSize.SMALL)
                .withHeadCount(1L)
                .withTurnover(BigDecimal.valueOf(2))
                .build();
    }

    @Test
    public void viewPage() throws Exception {
        YourOrganisationWithoutGrowthTableForm yourOrganisationWithoutGrowthTableForm = new YourOrganisationWithoutGrowthTableForm();
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(projectResource));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisationResource));
        when(projectYourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId)).thenReturn(serviceSuccess(organisationFinancesWithoutGrowthTableResource));
        when(viewModelPopulator.populate(organisationFinancesWithoutGrowthTableResource)).thenReturn(yourOrganisationWithoutGrowthTableForm);

        mockMvc.perform(get(viewPageUrl()))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_WITHOUT_GROWTH_TABLE_PAGE))
                .andReturn();
    }

    @Test
    public void saveWithoutGrowthTable_success() throws Exception {
        returnSuccessForUpdateWithoutGrowthTable();
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(projectResource));

        mockMvc.perform(postAllFormParameters())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(organisationDetailsPageUrl()))
                .andReturn();

        assertEquals(organisationFinancesWithoutGrowthTableResource.getOrganisationSize(), LARGE);
    }

    @Test
    public void saveWithoutGrowthTable_failure() throws Exception {
        organisationFinancesWithoutGrowthTableResource.setOrganisationSize(null);
        YourOrganisationWithoutGrowthTableForm yourOrganisationWithoutGrowthTableForm = new YourOrganisationWithoutGrowthTableForm();
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(projectResource));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(organisationResource));
        when(projectYourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId)).thenReturn(serviceSuccess(organisationFinancesWithoutGrowthTableResource));
        when(viewModelPopulator.populate(organisationFinancesWithoutGrowthTableResource)).thenReturn(yourOrganisationWithoutGrowthTableForm);

        mockMvc.perform(get(viewPageUrl()))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW_WITHOUT_GROWTH_TABLE_PAGE))
                .andReturn();
    }

    private RequestBuilder postAllFormParameters() {
        organisationFinancesWithoutGrowthTableResource.setOrganisationSize(LARGE);
        return post(viewPageUrl())
                .param("organisationSize", organisationFinancesWithoutGrowthTableResource.getOrganisationSize().toString())
                .param("headCount",
                        organisationFinancesWithoutGrowthTableResource.getHeadCount().toString())
                .param("annualTurnover",
                        organisationFinancesWithoutGrowthTableResource.getTurnover().toString());
    }

    private String viewPageUrl() {
        return format("/project/%d/organisation/%d/edit/without-growth-table",
                projectId, organisationId);
    }

    private String organisationDetailsPageUrl() {
        return format("redirect:/competition/%d/project/%d/organisation/%d/details/without-growth-table",
                projectResource.getCompetition(), projectId, organisationId);
    }

    private void returnSuccessForUpdateWithoutGrowthTable() {
        when(projectYourOrganisationRestService.updateOrganisationFinancesWithoutGrowthTable(eq(projectId), eq(organisationId),
                any())).thenReturn(serviceSuccess());
    }
}