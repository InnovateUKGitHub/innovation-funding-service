package com.worth.ifs.application;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.exception.ErrorControllerAdvice;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.user.resource.OrganisationResource;
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

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
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


        mockMvc = MockMvcBuilders.standaloneSetup(applicationCreationController, new ErrorControllerAdvice())
                .setViewResolvers(viewResolver())
                .build();

        super.setup();

        applicationResource = newApplicationResource().withId(6L).withName("some application").build();
        OrganisationSearchResult organisationSearchResult = new OrganisationSearchResult(COMPANY_ID, COMPANY_NAME);
        organisationResource = newOrganisationResource().withId(5L).withName(COMPANY_NAME).build();
        when(organisationService.getCompanyHouseOrganisation(COMPANY_ID)).thenReturn(organisationSearchResult);
        when(organisationService.save(any(OrganisationResource.class))).thenReturn(organisationResource);
        when(applicationService.createApplication(anyLong(), anyLong(), anyString())).thenReturn(applicationResource);
    }

    @Test
    public void testCheckEligibility() throws Exception {
        mockMvc.perform(get("/application/create/check-eligibility/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/check-eligibility"))
                .andExpect(cookie().value("competitionId", "1"))
                .andExpect(cookie().value("invite_hash", ""));
    }


    @Test
    public void testInitializeApplication() throws Exception {
        mockMvc.perform(get("/application/create/initialize-application")
                        .cookie(new Cookie(ApplicationCreationController.COMPETITION_ID, "1"))
                        .cookie(new Cookie(ApplicationCreationController.USER_ID, "1"))
        )
                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/application/" + applicationResource.getId()+"/contributors/invite?newApplication"));
                // TODO INFUND-936 temporary measure to redirect to login screen until email verification is in place
                .andExpect(view().name("redirect:/"));
    }
}
