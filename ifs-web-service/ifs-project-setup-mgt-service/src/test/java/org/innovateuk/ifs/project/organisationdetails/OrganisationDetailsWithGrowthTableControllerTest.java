package org.innovateuk.ifs.project.organisationdetails;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Arrays;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationdetails.controller.OrganisationDetailsWithGrowthTableController;
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
public class OrganisationDetailsWithGrowthTableControllerTest extends BaseControllerMockMVCTest<OrganisationDetailsWithGrowthTableController> {

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private YourOrganisationWithGrowthTableFormPopulator withGrowthTableFormPopulator;

    @Mock
    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Override
    protected OrganisationDetailsWithGrowthTableController supplyControllerUnderTest() {
        return new OrganisationDetailsWithGrowthTableController();
    }

    private long projectId = 2L;
    private long organisationId = 3L;

    @Test
    public void viewOrganisationDetailsWithNoPartnerOrganisations() throws Exception {
        long competitionId = 1L;
        ProjectResource project = getProject();
        OrganisationResource organisation = getOrganisation();
        OrganisationFinancesWithGrowthTableResource finances = getFinances();
        YourOrganisationWithGrowthTableForm form = getForm();

        when(projectRestService.getProjectById(projectId)).thenReturn(new RestResult(restSuccess(project)));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(new RestResult(restSuccess(organisation)));
        when(projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId)).thenReturn(serviceSuccess(finances));
        when(withGrowthTableFormPopulator.populate(finances)).thenReturn(form);
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(new RestResult(restSuccess(Arrays.asList(new PartnerOrganisationResource()))));

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/project/" + projectId + "/organisation/" + organisationId + "/details/with-growth-table"))
            .andExpect(status().isOk())
            .andReturn();

        OrganisationDetailsViewModel orgDetails = (OrganisationDetailsViewModel) result.getModelAndView().getModel().get("orgDetails");
        ProjectYourOrganisationViewModel yourOrganisation = (ProjectYourOrganisationViewModel) result.getModelAndView().getModel().get("yourOrg");
        YourOrganisationWithGrowthTableForm actualForm = (YourOrganisationWithGrowthTableForm) result.getModelAndView().getModel().get("form");

        assertEquals("project/organisation-details-with-growth-table", result.getModelAndView().getViewName());
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

    private OrganisationFinancesWithGrowthTableResource getFinances() {
        return new OrganisationFinancesWithGrowthTableResource(OrganisationSize.SMALL, YearMonth.now(),
            3L, BigDecimal.valueOf(4), BigDecimal.valueOf(5), BigDecimal.valueOf(6), BigDecimal.valueOf(7));
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

    private YourOrganisationWithGrowthTableForm getForm() {
        return new YourOrganisationWithGrowthTableForm();
    }
}
