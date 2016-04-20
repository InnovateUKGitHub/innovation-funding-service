package com.worth.ifs.registration;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.error.CommonErrors;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.exception.ErrorControllerAdvice;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class RegistrationControllerTest extends BaseControllerMockMVCTest<RegistrationController> {

    private static String VERIFY_HASH;
    private static String INVALID_VERIFY_HASH;
    @InjectMocks
    private RegistrationController registrationController;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    Validator validator;
    private Cookie inviteHashCookie;

    private Cookie usedInviteHashCookie;

    @Override
    protected RegistrationController supplyControllerUnderTest() {
        return new RegistrationController();
    }

    @Before
    public void setUp() {
        super.setUp();

        VERIFY_HASH = UUID.randomUUID().toString();
        INVALID_VERIFY_HASH = UUID.randomUUID().toString();
        MockitoAnnotations.initMocks(this);

        setupUserRoles();
        setupInvites();

        registrationController.setValidator(new LocalValidatorFactoryBean());

        when(userService.findUserByEmail(anyString())).thenReturn(restSuccess(new UserResource()));
        when(userService.findUserByEmailForAnonymousUserFlow(anyString())).thenReturn(restSuccess(new UserResource()));
        when(userService.createLeadApplicantForOrganisation(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyLong())).thenReturn(restSuccess(new UserResource()));

        when(userService.verifyEmail(eq(VERIFY_HASH))).thenReturn(restSuccess());
        when(userService.verifyEmail(eq(INVALID_VERIFY_HASH))).thenReturn(restFailure(CommonErrors.notFoundError(Invite.class, INVALID_VERIFY_HASH)));

        inviteHashCookie = new Cookie(AcceptInviteController.INVITE_HASH, INVITE_HASH);
        usedInviteHashCookie = new Cookie(AcceptInviteController.INVITE_HASH, ACCEPTED_INVITE_HASH);

    }

    @Test
    public void onGetRequestRegistrationViewIsReturned() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(get("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
        ;
    }

    @Test
    public void onGetRequestRegistrationViewIsReturnedWithInviteEmail() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(get("/registration/register?organisationId=1")
                .cookie(inviteHashCookie)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attribute("invitee", true))
        ;
    }

    @Test
    public void onGetRequestRegistrationViewIsReturnedWithUsedInviteEmail() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(get("/registration/register?organisationId=1")
                .cookie(usedInviteHashCookie)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class), eq("inviteAlreadyAccepted"));
    }

    @Test
    public void missingOrganisationGetParameterChangesViewWhenViewingForm() throws Exception {
        mockMvc.perform(get("/registration/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
        ;
    }

    @Test
    public void testSuccessUrl() throws Exception {
        mockMvc.perform(get("/registration/success").header("referer", "https://localhost/registration/register?organisationId=14"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/successful"))
        ;
    }

    @Test
    public void testVerifiedUrlNotFoundOnDirectAccess() throws Exception {
        mockMvc.perform(get("/registration/verified"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testVerifyEmail() throws Exception {
        mockMvc.perform(get("/registration/verify-email/" + VERIFY_HASH))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/verified"))
        ;
    }

    @Test
    public void testVerifyEmailInvalid() throws Exception {
        mockMvc.perform(get("/registration/verify-email/"+INVALID_VERIFY_HASH))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(ErrorControllerAdvice.URL_HASH_INVALID_TEMPLATE))
        ;
    }

    @Test
    public void organisationGetParameterOfANonExistentOrganisationChangesViewWhenViewingForm() throws Exception {
        mockMvc.perform(get("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
        ;
    }

    @Test
    public void missingOrganisationGetParameterChangesViewWhenSubmittingForm() throws Exception {
        mockMvc.perform(post("/registration/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
        ;
    }

    @Test
    public void organisationGetParameterOfANonExistentOrganisationChangesViewWhenSubmittingForm() throws Exception {
        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
        ;
    }

    @Test
    public void validButAlreadyExistingEmailInputShouldReturnErrorOnEmailField() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();

        String email = "alreadyexistingemail@test.test";

        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);
        when(userService.findUserByEmailForAnonymousUserFlow(email)).thenReturn(restSuccess(new UserResource()));

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", email)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
        ;
    }

    @Test
    public void emptyFormInputsShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "")
                        .param("password", "")
                        .param("retypedPassword", "")
                        .param("title", "")
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("phoneNumber", "")
                        .param("termsAndConditions", "")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "title"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "firstName"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "lastName"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "phoneNumber"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "termsAndConditions"))
        ;
    }

    @Test
    public void invalidEmailFormatShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);
        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "invalid email format")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
        ;
    }

    @Test
    public void invalidCharactersInEmailShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "{a|b}@test.test")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
        ;

        verifyNoMoreInteractions(userService);
    }



    @Test
    public void incorrectPasswordSizeShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", "12345")
                        .param("retypedPassword", "123456789012345678901234567890123")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"))
        ;
    }

    @Test
    public void unmatchedPasswordAndRetypePasswordShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", "12345678")
                        .param("retypedPassword", "123456789")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"))
        ;
    }

    @Test
    public void uncheckedTermsAndConditionsCheckboxShouldReturnError() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "termsAndConditions"))
        ;
    }

    @Test
    public void validRegisterPost() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();

        UserResource userResource = newUserResource()
                .withPassword("password123")
                .withFirstName("firstName")
                .withLastName("lastName")
                .withTitle("Mr")
                .withPhoneNumber("0123456789")
                .withEmail("test@test.test")
                .withId(1L)
                .build();


        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle(),
                userResource.getPhoneNumber(),
                1L,
                null)).thenReturn(restSuccess(userResource));
        when(userService.findUserByEmailForAnonymousUserFlow("test@test.test")).thenReturn(restFailure(notFoundError(User.class, "test@test.test")));

        mockMvc.perform(post("/registration/register?organisationId=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", userResource.getEmail())
                .param("password", userResource.getPassword())
                .param("retypedPassword", userResource.getPassword())
                .param("title", userResource.getTitle())
                .param("firstName", userResource.getFirstName())
                .param("lastName", userResource.getLastName())
                .param("phoneNumber", userResource.getPhoneNumber())
                .param("termsAndConditions", "1")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration/success"));

    }

    @Test
    public void validRegisterPostWithInvite() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();

        UserResource userResource = newUserResource()
                .withPassword("password")
                .withFirstName("firstName")
                .withLastName("lastName")
                .withTitle("Mr")
                .withPhoneNumber("0123456789")
                .withEmail("invited@email.com")
                .withId(1L)
                .build();

        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle(),
                userResource.getPhoneNumber(),
                1L,
                null)).thenReturn(restSuccess(userResource));
        when(userService.findUserByEmailForAnonymousUserFlow(eq("invited@email.com"))).thenReturn(restFailure(notFoundError(User.class, "invited@email.com")));
        when(inviteRestService.acceptInvite(eq(INVITE_HASH),anyLong())).thenReturn(restSuccess());
        mockMvc.perform(post("/registration/register?organisationId=1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(inviteHashCookie)
                .param("password", userResource.getPassword())
                .param("retypedPassword", userResource.getPassword())
                .param("title", userResource.getTitle())
                .param("firstName", userResource.getFirstName())
                .param("lastName", userResource.getLastName())
                .param("phoneNumber", userResource.getPhoneNumber())
                .param("termsAndConditions", "1")
        )
                .andExpect(view().name("redirect:/registration/success"))
                .andExpect(status().is3xxRedirection());

    }

    @Test
    public void correctOrganisationNameIsAddedToModel() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(4L).withName("uniqueOrganisationName").build();

        when(organisationService.getOrganisationByIdForAnonymousUserFlow(4L)).thenReturn(organisation);
        mockMvc.perform(post("/registration/register?organisationId=4")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(model().attribute("organisationName", "uniqueOrganisationName"));
    }

    @Test
    public void gettingRegistrationPageWithLoggedInUserShouldResultInRedirectOnly() throws Exception {
        when(userAuthenticationService.getAuthenticatedUser(isA(HttpServletRequest.class))).thenReturn(
                newUserResource().withRolesGlobal(singletonList(
                        newRoleResource().withName("testrolename").withUrl("testrolename/dashboard").build()
                )).build()
        );

        mockMvc.perform(get("/registration/register?organisationId=1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/testrolename/dashboard"))
        ;

    }

    @Test
    public void postingRegistrationWithLoggedInUserShouldResultInRedirectOnly() throws Exception {
        when(userAuthenticationService.getAuthenticatedUser(isA(HttpServletRequest.class))).thenReturn(
                newUserResource().withRolesGlobal(singletonList(
                        newRoleResource().withName("testrolename").withUrl("testrolename/dashboard").build()
                )).build()
        );

        mockMvc.perform(post("/registration/register?organisationId=1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/testrolename/dashboard"))
        ;

    }

    @Test
    public void errorsReturnedInEnvelopeAreAddedToTheModel() throws Exception {
        OrganisationResource organisation = newOrganisationResource().withId(1L).withName("Organisation 1").build();
        UserResource userResource = newUserResource()
                .withPassword("password")
                .withFirstName("firstName")
                .withLastName("lastName")
                .withTitle("Mr")
                .withPhoneNumber("0123456789")
                .withEmail("test@test.test")
                .withId(1L)
                .build();

        Error error = new Error("errorname", "errordescription", BAD_REQUEST);

        when(organisationService.getOrganisationByIdForAnonymousUserFlow(1L)).thenReturn(organisation);
        when(userService.createLeadApplicantForOrganisationWithCompetitionId(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle(),
                userResource.getPhoneNumber(),
                1L, null)).thenReturn(restFailure(error));
        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", userResource.getEmail())
                        .param("password", userResource.getPassword())
                        .param("retypedPassword", userResource.getPassword())
                        .param("title", userResource.getTitle())
                        .param("firstName", userResource.getFirstName())
                        .param("lastName", userResource.getLastName())
                        .param("phoneNumber", userResource.getPhoneNumber())
                        .param("termsAndConditions", "1")
        )
                .andExpect(model().hasErrors())
        ;
    }
}
