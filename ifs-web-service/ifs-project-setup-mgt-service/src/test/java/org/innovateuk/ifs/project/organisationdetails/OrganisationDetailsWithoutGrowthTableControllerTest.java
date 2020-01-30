package org.innovateuk.ifs.project.organisationdetails;

import java.math.BigDecimal;
import java.util.Arrays;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithoutGrowthTableFormPopulator;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationdetails.controller.OrganisationDetailsWithoutGrowthTableController;
import org.innovateuk.ifs.project.organisationdetails.viewmodel.OrganisationDetailsViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.yourorganisation.viewmodel.ProjectYourOrganisationViewModel;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.Silent.class)
public class OrganisationDetailsWithoutGrowthTableControllerTest extends BaseControllerMockMVCTest<OrganisationDetailsWithoutGrowthTableController> {

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private YourOrganisationWithoutGrowthTableFormPopulator withoutGrowthTableFormPopulator;

    @Mock
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Override
    protected OrganisationDetailsWithoutGrowthTableController supplyControllerUnderTest() {
        return new OrganisationDetailsWithoutGrowthTableController();
    }

    private long projectId = 2L;
    private long organisationId = 3L;

    @Test
    public void viewOrganisationDetailsWithNoPartnerOrganisations() throws Exception {
        long competitionId = 1L;
        ProjectResource project = getProject();
        OrganisationResource organisation = getOrganisation();
        OrganisationFinancesWithoutGrowthTableResource finances = getFinances();
        YourOrganisationWithoutGrowthTableForm form = getForm();

        when(projectRestService.getProjectById(projectId)).thenReturn(new RestResult(restSuccess(project)));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(new RestResult(restSuccess(organisation)));
        when(projectYourOrganisationRestService.getOrganisationFinancesWithoutGrowthTable(projectId, organisationId)).thenReturn(serviceSuccess(finances));
        when(withoutGrowthTableFormPopulator.populate(finances)).thenReturn(form);
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(new RestResult(restSuccess(Arrays
            .asList(new PartnerOrganisationResource()))));

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/project/" + projectId + "/organisation/" + organisationId + "/details/without-growth-table"))
            .andExpect(status().isOk())
            .andReturn();

        OrganisationDetailsViewModel orgDetails = (OrganisationDetailsViewModel) result.getModelAndView().getModel().get("orgDetails");
        ProjectYourOrganisationViewModel yourOrganisation = (ProjectYourOrganisationViewModel) result.getModelAndView().getModel().get("yourOrg");
        YourOrganisationWithoutGrowthTableForm actualForm = (YourOrganisationWithoutGrowthTableForm) result.getModelAndView().getModel().get("form");

        assertEquals("project/organisation-details-without-growth-table", result.getModelAndView().getViewName());
        AddressResource expectedAddress = organisation.getAddresses().get(0).getAddress();
        assertEquals(expectedAddress.getAddressLine1(), orgDetails.getAddressLine1());
        assertEquals(expectedAddress.getAddressLine2(), orgDetails.getAddressLine2());
        assertEquals(expectedAddress.getAddressLine3(), orgDetails.getAddressLine3());
        assertEquals(competitionId, orgDetails.getCompetitionId());
        assertEquals(expectedAddress.getCounty(), orgDetails.getCounty());
        assertEquals(organisation.getName(), orgDetails.getOrganisationName());
        assertEquals(organisation.getOrganisationTypeName(), orgDetails.getOrganisationType());
        assertEquals(expectedAddress.getPostcode(), orgDetails.getPostcode());
        assertEquals(projectId, orgDetails.getProjectId());
        assertEquals(project.getName(), orgDetails.getProjectName());
        assertEquals(organisation.getCompaniesHouseNumber(), orgDetails.getRegistrationNumber());
        assertEquals(expectedAddress.getTown(), orgDetails.getTown());

        assertEquals(organisationId, yourOrganisation.getOrganisationId());
        assertEquals(projectId, yourOrganisation.getProjectId());
        assertEquals(project.getName(), yourOrganisation.getProjectName());

        assertEquals(form, actualForm);
    }

    private OrganisationFinancesWithoutGrowthTableResource getFinances() {
        return new OrganisationFinancesWithoutGrowthTableResource(OrganisationSize.SMALL, BigDecimal.valueOf(7), 6L);
    }

    private ProjectResource getProject() {
        ProjectResource project = new ProjectResource();
        project.setId(projectId);
        project.setName("projName");
        return project;
    }

    private OrganisationResource getOrganisation() {
        OrganisationResource organisation = new OrganisationResource();
        organisation.setId(organisationId);
        organisation.setOrganisationTypeName("orgType");
        organisation.setCompaniesHouseNumber("1234");
        AddressResource address = new AddressResource("A", "B", "C", "D", "E", "F");
        OrganisationAddressResource orgAddress = new OrganisationAddressResource();
        orgAddress.setAddress(address);
        organisation.setAddresses(Arrays.asList(orgAddress));

        return organisation;
    }

    private YourOrganisationWithoutGrowthTableForm getForm() {
        return new YourOrganisationWithoutGrowthTableForm();
    }
}

