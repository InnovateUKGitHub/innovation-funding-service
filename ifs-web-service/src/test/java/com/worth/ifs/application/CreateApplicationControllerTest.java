package com.worth.ifs.application;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.form.CompanyHouseForm;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.exception.ErrorController;
import com.worth.ifs.organisation.domain.Address;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationSize;
import com.worth.ifs.user.resource.OrganisationResource;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.Validator;

import javax.servlet.http.Cookie;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CreateApplicationControllerTest extends BaseUnitTest {
    @InjectMocks
    private CreateApplicationController createApplicationController;

    @Mock
    private Validator validator;

    @Mock
    private Model model;

    private String COMPANY_ID = "08241216";
    private String COMPANY_NAME = "NETWORTHNET LTD";
    private String POSTCODE_LOOKUP = "CH64 3RU";
    private Address address;
    private CompanyHouseBusiness companyHouseBusiness;
    private OrganisationResource organisationResource;
    private ApplicationResource applicationResource;

    @Before
    public void setUp() {
        super.setup();
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(createApplicationController, new ErrorController())
                .setViewResolvers(viewResolver())
                .build();

        applicationResource = new ApplicationResource(new Application(6L, "some application", null));
        address = new Address("line1", "line2", "line3", "careof", "country", "locality", "pobox", "postcode", "region");
        companyHouseBusiness = new CompanyHouseBusiness(COMPANY_ID, COMPANY_NAME, null, null, null, address);
        organisationResource = new OrganisationResource(new Organisation(5L, COMPANY_NAME));
        when(organisationService.getCompanyHouseOrganisation(COMPANY_ID)).thenReturn(companyHouseBusiness);
        when(organisationService.save(any(Organisation.class))).thenReturn(organisationResource);
        when(applicationService.createApplication(anyLong(), anyLong(), anyString())).thenReturn(applicationResource);
    }

    @Test
    public void testCheckEligibility() throws Exception {
        mockMvc.perform(get("/application/create/check-eligibility/1"))
                .andExpect(view().name("create-application/check-eligibility"))
                .andExpect(cookie().value("competitionId", "1"));
    }

    @Test
    public void testCreateAccountOrganisationType() throws Exception {
        mockMvc.perform(get("/application/create/create-organisation-type"))
                .andExpect(view().name("create-application/create-organisation-type"));
    }

    @Test
    public void testFindBusiness() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("companyHouseName", "BusinessName")
                        .param("not-in-company-house", "")
        )
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attribute("companyHouseLookup", Matchers.isA(CompanyHouseForm.class)))
                .andExpect(model().attribute("companyHouseLookup", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(false))));
    }

    @Test
    public void testFindBusinessManualAddress() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("companyHouseName", "BusinessName")
                        .param("manual-address", "")
        )
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attribute("companyHouseLookup", Matchers.isA(CompanyHouseForm.class)))
                .andExpect(model().attribute("companyHouseLookup", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(false))))
                .andExpect(model().attribute("companyHouseLookup", Matchers.hasProperty("manualAddress", Matchers.equalTo(true))));
    }

    @Test
    public void testFindBusinessSearcAddress() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("companyHouseName", "BusinessName")
                        .param("search-address", "")
        )
                .andExpect(view().name("create-application/find-business"));
    }

    @Test
    public void testFindBusinessSelectAddress() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("companyHouseName", "BusinessName")
                        .param("select-address", "")
        )
                .andExpect(view().name("create-application/find-business"));

    }

    @Test
    public void testFindBusinessSearchCompanyHouse() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("companyHouseName", "BusinessName")
                        .param("search-company-house", "")
        )
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attributeExists("companyHouseLookup"))
                .andExpect(model().attribute("companyHouseLookup", Matchers.isA(CompanyHouseForm.class)))
                .andExpect(model().attribute("companyHouseLookup", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(true))));
    }

    @Test
    public void testFindBusinessSearchCompanyHouseInvalid() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("companyHouseName", "")
                        .param("search-company-house", "")
        )
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attributeExists("companyHouseLookup"))
                .andExpect(model().attribute("companyHouseLookup", Matchers.isA(CompanyHouseForm.class)))
                .andExpect(model().attribute("companyHouseLookup", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(true))));

    }

    @Test
    public void testFindBusinessConfirmCompanyDetails() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("organisationName", "BusinessName")
                        .param("organisationSize", OrganisationSize.LARGE.name())
                        .param("confirm-company-details", "")
        )

                .andExpect(model().attribute("companyHouseLookup", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(false))))
                .andExpect(cookie().exists(CreateApplicationController.COMPANY_NAME))
                .andExpect(cookie().exists(CreateApplicationController.COMPANY_ADDRESS))
                .andExpect(cookie().exists(CreateApplicationController.ORGANISATION_SIZE))
                .andExpect(view().name("redirect:/application/create/confirm-company"));
    }

    @Test
    public void testCreateOrganisationBusiness() throws Exception {
        mockMvc.perform(get("/application/create/find-business"))
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attributeExists("companyHouseLookup"));
    }

    @Test
    public void testConfirmBusiness() throws Exception {
        mockMvc.perform(get("/application/create/selected-business/" + COMPANY_ID))
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeExists("confirmCompanyDetailsForm"));
    }

    @Test
    public void testSelectedBusinessSubmit() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", POSTCODE_LOOKUP)
        )
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeExists("confirmCompanyDetailsForm"));
    }

    @Test
    public void testSelectedBusinessSubmitSelectAddress() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", POSTCODE_LOOKUP)
                        .param("select-address", "true")
                        .param("selectedPostcodeIndex", "0")
        )
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeExists("confirmCompanyDetailsForm"));
    }

    @Test
    public void testSelectedBusinessManualAddress() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("manual-address", "true")
        )
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeExists("confirmCompanyDetailsForm"))
                .andExpect(model().attribute("confirmCompanyDetailsForm", Matchers.hasProperty("manualAddress", Matchers.equalTo(true))));
    }

    @Test
    public void testSelectedBusinessSaveBusiness() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("save-company-details", "true")
        )
                .andExpect(view().name("redirect:/registration/register?organisationId=5"));
    }

    @Test
    public void testSelectedBusinessInvalidSubmit() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", "")
                        .param("search-address", "")
        )
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeExists("confirmCompanyDetailsForm"))
                .andExpect(model().attributeHasFieldErrorCode("confirmCompanyDetailsForm", "postcodeInput", "NotEmpty"));
    }

    @Test
    public void testInitializeApplication() throws Exception {
        mockMvc.perform(get("/application/create/initialize-application")
                        .cookie(new Cookie(CreateApplicationController.COMPETITION_ID, "1"))
                        .cookie(new Cookie(CreateApplicationController.USER_ID, "1"))
        )
                .andExpect(view().name("redirect:/application/" + applicationResource.getId()));
    }

    @Test
    public void testConfirmCompany() throws Exception {
        mockMvc.perform(get("/application/create/confirm-company")
                        .cookie(new Cookie(CreateApplicationController.COMPANY_ADDRESS, "{}"))
                        .cookie(new Cookie(CreateApplicationController.COMPANY_NAME, "SOME NAME"))
                        .cookie(new Cookie(CreateApplicationController.ORGANISATION_SIZE, OrganisationSize.LARGE.name()))
        )
                .andExpect(model().attributeExists("business"))
                .andExpect(model().attribute("business", Matchers.hasProperty("name", Matchers.equalTo("SOME NAME"))))
                .andExpect(view().name("create-application/confirm-company"));
    }

    @Test
    public void testSaveCompany() throws Exception {
        mockMvc.perform(get("/application/create/save-company/")
                        .cookie(new Cookie(CreateApplicationController.COMPANY_ADDRESS, "{}"))
                        .cookie(new Cookie(CreateApplicationController.COMPANY_NAME, "SOME NAME"))
                        .cookie(new Cookie(CreateApplicationController.ORGANISATION_SIZE, OrganisationSize.LARGE.name()))
        )
                .andExpect(view().name("redirect:/registration/register?organisationId=" + organisationResource.getId()));

    }
}