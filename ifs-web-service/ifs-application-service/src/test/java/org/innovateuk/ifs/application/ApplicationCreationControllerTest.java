package org.innovateuk.ifs.application;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;
import org.springframework.validation.Validator;

import javax.servlet.http.Cookie;

import static org.innovateuk.ifs.BaseControllerMockMVCTest.setupMockMvc;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private OrganisationResource organisationResource;
    private ApplicationResource applicationResource;

    @Before
    public void setUp() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = setupMockMvc(applicationCreationController, () -> loggedInUser, env, messageSource);

        super.setup();
        setupCookieUtil();

        applicationResource = newApplicationResource().withId(6L).withName("some application").build();
        OrganisationSearchResult organisationSearchResult = new OrganisationSearchResult(COMPANY_ID, COMPANY_NAME);
        organisationResource = newOrganisationResource().withId(5L).withName(COMPANY_NAME).build();
        when(organisationService.getCompanyHouseOrganisation(COMPANY_ID)).thenReturn(organisationSearchResult);
        when(organisationService.save(any(OrganisationResource.class))).thenReturn(organisationResource);
        when(applicationService.createApplication(anyLong(), anyLong(), anyString())).thenReturn(applicationResource);
    }

    @Test
    public void testCheckEligibility() throws Exception {
        MvcResult result = mockMvc.perform(get("/application/create/check-eligibility/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/check-eligibility"))
                .andExpect(cookie().exists("competitionId"))
                .andExpect(cookie().value("invite_hash", ""))
                .andReturn();

        assertEquals("1", getDecryptedCookieValue(result.getResponse().getCookies(), "competitionId"));
    }


    @Test
    public void testInitializeApplication() throws Exception {
        mockMvc.perform(get("/application/create/initialize-application")
                .cookie(new Cookie(ApplicationCreationController.COMPETITION_ID, encryptor.encrypt("1")))
                .cookie(new Cookie(ApplicationCreationController.USER_ID, encryptor.encrypt("1")))
        )
                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/application/" + applicationResource.getId()+"/contributors/invite?newApplication"));
                // TODO INFUND-936 temporary measure to redirect to login screen until email verification is in place
                .andExpect(view().name("redirect:/"));
    }
}
