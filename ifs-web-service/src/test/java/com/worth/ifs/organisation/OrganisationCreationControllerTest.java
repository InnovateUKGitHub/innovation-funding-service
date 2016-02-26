package com.worth.ifs.organisation;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.application.form.CompanyHouseForm;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.exception.ErrorController;
import com.worth.ifs.organisation.resource.CompanyHouseBusiness;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.organisation.OrganisationCreationController;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class OrganisationCreationControllerTest  extends BaseUnitTest {
    @InjectMocks
    private OrganisationCreationController organisationCreationController;
    private ApplicationResource applicationResource;

    @Mock
    private Validator validator;

    @Mock
    private AddressRestService addressRestService;

    private String COMPANY_ID = "08241216";
    private String COMPANY_NAME = "NETWORTHNET LTD";
    private String POSTCODE_LOOKUP = "CH64 3RU";
    private AddressResource address;
    private CompanyHouseBusiness companyHouseBusiness;
    private OrganisationResource organisationResource;

    @Before
    public void setUp() {
        super.setup();
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(organisationCreationController, new ErrorController())
                .setViewResolvers(viewResolver())
                .build();

        applicationResource = newApplicationResource().withId(6L).withName("some application").build();
        address = new AddressResource("line1", "line2", "line3", "locality", "region", "postcode");
        companyHouseBusiness = new CompanyHouseBusiness(COMPANY_ID, COMPANY_NAME, null, null, null, address);
        organisationResource = newOrganisationResource().withId(5L).withName(COMPANY_NAME).build();
        List<AddressResource> addressResourceList = new ArrayList<>();
        addressResourceList.add(address);
        when(organisationService.getCompanyHouseOrganisation(COMPANY_ID)).thenReturn(companyHouseBusiness);
        when(organisationService.save(any(Organisation.class))).thenReturn(organisationResource);
        when(organisationService.save(any(OrganisationResource.class))).thenReturn(organisationResource);
        when(applicationService.createApplication(anyLong(), anyLong(), anyString())).thenReturn(applicationResource);
        when(addressRestService.doLookup(POSTCODE_LOOKUP)).thenReturn(restSuccess(addressResourceList));
    }

    @Test
    public void testCreateAccountOrganisationType() throws Exception {
        mockMvc.perform(get("/organisation/create/create-organisation-type"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/create-organisation-type"));
    }


    @Test
    public void testFindBusiness() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-business")
                        .param("companyHouseName", "BusinessName")
                        .param("not-in-company-house", "")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/find-business/not-in-company-house"));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/find-business/not-in-company-house"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/find-business"))
                .andExpect(MockMvcResultMatchers.model().attribute("companyHouseForm", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(false))))
                .andReturn();
    }

    @Test
    public void testFindBusinessManualAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-business")
                        .param("companyHouseName", "BusinessName")
                        .param("manual-address", "")
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/find-business"))
                .andExpect(MockMvcResultMatchers.model().attribute("companyHouseForm", Matchers.isA(CompanyHouseForm.class)))
                .andExpect(MockMvcResultMatchers.model().attribute("companyHouseForm", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(false))))
                .andExpect(MockMvcResultMatchers.model().attribute("companyHouseForm", Matchers.hasProperty("manualAddress", Matchers.equalTo(true))));
    }

    @Test
    public void testFindBusinessSearchAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-business")
                        .param("postcodeInput", POSTCODE_LOOKUP)
                        .param("search-address", "")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(String.format("redirect:/organisation/create/find-business/postcode/%s", POSTCODE_LOOKUP)));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/find-business/postcode/%s", POSTCODE_LOOKUP)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/find-business"))
                .andExpect(MockMvcResultMatchers.model().attribute("companyHouseForm", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(false))))
                .andReturn();
    }

    @Test
    public void testFindBusinessSearchAddressInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-business")
                        .param("postcodeInput", "")
                        .param("search-address", "")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(String.format("redirect:/organisation/create/find-business/postcode/")));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/find-business/postcode/")))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/find-business"))
                .andExpect(MockMvcResultMatchers.model().attribute("companyHouseForm", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(false))))
                .andReturn();
    }

    @Test
    public void testFindBusinessSelectAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-business")
                        .param("postcodeInput", POSTCODE_LOOKUP)
                        .param("selectedPostcodeIndex", String.valueOf(0))
                        .param("select-address", "")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(String.format("redirect:/organisation/create/find-business/postcode/%s/use-address/0", POSTCODE_LOOKUP)));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/find-business/postcode/%s/use-address/0", POSTCODE_LOOKUP)))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/find-business"))
                .andExpect(MockMvcResultMatchers.model().attribute("companyHouseForm", Matchers.hasProperty("inCompanyHouse", Matchers.equalTo(false))))
                .andReturn();
    }

    @Test
    public void testFindBusinessSearchCompanyHouse() throws Exception {
        String companyName = "BusinessName";
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-business")
                        .param("companyHouseName", companyName)
                        .param("search-company-house", "")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/find-business/search/" + companyName));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/find-business/search/" + companyName))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/find-business"))
                .andExpect(MockMvcResultMatchers.model().attribute("companyHouseForm", Matchers.hasProperty("companyHouseSearching", Matchers.equalTo(true))))
                .andReturn();
    }

    @Test
    public void testFindBusinessSearchCompanyHouseInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-business")
                        .param("companyHouseName", "")
                        .param("search-company-house", "")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/find-business/search/"));
    }

    @Test
    public void testFindBusinessConfirmCompanyDetailsInvalid() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-business")
                        .param("organisationName", "")
                        .param("confirm-company-details", "")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/find-business/invalid-entry"))
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("companyHouseForm");

        result = mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/find-business/invalid-entry")
                        .cookie(cookie)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/find-business"))
