package org.innovateuk.ifs.project.organisationdetails;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableForm;
import org.innovateuk.ifs.application.forms.sections.yourorganisation.form.YourOrganisationWithGrowthTableFormPopulator;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckPartnerStatusResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.organisationdetails.view.controller.OrganisationDetailsWithGrowthTableController;
import org.innovateuk.ifs.project.organisationdetails.view.viewmodel.OrganisationDetailsViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.yourorganisation.viewmodel.ProjectYourOrganisationViewModel;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.GRANT;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckSummaryResourceBuilder.newFinanceCheckSummaryResource;
import static org.innovateuk.ifs.project.finance.resource.ViabilityState.REVIEW;
import static org.junit.Assert.*;
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

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private FinanceCheckService financeCheckService;

    @Override
    protected OrganisationDetailsWithGrowthTableController supplyControllerUnderTest() {
        return new OrganisationDetailsWithGrowthTableController();
    }

    private long competitionId = 1L;
    private long projectId = 2L;
    private long organisationId = 3L;

    private CompetitionResource competition;
    private ProjectResource project;
    private OrganisationResource organisation;
    private YourOrganisationWithGrowthTableForm form;

    @Before
    public void setup() {
        project = getProject();
        organisation = getOrganisation();
        competition = new CompetitionResource();

        OrganisationFinancesWithGrowthTableResource finances = getFinances();
        form = getForm();

        FinanceCheckPartnerStatusResource partner = new FinanceCheckPartnerStatusResource();
        partner.setViability(REVIEW);
        partner.setEligibility(EligibilityState.REVIEW);
        List<FinanceCheckPartnerStatusResource> partnerStatusResources = Arrays.asList(partner);
        FinanceCheckSummaryResource financeCheckSummaryResource = newFinanceCheckSummaryResource()
                .build();
        financeCheckSummaryResource.setPartnerStatusResources(partnerStatusResources);

        when(projectRestService.getProjectById(projectId)).thenReturn(new RestResult(restSuccess(project)));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(new RestResult(restSuccess(organisation)));
        when(projectYourOrganisationRestService.getOrganisationFinancesWithGrowthTable(projectId, organisationId)).thenReturn(serviceSuccess(finances));
        when(withGrowthTableFormPopulator.populate(finances)).thenReturn(form);
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(new RestResult(restSuccess(Arrays.asList(new PartnerOrganisationResource()))));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(new RestResult(restSuccess(competition)));
        when(financeCheckService.getFinanceCheckSummary(projectId)).thenReturn(serviceSuccess(financeCheckSummaryResource));
    }

    private MvcResult callEndpoint() throws Exception {
        return mockMvc.perform(get("/competition/" + competitionId + "/project/" + projectId + "/organisation/" + organisationId + "/details/with-growth-table"))
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void viewOrganisationDetailsWithYourOrganisationSection() throws Exception {
        competition.setIncludeYourOrganisationSection(true);
        competition.setIncludeJesForm(false);
        organisation.setOrganisationType(1L);

        MvcResult result = callEndpoint();
        ProjectYourOrganisationViewModel yourOrganisation = (ProjectYourOrganisationViewModel) result.getModelAndView().getModel().get("yourOrganisation");
        YourOrganisationWithGrowthTableForm actualForm = (YourOrganisationWithGrowthTableForm) result.getModelAndView().getModel().get("form");

        sharedAssertions(result, organisation.getAddresses().get(0).getAddress());
        assertEquals(yourOrganisation, result.getModelAndView().getModel().get("yourOrganisation"));

        assertEquals(organisationId, yourOrganisation.getOrganisationId());
        assertEquals(projectId, yourOrganisation.getProjectId());
        assertEquals(project.getName(), yourOrganisation.getProjectName());

        assertEquals(form, actualForm);
    }

    private void sharedAssertions (MvcResult result, AddressResource expectedAddress){
        OrganisationDetailsViewModel organisationDetails = (OrganisationDetailsViewModel) result.getModelAndView().getModel().get("organisationDetails");
        assertEquals("project/organisationdetails/organisation-details-with-growth-table", result.getModelAndView().getViewName());
        assertEquals(expectedAddress.getAddressLine1(), organisationDetails.getAddressLine1());
        assertEquals(expectedAddress.getAddressLine2(), organisationDetails.getAddressLine2());
        assertEquals(expectedAddress.getAddressLine3(), organisationDetails.getAddressLine3());
        assertEquals(competitionId, organisationDetails.getCompetitionId());
        assertEquals(expectedAddress.getCounty(), organisationDetails.getCounty());
        assertEquals(organisation.getName(), organisationDetails.getOrganisationName());
        assertEquals(organisation.getOrganisationTypeName(), organisationDetails.getOrganisationType());
        assertEquals(expectedAddress.getPostcode(), organisationDetails.getPostcode());
        assertEquals(projectId, organisationDetails.getProjectId());
        assertEquals(project.getName(), organisationDetails.getProjectName());
        assertEquals(organisation.getCompaniesHouseNumber(), organisationDetails.getRegistrationNumber());
        assertEquals(expectedAddress.getTown(), organisationDetails.getTown());
    }

    @Test
    public void viewOrganisationDetailsWithoutYourOrganisationSectionAndAddressEmpty() throws Exception {
        competition.setIncludeYourOrganisationSection(false);
        competition.setIncludeJesForm(true);
        competition.setFundingType(GRANT);
        organisation.setOrganisationType(2L);

        organisation.setAddresses(new ArrayList());

        MvcResult result = callEndpoint();

        sharedAssertions(result, new AddressResource("", "", "", "", "", ""));

        assertNotEquals("yourOrganisation", result.getModelAndView().getModel());
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
