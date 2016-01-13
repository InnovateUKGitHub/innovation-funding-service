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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationCreationControllerTest extends BaseUnitTest {
    @InjectMocks
    private ApplicationCreationController applicationCreationController;

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

        mockMvc = MockMvcBuilders.standaloneSetup(applicationCreationController, new ErrorController())
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
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/check-eligibility"))
                .andExpect(cookie().value("competitionId", "1"));
    }

    @Test
    public void testCreateAccountOrganisationType() throws Exception {
        mockMvc.perform(get("/application/create/create-organisation-type"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/create-organisation-type"));
    }

    @Test
    public void testFindBusiness() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("companyHouseName", "BusinessName")
                        .param("not-in-company-house", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/application/create/find-business/not-in-company-house"));

        MvcResult result = mockMvc.perform(get("/application/create/find-business/not-in-company-house"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attribute("companyHouseForm", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(false))))
                .andReturn();
    }

    @Test
    public void testFindBusinessManualAddress() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("companyHouseName", "BusinessName")
                        .param("manual-address", "")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attribute("companyHouseForm", Matchers.isA(CompanyHouseForm.class)))
                .andExpect(model().attribute("companyHouseForm", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(false))))
                .andExpect(model().attribute("companyHouseForm", Matchers.hasProperty("manualAddress", Matchers.equalTo(true))));
    }

    @Test
    public void testFindBusinessSearchAddress() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("postcodeInput", POSTCODE_LOOKUP)
                        .param("search-address", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/application/create/find-business/postcode/%s", POSTCODE_LOOKUP)));

        MvcResult result = mockMvc.perform(get(String.format("/application/create/find-business/postcode/%s", POSTCODE_LOOKUP)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attribute("companyHouseForm", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(false))))
                .andReturn();
    }

    @Test
    public void testFindBusinessSearchAddressInvalid() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("postcodeInput", "")
                        .param("search-address", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/application/create/find-business/postcode/")));

        MvcResult result = mockMvc.perform(get(String.format("/application/create/find-business/postcode/")))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attribute("companyHouseForm", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(false))))
                .andReturn();
    }


    @Test
    public void testFindBusinessSelectAddress() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("postcodeInput", POSTCODE_LOOKUP)
                        .param("selectedPostcodeIndex", String.valueOf(0))
                        .param("select-address", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/application/create/find-business/postcode/%s/use-address/0", POSTCODE_LOOKUP)));

        MvcResult result = mockMvc.perform(get(String.format("/application/create/find-business/postcode/%s/use-address/0", POSTCODE_LOOKUP)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attribute("companyHouseForm", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(false))))
                .andReturn();
    }

    @Test
    public void testFindBusinessSearchCompanyHouse() throws Exception {
        String companyName = "BusinessName";
        mockMvc.perform(post("/application/create/find-business")
                        .param("companyHouseName", companyName)
                        .param("search-company-house", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/application/create/find-business/search/" + companyName));

        MvcResult result = mockMvc.perform(get("/application/create/find-business/search/" + companyName))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attribute("companyHouseForm", Matchers.hasProperty("companyHouseSearching", Matchers.equalTo(true))))
                .andReturn();
    }

    @Test
    public void testFindBusinessSearchCompanyHouseInvalid() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("companyHouseName", "")
                        .param("search-company-house", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/application/create/find-business/search/"));
    }

    @Test
    public void testFindBusinessConfirmCompanyDetailsInvalid() throws Exception {
        MvcResult result = mockMvc.perform(post("/application/create/find-business")
                        .param("organisationName", "")
                        .param("organisationSize", OrganisationSize.LARGE.name())
                        .param("confirm-company-details", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/application/create/find-business/invalid-entry"))
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("companyHouseForm");

        result = mockMvc.perform(get("/application/create/find-business/invalid-entry")
                        .cookie(cookie)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/find-business"))
//                .andExpect(model().attributeHasFieldErrorCode("companyHouseForm", "organisationName", "NotEmpty"))
                .andReturn();

        ModelAndView mav = result.getModelAndView();
        log.warn("mav");
    }

    @Test
    public void testFindBusinessConfirmCompanyDetails() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                        .param("organisationName", "BusinessName")
                        .param("organisationSize", OrganisationSize.LARGE.name())
                        .param("confirm-company-details", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists(ApplicationCreationController.COMPANY_NAME))
                .andExpect(cookie().exists(ApplicationCreationController.COMPANY_ADDRESS))
                .andExpect(cookie().exists(ApplicationCreationController.ORGANISATION_SIZE))
                .andExpect(view().name("redirect:/application/create/confirm-company"));
    }

    @Test
    public void testCreateOrganisationBusiness() throws Exception {
        mockMvc.perform(get("/application/create/find-business"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attributeExists("companyHouseForm"));
    }

    @Test
    public void testConfirmBusiness() throws Exception {
        mockMvc.perform(get("/application/create/selected-business/" + COMPANY_ID))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeExists("confirmCompanyDetailsForm"));
    }

    @Test
    public void testSelectedBusinessSubmit() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", POSTCODE_LOOKUP)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeExists("confirmCompanyDetailsForm"));
    }

    @Test
    public void testSelectedBusinessSubmitSearchAddress() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", POSTCODE_LOOKUP)
                        .param("search-address", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/application/create/selected-business/%s/postcode/%s", COMPANY_ID, POSTCODE_LOOKUP)));

        MvcResult result = mockMvc.perform(get(String.format("/application/create/selected-business/%s/postcode/%s", COMPANY_ID, POSTCODE_LOOKUP))

        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeHasNoErrors("confirmCompanyDetailsForm"))
                .andReturn();
    }

    @Test
    public void testSelectedBusinessSubmitSelectAddress() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", POSTCODE_LOOKUP)
                        .param("selectedPostcodeIndex", "0")
                        .param("select-address", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/application/create/selected-business/%s/postcode/%s/use-address/%s", COMPANY_ID, POSTCODE_LOOKUP, "0")));

        MvcResult result = mockMvc.perform(get(String.format("/application/create/selected-business/%s/postcode/%s/use-address/%s", COMPANY_ID, POSTCODE_LOOKUP, "0"))

        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeHasNoErrors("confirmCompanyDetailsForm"))
                .andReturn();
    }

    @Test
    public void testSelectedBusinessManualAddress() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("manual-address", "true")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeExists("confirmCompanyDetailsForm"))
                .andExpect(model().attribute("confirmCompanyDetailsForm", Matchers.hasProperty("manualAddress", Matchers.equalTo(true))));
    }

    @Test
    public void testSelectedBusinessSaveBusiness() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("organisationSize", OrganisationSize.LARGE.name())
                        .param("save-company-details", "true")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/register?organisationId=5"));
    }

    @Test
    public void testSelectedBusinessInvalidSubmit() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", "")
                        .param("search-address", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/application/create/selected-business/%s/postcode/", COMPANY_ID)));

        MvcResult result = mockMvc.perform(get(String.format("/application/create/selected-business/%s/postcode/", COMPANY_ID))

        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeHasFieldErrorCode("confirmCompanyDetailsForm", "postcodeInput", "NotEmpty"))
                .andReturn();
    }

    @Test
    public void testInitializeApplication() throws Exception {
        mockMvc.perform(get("/application/create/initialize-application")
                        .cookie(new Cookie(ApplicationCreationController.COMPETITION_ID, "1"))
                        .cookie(new Cookie(ApplicationCreationController.USER_ID, "1"))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/application/" + applicationResource.getId()+"/contributors/invite"));
    }

    @Test
    public void testConfirmCompany() throws Exception {
        mockMvc.perform(get("/application/create/confirm-company")
                        .cookie(new Cookie(ApplicationCreationController.COMPANY_ADDRESS, "{}"))
                        .cookie(new Cookie(ApplicationCreationController.COMPANY_NAME, "SOME NAME"))
                        .cookie(new Cookie(ApplicationCreationController.ORGANISATION_SIZE, OrganisationSize.LARGE.name()))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("business"))
                .andExpect(model().attribute("business", Matchers.hasProperty("name", Matchers.equalTo("SOME NAME"))))
                .andExpect(view().name("create-application/confirm-company"));
    }

    @Test
    public void testSaveCompany() throws Exception {
        mockMvc.perform(get("/application/create/save-company/")
                        .cookie(new Cookie(ApplicationCreationController.COMPANY_ADDRESS, "{}"))
                        .cookie(new Cookie(ApplicationCreationController.COMPANY_NAME, "SOME NAME"))
                        .cookie(new Cookie(ApplicationCreationController.ORGANISATION_SIZE, OrganisationSize.LARGE.name()))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/register?organisationId=" + organisationResource.getId()));

    }
}