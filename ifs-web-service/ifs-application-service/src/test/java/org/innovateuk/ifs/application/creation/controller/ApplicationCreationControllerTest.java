package org.innovateuk.ifs.application.creation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationCreationControllerTest extends BaseControllerMockMVCTest<ApplicationCreationController> {

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Override
    protected ApplicationCreationController supplyControllerUnderTest() {
        return new ApplicationCreationController();
    }

    private String COMPANY_ID = "08241216";
    private String COMPANY_NAME = "NETWORTHNET LTD";
    private OrganisationResource organisationResource;
    private ApplicationResource applicationResource;

    @Before
    public void setUp() {
        super.setUp();
        setupCookieUtil();

        applicationResource = newApplicationResource().withId(6L).withName("some application").build();
        OrganisationSearchResult organisationSearchResult = new OrganisationSearchResult(COMPANY_ID, COMPANY_NAME);
        organisationResource = newOrganisationResource().withId(5L).withName(COMPANY_NAME).build();
        when(organisationService.getCompanyHouseOrganisation(COMPANY_ID)).thenReturn(organisationSearchResult);
        when(organisationService.save(any(OrganisationResource.class))).thenReturn(organisationResource);
        when(applicationService.createApplication(anyLong(), anyLong(), anyString())).thenReturn(applicationResource);
    }

    @Test
    public void checkEligibility() throws Exception {
        long competitionId = 1L;
        PublicContentItemResource publicContentItem = newPublicContentItemResource()
                .withCompetitionOpenDate(ZonedDateTime.now().minusDays(1))
                .withCompetitionCloseDate(ZonedDateTime.now().plusDays(1))
                .withNonIfs(false)
                .build();
        when(competitionService.getPublicContentOfCompetition(competitionId)).thenReturn(publicContentItem);

        MvcResult result = mockMvc.perform(get("/application/create/check-eligibility/{competitionId}", competitionId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/check-eligibility"))
                .andReturn();

        verify(registrationCookieService, times(1)).saveToCompetitionIdCookie(eq(competitionId), any());
    }


    @Test
    public void checkEligibility_nonIfs() throws Exception {
        long competitionId = 1L;
        PublicContentItemResource publicContentItem = newPublicContentItemResource()
                .withCompetitionOpenDate(ZonedDateTime.now().minusDays(1))
                .withCompetitionCloseDate(ZonedDateTime.now().plusDays(1))
                .withNonIfs(true)
                .build();
        when(competitionService.getPublicContentOfCompetition(competitionId)).thenReturn(publicContentItem);

        mockMvc.perform(get("/application/create/check-eligibility/{competitionId}", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/search"));
    }


    @Test
    public void checkEligibility_early() throws Exception {
        long competitionId = 1L;
        PublicContentItemResource publicContentItem = newPublicContentItemResource()
                .withCompetitionOpenDate(ZonedDateTime.now().plusDays(1))
                .withCompetitionCloseDate(ZonedDateTime.now().plusDays(2))
                .withNonIfs(false)
                .build();
        when(competitionService.getPublicContentOfCompetition(competitionId)).thenReturn(publicContentItem);

        mockMvc.perform(get("/application/create/check-eligibility/{competitionId}", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/search"));
    }

 @Test
    public void checkEligibility_late() throws Exception {
        long competitionId = 1L;
        PublicContentItemResource publicContentItem = newPublicContentItemResource()
                .withCompetitionOpenDate(ZonedDateTime.now().minusDays(2))
                .withCompetitionCloseDate(ZonedDateTime.now().minusDays(1))
                .withNonIfs(false)
                .build();
        when(competitionService.getPublicContentOfCompetition(competitionId)).thenReturn(publicContentItem);

        mockMvc.perform(get("/application/create/check-eligibility/{competitionId}", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/search"));
    }
}
