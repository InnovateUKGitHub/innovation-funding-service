package org.innovateuk.ifs.application.creation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.service.CompaniesHouseRestService;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.util.CookieTestUtil.setupEncryptedCookieService;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationCreationControllerTest extends BaseControllerMockMVCTest<ApplicationCreationController> {

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private EncryptedCookieService cookieUtil;

    @Mock
    private CompaniesHouseRestService companiesHouseRestService;

    @Mock
    private PublicContentItemRestService publicContentItemRestService;

    @Override
    protected ApplicationCreationController supplyControllerUnderTest() {
        return new ApplicationCreationController();
    }

    private String COMPANY_ID = "08241216";
    private String COMPANY_NAME = "NETWORTHNET LTD";

    @Before
    public void setUpData() {

        setupEncryptedCookieService(cookieUtil);

        OrganisationSearchResult organisationSearchResult = new OrganisationSearchResult(COMPANY_ID, COMPANY_NAME);
        when(companiesHouseRestService.getOrganisationById(COMPANY_ID)).thenReturn(restSuccess(organisationSearchResult));
    }

    @Test
    public void checkEligibility() throws Exception {
        long competitionId = 1L;
        PublicContentItemResource publicContentItem = newPublicContentItemResource()
                .withCompetitionOpenDate(ZonedDateTime.now().minusDays(1))
                .withCompetitionCloseDate(ZonedDateTime.now().plusDays(1))
                .withNonIfs(false)
                .build();
        when(publicContentItemRestService.getItemByCompetitionId(competitionId)).thenReturn(restSuccess(publicContentItem));

        MvcResult result = mockMvc.perform(get("/application/create/start-application/{competitionId}", competitionId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/start-application"))
                .andReturn();

        verify(registrationCookieService, times(1)).deleteAllRegistrationJourneyCookies(any(HttpServletResponse.class));
        verify(registrationCookieService, times(1)).saveToCompetitionIdCookie(anyLong(), any(HttpServletResponse.class));
    }


    @Test
    public void checkEligibility_nonIfs() throws Exception {
        long competitionId = 1L;
        PublicContentItemResource publicContentItem = newPublicContentItemResource()
                .withCompetitionOpenDate(ZonedDateTime.now().minusDays(1))
                .withCompetitionCloseDate(ZonedDateTime.now().plusDays(1))
                .withNonIfs(true)
                .build();
        when(publicContentItemRestService.getItemByCompetitionId(competitionId)).thenReturn(restSuccess(publicContentItem));

        mockMvc.perform(get("/application/create/start-application/{competitionId}", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/search"));

        verify(registrationCookieService, never()).deleteAllRegistrationJourneyCookies(any(HttpServletResponse.class));
        verify(registrationCookieService, never()).saveToCompetitionIdCookie(anyLong(), any(HttpServletResponse.class));
    }


    @Test
    public void checkEligibility_early() throws Exception {
        long competitionId = 1L;
        PublicContentItemResource publicContentItem = newPublicContentItemResource()
                .withCompetitionOpenDate(ZonedDateTime.now().plusDays(1))
                .withCompetitionCloseDate(ZonedDateTime.now().plusDays(2))
                .withNonIfs(false)
                .build();
        when(publicContentItemRestService.getItemByCompetitionId(competitionId)).thenReturn(restSuccess(publicContentItem));

        mockMvc.perform(get("/application/create/start-application/{competitionId}", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/search"));

        verify(registrationCookieService, never()).deleteAllRegistrationJourneyCookies(any(HttpServletResponse.class));
        verify(registrationCookieService, never()).saveToCompetitionIdCookie(anyLong(), any(HttpServletResponse.class));
    }

    @Test
    public void checkEligibility_late() throws Exception {
        long competitionId = 1L;
        PublicContentItemResource publicContentItem = newPublicContentItemResource()
                .withCompetitionOpenDate(ZonedDateTime.now().minusDays(2))
                .withCompetitionCloseDate(ZonedDateTime.now().minusDays(1))
                .withNonIfs(false)
                .build();
        when(publicContentItemRestService.getItemByCompetitionId(competitionId)).thenReturn(restSuccess(publicContentItem));

        mockMvc.perform(get("/application/create/start-application/{competitionId}", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/search"));

        verify(registrationCookieService, never()).deleteAllRegistrationJourneyCookies(any(HttpServletResponse.class));
        verify(registrationCookieService, never()).saveToCompetitionIdCookie(anyLong(), any(HttpServletResponse.class));
    }
}