//                .andExpect(model().attributeHasFieldErrorCode("companyHouseForm", "organisationName", "NotEmpty"))
                .andReturn();

        ModelAndView mav = result.getModelAndView();
        log.warn("mav");
    }

    @Test
    public void testFindBusinessConfirmCompanyDetails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/find-business")
                        .param("organisationName", "BusinessName")
                        .param("confirm-company-details", "")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.cookie().exists(OrganisationCreationController.COMPANY_NAME))
                .andExpect(MockMvcResultMatchers.cookie().exists(OrganisationCreationController.COMPANY_ADDRESS))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/organisation/create/confirm-company"));
    }

    @Test
    public void testCreateOrganisationBusiness() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/find-business"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/find-business"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("companyHouseForm"));
    }

    @Test
    public void testConfirmBusiness() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/selected-business/" + COMPANY_ID))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/confirm-selected-organisation"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("confirmCompanyDetailsForm"));
    }

    @Test
    public void testSelectedBusinessSubmit() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", POSTCODE_LOOKUP)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/confirm-selected-organisation"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("confirmCompanyDetailsForm"));
    }

    @Test
    public void testSelectedBusinessSubmitSearchAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", POSTCODE_LOOKUP)
                        .param("search-address", "")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(String.format("redirect:/organisation/create/selected-business/%s/postcode/%s", COMPANY_ID, POSTCODE_LOOKUP)));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/selected-business/%s/postcode/%s", COMPANY_ID, POSTCODE_LOOKUP))

        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/confirm-selected-organisation"))
                .andExpect(MockMvcResultMatchers.model().attributeHasNoErrors("confirmCompanyDetailsForm"))
                .andReturn();
    }

    @Test
    public void testSelectedBusinessSubmitSelectAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", POSTCODE_LOOKUP)
                        .param("selectedPostcodeIndex", "0")
                        .param("select-address", "")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(String.format("redirect:/organisation/create/selected-business/%s/postcode/%s/use-address/%s", COMPANY_ID, POSTCODE_LOOKUP, "0")));

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/selected-business/%s/postcode/%s/use-address/%s", COMPANY_ID, POSTCODE_LOOKUP, "0"))

        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/confirm-selected-organisation"))
                .andExpect(MockMvcResultMatchers.model().attributeHasNoErrors("confirmCompanyDetailsForm"))
                .andReturn();
    }

    @Test
    public void testSelectedBusinessManualAddress() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/selected-business/" + COMPANY_ID)
                        .param("manual-address", "true")
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/confirm-selected-organisation"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("confirmCompanyDetailsForm"))
                .andExpect(MockMvcResultMatchers.model().attribute("confirmCompanyDetailsForm", Matchers.hasProperty("manualAddress", Matchers.equalTo(true))));
    }

    @Test
    public void testSelectedBusinessSaveBusiness() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/selected-business/" + COMPANY_ID)
                        .param("save-company-details", "true")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/registration/register?organisationId=5"));
    }

    @Test
    public void testSelectedBusinessInvalidSubmit() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/organisation/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", "")
                        .param("search-address", "")
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(String.format("redirect:/organisation/create/selected-business/%s/postcode/", COMPANY_ID)));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(String.format("/organisation/create/selected-business/%s/postcode/", COMPANY_ID))

        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.view().name("create-application/confirm-selected-organisation"))
                .andExpect(MockMvcResultMatchers.model().attributeHasFieldErrorCode("confirmCompanyDetailsForm", "postcodeInput", "NotEmpty"))
                .andReturn();
    }

    @Test
    public void testConfirmCompany() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/confirm-company")
                        .cookie(new Cookie(OrganisationCreationController.COMPANY_ADDRESS, "{}"))
                        .cookie(new Cookie(OrganisationCreationController.COMPANY_NAME, "SOME NAME"))
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.model().attributeExists("business"))
                .andExpect(MockMvcResultMatchers.model().attribute("business", Matchers.hasProperty("name", Matchers.equalTo("SOME NAME"))))
                .andExpect(MockMvcResultMatchers.view().name("create-application/confirm-company"));
    }

    @Test
    public void testSaveCompany() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/organisation/create/save-company/")
                        .cookie(new Cookie(OrganisationCreationController.COMPANY_ADDRESS, "{}"))
                        .cookie(new Cookie(OrganisationCreationController.COMPANY_NAME, "SOME NAME"))
        )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/registration/register?organisationId=" + organisationResource.getId()));

    }
}