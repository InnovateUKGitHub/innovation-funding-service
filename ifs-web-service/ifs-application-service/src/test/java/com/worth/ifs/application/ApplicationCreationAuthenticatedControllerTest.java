package com.worth.ifs.application;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.exception.ErrorControllerAdvice;
import com.worth.ifs.filter.ErrorHandlerFilter;
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

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationCreationAuthenticatedControllerTest extends BaseUnitTest {
    @InjectMocks
    private ApplicationCreationAuthenticatedController applicationCreationController;

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
                .addFilter(new ErrorHandlerFilter())
                .build();

        super.setup();

        applicationResource = newApplicationResource().withId(6L).withName("some application").build();
        OrganisationSearchResult organisationSearchResult = new OrganisationSearchResult(COMPANY_ID, COMPANY_NAME);
        organisationResource = newOrganisationResource().withId(5L).withName(COMPANY_NAME).build();
        when(organisationService.getCompanyHouseOrganisation(COMPANY_ID)).thenReturn(organisationSearchResult);
        when(organisationService.save(any(OrganisationResource.class))).thenReturn(organisationResource);
        when(applicationService.createApplication(anyLong(), anyLong(), anyString())).thenReturn(applicationResource);
        loginDefaultUser();
    }

    @Test
    public void testGetRequest() throws Exception {
        mockMvc.perform(get("/application/create-authenticated/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/confirm-new-application"));
    }


    @Test
    public void testPostEmptyForm() throws Exception {
        mockMvc.perform(post("/application/create-authenticated/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/create-authenticated/1"));
    }

    @Test
    public void testPostCreateNewApplication() throws Exception {
        ApplicationResource application = new ApplicationResource();
        application.setId(99L);
        when(applicationService.createApplication(anyLong(), anyLong(), eq(""))).thenReturn(application);

        mockMvc.perform(post("/application/create-authenticated/1").param(ApplicationCreationAuthenticatedController.FORM_RADIO_NAME, ApplicationCreationAuthenticatedController.RADIO_TRUE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/99/contributors/invite?newApplication"));

        // application needs to be linked to current user
        verify(userAuthenticationService, atLeastOnce()).getAuthenticatedUser(any());
        // application needs to be created.
        verify(applicationService, atLeastOnce()).createApplication(anyLong(), anyLong(), eq(""));
    }

    @Test
    public void testPostNoNewApplication() throws Exception {
        // This should just redirect to the dashboard.
        mockMvc.perform(post("/application/create-authenticated/1").param(ApplicationCreationAuthenticatedController.FORM_RADIO_NAME, ApplicationCreationAuthenticatedController.RADIO_FALSE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
