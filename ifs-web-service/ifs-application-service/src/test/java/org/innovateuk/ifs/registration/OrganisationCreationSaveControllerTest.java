package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.registration.controller.OrganisationCreationSaveController;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.CookieUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Validator;

import java.util.Collections;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_TEAM;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrganisationCreationSaveControllerTest extends BaseControllerMockMVCTest<OrganisationCreationSaveController> {

    private static String COMPANY_NAME = "organisation name";
    private static String COMPANY_ID = "1";
    private static String INVITE_HASH = "123abc";
    private static Long COMPETITION_ID = 1L;
    private static Long ORGANISATION_ID = 2L;
    private static Long APPLICATION_ID = 3L;
    private static Long TEAM_QUESTION_ID = 4L;
    private static Long ASSESSOR_ID = 5L;

    protected OrganisationCreationSaveController supplyControllerUnderTest() {
        return new OrganisationCreationSaveController();
    }

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private OrganisationSearchRestService organisationSearchRestService;

    @Mock
    private Validator validator;

    @Mock
    private InviteOrganisationRestService inviteOrganisationRestService;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private OrganisationTypeRestService organisationTypeRestService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private InviteRestService inviteRestService;

    private OrganisationTypeForm organisationTypeForm;
    private OrganisationCreationForm organisationForm;

    @Before
    public void setup(){
        super.setUp();
        setLoggedInUser(null);

        OrganisationSearchResult organisationSearchResult = new OrganisationSearchResult(COMPANY_ID, COMPANY_NAME);

        when(organisationSearchRestService.getOrganisation(anyLong(), anyString())).thenReturn(restSuccess(organisationSearchResult));
        when(organisationTypeRestService.findOne(anyLong())).thenReturn(restSuccess(new OrganisationTypeResource()));

        AddressForm addressForm = new AddressForm();
        addressForm.setPostcodeInput("ABC 12345");
        addressForm.setSelectedPostcodeIndex(null);
        addressForm.setPostcodeOptions(Collections.emptyList());

        organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setOrganisationType(1L);

        organisationForm = new OrganisationCreationForm();
        organisationForm.setAddressForm(addressForm);
        organisationForm.setTriedToSave(true);
        organisationForm.setOrganisationSearchName(null);
        organisationForm.setSearchOrganisationId(COMPANY_ID);
        organisationForm.setOrganisationSearching(false);
        organisationForm.setManualEntry(false);
        organisationForm.setOrganisationSearchResults(Collections.emptyList());
        organisationForm.setOrganisationName("NOMENSA LTD");
    }

    @Test
    public void saveOrganisation() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(registrationCookieService.getInviteHashCookieValue(any())).thenReturn(Optional.of(INVITE_HASH));
        when(inviteOrganisationRestService.getByIdForAnonymousUserFlow(anyLong())).thenReturn(restSuccess(newInviteOrganisationResource().build()));
        when(organisationService.createAndLinkByInvite(any(), any())).thenReturn(newOrganisationResource().withId(2L).build());
        when(inviteOrganisationRestService.put(any())).thenReturn(restSuccess());

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/register"));

        verify(registrationCookieService, times(1)).saveToOrganisationIdCookie(eq(2L), any());
    }

    @Test
    public void saveOrganisation_loggedInUser() throws Exception {
        setLoggedInUser(loggedInUser);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(registrationCookieService.getInviteHashCookieValue(any())).thenReturn(Optional.empty());
        when(organisationService.createOrMatch(any())).thenReturn(newOrganisationResource().withId(ORGANISATION_ID).build());
        when(registrationCookieService.getCompetitionIdCookieValue(any())).thenReturn(Optional.of(COMPETITION_ID));
        when(applicationService.createApplication(COMPETITION_ID, loggedInUser.getId(), ORGANISATION_ID, "")).thenReturn(newApplicationResource().withId(APPLICATION_ID).withCompetition(COMPETITION_ID).build());
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(COMPETITION_ID, APPLICATION_TEAM))
                                .thenReturn(restSuccess(newQuestionResource().withId(TEAM_QUESTION_ID).build()));

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/application/%s/form/question/%s", APPLICATION_ID, TEAM_QUESTION_ID)));

        verify(applicationService).createApplication(COMPETITION_ID, loggedInUser.getId(), ORGANISATION_ID, "");
    }

    @Test
    public void saveOrganisation_loggedInUserOldApplication() throws Exception {
        setLoggedInUser(loggedInUser);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(registrationCookieService.getInviteHashCookieValue(any())).thenReturn(Optional.empty());
        when(organisationService.createOrMatch(any())).thenReturn(newOrganisationResource().withId(ORGANISATION_ID).build());
        when(registrationCookieService.getCompetitionIdCookieValue(any())).thenReturn(Optional.of(COMPETITION_ID));
        when(applicationService.createApplication(COMPETITION_ID, loggedInUser.getId(), ORGANISATION_ID, "")).thenReturn(newApplicationResource().withId(APPLICATION_ID).withCompetition(COMPETITION_ID).build());
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(COMPETITION_ID, APPLICATION_TEAM))
                .thenReturn(restFailure(Collections.emptyList(), HttpStatus.NOT_FOUND));

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/application/%s/team", APPLICATION_ID)));

        verify(applicationService).createApplication(COMPETITION_ID, loggedInUser.getId(), ORGANISATION_ID, "");
    }

    @Test
    public void saveOrganisation_loggedInAssessor() throws Exception {
        setLoggedInUser(newUserResource().withId(ASSESSOR_ID).withRolesGlobal(asList(Role.ASSESSOR)).build());
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(registrationCookieService.getInviteHashCookieValue(any())).thenReturn(Optional.empty());
        when(organisationService.createOrMatch(any())).thenReturn(newOrganisationResource().withId(ORGANISATION_ID).build());
        when(registrationCookieService.getCompetitionIdCookieValue(any())).thenReturn(Optional.of(COMPETITION_ID));
        when(userRestService.grantRole(ASSESSOR_ID, Role.APPLICANT)).thenReturn(restSuccess());
        when(applicationService.createApplication(COMPETITION_ID, ASSESSOR_ID, ORGANISATION_ID, "")).thenReturn(newApplicationResource().withId(APPLICATION_ID).withCompetition(COMPETITION_ID).build());
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(COMPETITION_ID, APPLICATION_TEAM))
                .thenReturn(restSuccess(newQuestionResource().withId(TEAM_QUESTION_ID).build()));

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/application/%s/form/question/%s", APPLICATION_ID, TEAM_QUESTION_ID)));

        verify(applicationService).createApplication(COMPETITION_ID, ASSESSOR_ID, ORGANISATION_ID, "");
        verify(cookieUtil).saveToCookie(any(), eq("role"), eq(Role.APPLICANT.getName()));
        verify(userRestService).grantRole(ASSESSOR_ID, Role.APPLICANT);
    }

    @Test
    public void saveOrganisation_loggedInInvitee() throws Exception {
        setLoggedInUser(loggedInUser);
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(registrationCookieService.getInviteHashCookieValue(any())).thenReturn(Optional.of(INVITE_HASH));

        when(inviteRestService.acceptInvite(INVITE_HASH, loggedInUser.getId())).thenReturn(restSuccess());
        when(inviteRestService.getInviteByHash(INVITE_HASH)).thenReturn(restSuccess(newApplicationInviteResource().withApplication(APPLICATION_ID).build()));
        when(applicationService.getById(APPLICATION_ID)).thenReturn(newApplicationResource().withId(APPLICATION_ID).withCompetition(COMPETITION_ID).build());

        when(organisationService.createAndLinkByInvite(any(), any())).thenReturn(newOrganisationResource().withId(2L).build());
        when(registrationCookieService.getCompetitionIdCookieValue(any())).thenReturn(Optional.empty());
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(COMPETITION_ID, APPLICATION_TEAM))
                .thenReturn(restSuccess(newQuestionResource().withId(TEAM_QUESTION_ID).build()));

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/application/%s/form/question/%s", APPLICATION_ID, TEAM_QUESTION_ID)));

        verify(inviteRestService).acceptInvite(INVITE_HASH, loggedInUser.getId());
        verify(registrationCookieService).deleteInviteHashCookie(any());
    }

    @Test
    public void saveOrganisation_createOrMatchServiceCallIsMadeWhenHashIsNotPresent() throws Exception {
        when(registrationCookieService.getInviteHashCookieValue(any())).thenReturn(Optional.empty());
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(organisationService.createOrMatch(any())).thenReturn(newOrganisationResource().withId(2L).build());

        mockMvc.perform(post("/organisation/create/save-organisation"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/register"));

        verify(organisationService, times(1)).createOrMatch(any());
        verify(organisationService, times(0)).createAndLinkByInvite(any(), any());
    }

    @Test
    public void saveOrganisation_createAndLinkByInviteServiceCallIsMadeWhenHashIsPresent() throws Exception {

        when(registrationCookieService.getInviteHashCookieValue(any())).thenReturn(Optional.of(INVITE_HASH));
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));
        when(organisationService.createAndLinkByInvite(any(), any())).thenReturn(newOrganisationResource().withId(2L).build());

        mockMvc.perform(post("/organisation/create/save-organisation")
                .param("searchOrganisationId", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/register"));

        verify(organisationService, times(0)).createOrMatch(any());
        verify(organisationService, times(1)).createAndLinkByInvite(any(), any());
    }

    @Test
    public void confirmBusiness() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/confirm-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/confirm-organisation"))
                .andExpect(model().attributeExists("organisationForm"));
    }

    @Test
    public void confirmCompany() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.of(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.of(organisationForm));

        mockMvc.perform(get("/organisation/create/confirm-organisation"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("selectedOrganisation"))
                .andExpect(model().attribute("selectedOrganisation", hasProperty("name", equalTo(COMPANY_NAME))))
                .andExpect(view().name("registration/organisation/confirm-organisation"));
    }
}