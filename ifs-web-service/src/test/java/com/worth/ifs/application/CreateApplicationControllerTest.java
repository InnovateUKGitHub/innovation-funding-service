package com.worth.ifs.application;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.exception.ErrorController;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations="classpath:application.properties")
public class CreateApplicationControllerTest  extends BaseUnitTest {
    @InjectMocks
    private CreateApplicationController createApplicationController;

    @Mock
    private Validator validator;

    @Mock
    private Model model;

    private String COMPANY_ID = "08241216";
    private String COMPANY_NAME = "NETWORTHNET LTD";
    private String POSTCODE_LOOKUP = "CH64 3RU";

    @Before
    public void setUp() {
        super.setup();
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(createApplicationController, new ErrorController())
                .setViewResolvers(viewResolver())
                .build();
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
    public void testCreateOrganisationBusinessPost() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                .param("organisationName", "BusinessName"))
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attributeErrorCount("companyHouseLookup", 0));
    }
    @Test
    public void testCreateOrganisationBusinessInvalidPost() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                    .param("organisationName", ""))
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attributeHasFieldErrors("companyHouseLookup", "organisationName"))
                .andExpect(model().attributeErrorCount("companyHouseLookup", 1))
                .andExpect(model().attributeHasFieldErrorCode("companyHouseLookup", "organisationName", "NotEmpty"));
    }

    @Test
    public void testCreateOrganisationBusinessInvalidCharacters() throws Exception {
        mockMvc.perform(post("/application/create/find-business")
                .param("organisationName", "a{}a"))
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attributeHasFieldErrors("companyHouseLookup", "organisationName"))
                .andExpect(model().attributeErrorCount("companyHouseLookup", 1))
                .andExpect(model().attributeHasFieldErrorCode("companyHouseLookup", "organisationName", "Pattern"));
    }

    @Test
    public void testCreateOrganisationBusiness() throws Exception {
        mockMvc.perform(get("/application/create/find-business"))
                .andExpect(view().name("create-application/find-business"))
                .andExpect(model().attributeExists("companyHouseLookup"));
    }

    @Test
    public void testConfirmBusiness() throws Exception {
        mockMvc.perform(get("/application/create/selected-business/"+COMPANY_ID))
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeExists("confirmCompanyDetailsForm"));
    }

    @Test
    public void testConfirmBusinessSubmit() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", POSTCODE_LOOKUP)
        )
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeExists("confirmCompanyDetailsForm"))
                .andExpect(model().attributeErrorCount("confirmCompanyDetailsForm", 0));
    }

    @Test
    public void testConfirmBusinessSubmitSelectAddress() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", POSTCODE_LOOKUP)
                        .param("select-address", "true")
                        .param("selectedPostcodeIndex", "0")
        )
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeExists("confirmCompanyDetailsForm"))
                .andExpect(model().attributeErrorCount("confirmCompanyDetailsForm", 0));
        //TODO: check if the Address object is also added to the model
    }

    @Test
    public void testConfirmBusinessInvalidSubmit() throws Exception {
        mockMvc.perform(post("/application/create/selected-business/" + COMPANY_ID)
                        .param("postcodeInput", "")
                        .param("search-address", "")
        )
                .andExpect(view().name("create-application/confirm-selected-organisation"))
                .andExpect(model().attributeExists("confirmCompanyDetailsForm"))
                .andExpect(model().attributeErrorCount("confirmCompanyDetailsForm", 1))
                .andExpect(model().attributeHasFieldErrorCode("confirmCompanyDetailsForm", "postcodeInput", "NotEmpty"));
    }
}