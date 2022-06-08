package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.form.ConfirmResearchOrganisationEligibilityForm;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.innovateuk.ifs.registration.service.OrganisationJourneyEnd;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.RESEARCH;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ConfirmResearchOrganisationEligibilityControllerTest extends BaseControllerMockMVCTest<ConfirmResearchOrganisationEligibilityController> {

    private static final String BASE_URL = "/organisation/create";
    private static final String RESEARCH_ELIGIBILITY_TEMPLATE = "confirm-research-organisation-eligibility";
    private static final String TEMPLATE_PATH = "registration/organisation";
    private static final String VIEW = "application-process-view";
    private static final String FIND_ORGANISATION = "find-organisation";

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

    @Mock
    private PublicContentItemRestService publicContentItemRestService;

    @Override
    protected ConfirmResearchOrganisationEligibilityController supplyControllerUnderTest() {
        return new ConfirmResearchOrganisationEligibilityController();
    }

    @Before
    public void setup() {
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
    public void existingResearchOrganisationConfirmEligibilityViewPage() throws Exception {

        PublicContentResource publicContentResource = newPublicContentResource().build();

        PublicContentItemResource publicContentItemResource = newPublicContentItemResource()
                .withPublicContentResource(publicContentResource)
                .build();

        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(publicContentItemRestService.getItemByCompetitionId(competition.getId())).thenReturn(restSuccess(publicContentItemResource));

        mockMvc.perform(get(BASE_URL + "/" + competition.getId() +"/confirm-eligibility"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(TEMPLATE_PATH + "/" + RESEARCH_ELIGIBILITY_TEMPLATE));
    }

    @Test
    public void newResearchOrganisationConfirmEligibilityViewPage() throws Exception {

        PublicContentResource publicContentResource = newPublicContentResource().build();

        PublicContentItemResource publicContentItemResource = newPublicContentItemResource()
                .withPublicContentResource(publicContentResource)
                .build();

        when(publicContentItemRestService.getItemByCompetitionId(competition.getId())).thenReturn(restSuccess(publicContentItemResource));

        mockMvc.perform(get(BASE_URL + "/" + competition.getId() +"/confirm-eligibility"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(TEMPLATE_PATH + "/" + RESEARCH_ELIGIBILITY_TEMPLATE));
    }

    @Test
    public void researchUserChooseYes() throws Exception {
        ConfirmResearchOrganisationEligibilityForm form = new ConfirmResearchOrganisationEligibilityForm();
        form.setConfirmEligibility(true);

        PublicContentItemResource publicContentItemResource = newPublicContentItemResource()
                .withPublicContentResource()
                .build();

        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(publicContentItemRestService.getItemByCompetitionId(competition.getId())).thenReturn(restSuccess(publicContentItemResource));

        mockMvc.perform(post(BASE_URL + "/" + competition.getId() +"/confirm-eligibility/" + organisation.getId())
                .param("confirmEligibility", "Yes"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL + "/" + competition.getId() +"/confirm-eligibility/research-not-eligible/" + organisation.getId()));
    }

    @Test
    public void existingResearchUserChooseNo() throws Exception {
        ConfirmResearchOrganisationEligibilityForm form = new ConfirmResearchOrganisationEligibilityForm();
        form.setConfirmEligibility(false);

        PublicContentItemResource publicContentItemResource = newPublicContentItemResource()
                .withPublicContentResource()
                .build();

        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(publicContentItemRestService.getItemByCompetitionId(competition.getId())).thenReturn(restSuccess(publicContentItemResource));
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
    public void newResearchUserChooseNo() throws Exception {
        ConfirmResearchOrganisationEligibilityForm form = new ConfirmResearchOrganisationEligibilityForm();
        form.setConfirmEligibility(false);

            PublicContentItemResource publicContentItemResource = newPublicContentItemResource()
                    .withPublicContentResource()
                    .build();

        when(publicContentItemRestService.getItemByCompetitionId(competition.getId())).thenReturn(restSuccess(publicContentItemResource));

        mockMvc.perform(post(BASE_URL + "/" + competition.getId() +"/confirm-eligibility")
                .param("confirmEligibility", "No"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL + "/" + FIND_ORGANISATION));
    }
}