package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.form.ConfirmResearchOrganisationEligibilityForm;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.registration.service.OrganisationJourneyEnd;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.RESEARCH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ConfirmResearchOrganisationEligibilityControllerTest extends BaseControllerMockMVCTest<ConfirmResearchOrganisationEligibilityController> {

    private static final String BASE_URL = "/organisation/create";
    private static final String RESEARCH_ELIGIBILITY_TEMPLATE = "confirm-research-organisation-eligibility";
    private static final String TEMPLATE_PATH = "registration/organisation";
    private static final String VIEW = "application-process-view";

    private CompetitionResource competition;
    private OrganisationResource organisation;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private OrganisationJourneyEnd organisationJourneyEnd;

    @Override
    protected ConfirmResearchOrganisationEligibilityController supplyControllerUnderTest() {
        return new ConfirmResearchOrganisationEligibilityController();
    }

    @Before
    public void setuo() {
        competition = newCompetitionResource()
                .withId(1L)
                .withLeadApplicantType(singletonList(1L))
                .withMaxResearchRatio(60)
                .build();

        organisation = newOrganisationResource()
                .withId(2L)
                .withName("New Research Organisation")
                .withOrganisationType(2L)
                .withOrganisationTypeName(RESEARCH.name())
                .build();
    }

    @Test
    public void viewPage() throws Exception {
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));

        mockMvc.perform(get(BASE_URL + "/" + competition.getId() +"/confirm-eligibility/" + organisation.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(TEMPLATE_PATH + "/" + RESEARCH_ELIGIBILITY_TEMPLATE));
    }

    @Test
    public void researchUserChooseNo() throws Exception {
        ConfirmResearchOrganisationEligibilityForm form = new ConfirmResearchOrganisationEligibilityForm();
        form.setConfirmEligibility(false);

        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(registrationCookieService.isLeadJourney(any(HttpServletRequest.class))).thenReturn(false);
        when(registrationCookieService.isCollaboratorJourney(any(HttpServletRequest.class))).thenReturn(true);
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(organisationJourneyEnd.completeProcess(any(), any(), eq(loggedInUser), eq(organisation.getId()))).thenReturn(VIEW);

        mockMvc.perform(post(BASE_URL + "/" + competition.getId() +"/confirm-eligibility/" + organisation.getId())
                .param("confirmEligibility", "No"))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW));

        verify(organisationJourneyEnd).completeProcess(any(), any(), any(), eq(organisation.getId()));
    }

    @Test
    public void researchUserChooseYes() throws Exception {
        ConfirmResearchOrganisationEligibilityForm form = new ConfirmResearchOrganisationEligibilityForm();
        form.setConfirmEligibility(true);

        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(registrationCookieService.isLeadJourney(any(HttpServletRequest.class))).thenReturn(false);
        when(registrationCookieService.isCollaboratorJourney(any(HttpServletRequest.class))).thenReturn(true);
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

        mockMvc.perform(post(BASE_URL + "/" + competition.getId() +"/confirm-eligibility/" + organisation.getId())
                .param("confirmEligibility", "Yes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL + "/" + competition.getId() +"/confirm-eligibility/" + organisation.getId() + "/research-not-eligible"));
    }
}