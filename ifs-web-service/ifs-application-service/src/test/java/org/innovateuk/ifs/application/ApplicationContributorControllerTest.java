package org.innovateuk.ifs.application;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.form.ContributorsForm;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.apache.commons.lang3.CharEncoding;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.Validator;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.util.Arrays;

import static org.innovateuk.ifs.application.ApplicationContributorController.APPLICATION_CONTRIBUTORS_INVITE;
import static org.innovateuk.ifs.application.ApplicationContributorController.APPLICATION_CONTRIBUTORS_REMOVE_CONFIRM;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationContributorControllerTest extends BaseControllerMockMVCTest<ApplicationContributorController> {

    @Mock
    private Validator validator;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    private Long applicationId;
    private Long alternativeApplicationId;
    private String redirectUrl;
    private String viewName;
    private String inviteUrl;
    private String applicationRedirectUrl;
    private String inviteOverviewRedirectUrl;
    private String removeUrl;


    @Override
    protected ApplicationContributorController supplyControllerUnderTest() {
        return new ApplicationContributorController();
    }

    @Before
    public void setUp() {
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();
        this.setupInvites();
        setupCookieUtil();

        applicationId = applications.get(0).getId();
        alternativeApplicationId = applicationId + 1;
        removeUrl = String.format("/application/%d/contributors/remove", applicationId);
        inviteUrl = String.format("/application/%d/contributors/invite", applicationId);
        redirectUrl = String.format("redirect:/application/%d/contributors/invite", applicationId);
        applicationRedirectUrl = String.format("redirect:/application/%d", applicationId);
        inviteOverviewRedirectUrl = String.format("redirect:/application/%d/contributors", applicationId);
        viewName = APPLICATION_CONTRIBUTORS_INVITE;


    }

    @Test
    public void testInviteContributors() throws Exception {
        mockMvc.perform(get(inviteUrl))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(viewName));
    }

    @Test
    public void testInviteContributorsCookie() throws Exception {
        Cookie cookie = new Cookie("contributor_invite_state", encryptor.encrypt(URLEncoder.encode("{\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationId\":3,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null}]}]}}", CharEncoding.UTF_8)));

//        contributorsForm.getOrganisationMap()
        MvcResult mockResult = mockMvc.perform(get(inviteUrl).cookie(cookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(viewName))
                .andExpect(model().attributeExists("leadOrganisation", "leadApplicant", "contributorsForm"))
                .andReturn();

        ContributorsForm contributorsFormResult = (ContributorsForm) mockResult.getModelAndView().getModelMap().get("contributorsForm");
        assertNotNull(contributorsFormResult.getOrganisations().get(0));
        assertEquals(1, contributorsFormResult.getOrganisations().get(0).getInvites().size());
        assertEquals("Nico Bijl", contributorsFormResult.getOrganisations().get(0).getInvites().get(0).getPersonName());
        assertEquals("nico@worth.systems", contributorsFormResult.getOrganisations().get(0).getInvites().get(0).getEmail());
    }

    @Test
    public void testInviteContributorsPostAddPerson() throws Exception {
        MvcResult result = mockMvc.perform(post(inviteUrl)
                .param("organisations[0].organisationName", "Empire Ltd")
                .param("organisations[0].organisationId", "1")
                .param("add_person", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(view().name(redirectUrl))
                .andReturn();

        assertEquals(URLEncoder.encode("{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"\",\"email\":\"\",\"inviteStatus\":null}]}]}", CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "contributor_invite_state"));
    }

    @Test
    public void testInviteContributorsPostPerson() throws Exception {
        MvcResult result = mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "nico@worth.systems")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(view().name(redirectUrl))
                .andReturn();

        assertEquals(URLEncoder.encode("{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null}]}]}", CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "contributor_invite_state"));
    }

    @Test
    public void testInviteContributorsPostDuplicatePerson() throws Exception {
        MvcResult result = mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "nico@worth.systems")
                        .param("organisations[0].invites[1].personName", "Nico Bijl")
                        .param("organisations[0].invites[1].email", "nico@worth.systems")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(view().name(redirectUrl))
                .andReturn();

        assertEquals(URLEncoder.encode("{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null},{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null}]}]}", CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "contributor_invite_state"));
    }

    @Test
    public void testInviteContributorsPostInvalidPerson() throws Exception {

        String expectedCookieVal = URLEncoder.encode("{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"\",\"inviteStatus\":null}]}]}", CharEncoding.UTF_8);
        String cookieName = "contributor_invite_state";

        MvcResult result = mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].organisationInviteId", "")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists(cookieName))
                .andExpect(view().name(redirectUrl))
                .andReturn();

        assertEquals(expectedCookieVal, getDecryptedCookieValue(result.getResponse().getCookies(), cookieName));
    }

    @Test
    public void testInviteContributorsBeginApplication() throws Exception {
        MvcResult resultOne = mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "nico@gmail.com")
                        .param("save_contributors", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(view().name(inviteOverviewRedirectUrl))
                .andReturn();

        assertEquals("", getDecryptedCookieValue(resultOne.getResponse().getCookies(), "contributor_invite_state"));


        MvcResult resultTwo = mockMvc.perform(
                post(inviteUrl + "")
                        .param("newApplication", "Empire Ltd")
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "nico@gmail.com")
                        .param("save_contributors", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(view().name(applicationRedirectUrl))
                .andReturn();

        assertEquals("", getDecryptedCookieValue(resultTwo.getResponse().getCookies(), "contributor_invite_state"));
    }

    @Test
    public void testLeadCanInviteToOtherOrganisation() throws Exception {
        MvcResult result = mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[1].organisationName", "Some Other Org Ltd")
                        .param("organisations[1].organisationId", "2")
                        .param("organisations[1].invites[0].personName", "Jim Kirk")
                        .param("organisations[1].invites[0].email", "j.kirk@starfleet.org")
                        .param("save_contributors", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(view().name(inviteOverviewRedirectUrl))
                .andReturn();

        assertEquals("", getDecryptedCookieValue(result.getResponse().getCookies(), "contributor_invite_state"));
    }

    @Test
    public void testNonLeadCannotInviteToOtherOrganisation() throws Exception {

        this.setupUserInvite("name", "user@email.com", 3L);
        this.loginNonLeadUser("user@email.com");

        MvcResult result = mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[1].organisationName", "Some Other Org Ltd")
                        .param("organisations[1].organisationId", "2")
                        .param("organisations[1].invites[0].personName", "Jim Kirk")
                        .param("organisations[1].invites[0].email", "j.kirk@starfleet.org")
                        .param("save_contributors", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(view().name(redirectUrl))
                .andReturn();

        assertEquals(URLEncoder.encode("{\"triedToSave\":true,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[]},{\"organisationName\":\"Some Other Org Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":2,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Jim Kirk\",\"email\":\"j.kirk@starfleet.org\",\"inviteStatus\":null}]}]}", CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "contributor_invite_state"));
    }



    @Test
    public void testNonLeadCanInviteToTheirOwnOrganisation() throws Exception {

        this.setupUserInvite("name", "user@email.com", 2L);
        this.loginNonLeadUser("user@email.com");

        MvcResult result = mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[1].organisationName", "Some Other Org Ltd")
                        .param("organisations[1].organisationId", "2")
                        .param("organisations[1].invites[0].personName", "Jim Kirk")
                        .param("organisations[1].invites[0].email", "j.kirk@starfleet.org")
                        .param("save_contributors", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(view().name(inviteOverviewRedirectUrl))
                .andReturn();

        assertEquals("", getDecryptedCookieValue(result.getResponse().getCookies(), "contributor_invite_state"));
    }



    @Test
    public void testInviteContributorsRemovePerson() throws Exception {
        MvcResult result = mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "nico@worth.systems")
                        .param("organisations[0].invites[1].personName", "Brent de Kok")
                        .param("organisations[0].invites[1].email", "brent@worth.systems")
                        .param("remove_person", "0_1")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(view().name(redirectUrl))
                .andReturn();

        assertEquals(URLEncoder.encode("{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null}]}]}", CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "contributor_invite_state"));
    }

    @Test
    public void testInviteContributorsRemovePerson2() throws Exception {
        MvcResult result = mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "nico@worth.systems")
                        .param("organisations[0].invites[1].personName", "Brent de Kok")
                        .param("organisations[0].invites[1].email", "brent@worth.systems")
                        .param("remove_person", "0_0")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(view().name(redirectUrl))
                .andReturn();

        assertEquals(URLEncoder.encode("{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Brent de Kok\",\"email\":\"brent@worth.systems\",\"inviteStatus\":null}]}]}", CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "contributor_invite_state"));

    }

    /**
     * When user adds a partner organisation, it should just add a empty person row, so the user can fill in directly.
     */
    @Test
    public void testInviteContributorsPostAddPartner() throws Exception {
        MvcResult result = mockMvc.perform(post(inviteUrl)
                .param("organisations[0].organisationName", "Empire Ltd")
                .param("organisations[0].organisationId", "1")
                .param("organisations[0].invites[0].personName", "Nico Bijl")
                .param("organisations[0].invites[0].email", "nico@worth.systems")
                .param("organisations[0].invites[1].personName", "Brent de Kok")
                .param("organisations[0].invites[1].email", "brent@worth.systems")
                .param("add_partner", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(view().name(redirectUrl))
                .andReturn();

        assertEquals(URLEncoder.encode("{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null},{\"userId\":null,\"personName\":\"Brent de Kok\",\"email\":\"brent@worth.systems\",\"inviteStatus\":null}]},{\"organisationName\":\"\",\"organisationNameConfirmed\":null,\"organisationId\":null,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"\",\"email\":\"\",\"inviteStatus\":null}]}]}", CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "contributor_invite_state"));
    }


    /**
     * When the last person is removed from a partner organisation, also remove the organisation.
     * @throws Exception
     */
    @Test
    public void testInviteContributorsPostRemovePersonAndPartner() throws Exception {
        MvcResult result = mockMvc.perform(post(inviteUrl)
                .param("organisations[0].organisationName", "Empire Ltd")
                .param("organisations[0].organisationId", "1")
                .param("organisations[0].invites[0].personName", "Nico Bijl")
                .param("organisations[0].invites[0].email", "nico@worth.systems")
                .param("organisations[0].invites[1].personName", "Brent de Kok")
                .param("organisations[0].invites[1].email", "brent@worth.systems")
                .param("organisations[1].organisationName", "SomePartner")
                .param("organisations[1].invites[0].personName", "Nico Bijl")
                .param("organisations[1].invites[0].email", "nico@worth.systems")
                .param("remove_person", "1_0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(view().name(redirectUrl))
                .andReturn();

        assertEquals(
                URLEncoder.encode("{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null},{\"userId\":null,\"personName\":\"Brent de Kok\",\"email\":\"brent@worth.systems\",\"inviteStatus\":null}]}]}", CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "contributor_invite_state"));
    }

    @Test
    public void testInviteContributorsPostRemovePersonFromPartner() throws Exception {
        MvcResult result = mockMvc.perform(post(inviteUrl)
                .param("organisations[0].organisationName", "Empire Ltd")
                .param("organisations[0].organisationId", "1")
                .param("organisations[0].invites[0].personName", "Nico Bijl")
                .param("organisations[0].invites[0].email", "nico@worth.systems")
                .param("organisations[0].invites[1].personName", "Brent de Kok")
                .param("organisations[0].invites[1].email", "brent@worth.systems")
                .param("organisations[1].organisationName", "SomePartner")
                .param("organisations[1].invites[0].personName", "Nico Bijl")
                .param("organisations[1].invites[0].email", "nico@worth.systems")
                .param("organisations[1].invites[1].personName", "Brent de Kok")
                .param("organisations[1].invites[1].email", "brent@worth.systems")
                .param("applicationId", applicationId.toString())
                .param("remove_person", "1_0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(view().name(redirectUrl))
                .andReturn();

        assertEquals(
                URLEncoder.encode("{\"triedToSave\":false,\"applicationId\":1,\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":1,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null},{\"userId\":null,\"personName\":\"Brent de Kok\",\"email\":\"brent@worth.systems\",\"inviteStatus\":null}]},{\"organisationName\":\"SomePartner\",\"organisationNameConfirmed\":null,\"organisationId\":null,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Brent de Kok\",\"email\":\"brent@worth.systems\",\"inviteStatus\":null}]}]}", CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "contributor_invite_state"));
    }

    @Test
    public void whenCookieHasDifferingApplicationIdFromGetParameterItShouldBeIgnored() throws Exception {
        Cookie cookie = new Cookie("contributor_invite_state", encryptor.encrypt(URLEncoder.encode("{\"applicationId\":"+alternativeApplicationId+",\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationNameConfirmed\":null,\"organisationId\":3,\"organisationInviteId\":null,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\",\"inviteStatus\":null}]}]}}", CharEncoding.UTF_8)));

        MvcResult mockResult = mockMvc.perform(get(inviteUrl).cookie(cookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(viewName))
                .andExpect(model().attributeExists("leadOrganisation", "leadApplicant", "contributorsForm"))
                .andReturn();

        ContributorsForm contributorsFormResult = (ContributorsForm) mockResult.getModelAndView().getModelMap().get("contributorsForm");
        assertNotNull(contributorsFormResult.getOrganisations().get(0));
        assertEquals(0, contributorsFormResult.getOrganisations().get(0).getInvites().size());
    }

    @Test
    public void whenUserIsRemovedRedirectToOverview() throws Exception {
        mockMvc.perform(
                post(removeUrl)
                        .param("applicationInviteId", "2")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(inviteOverviewRedirectUrl));
    }

    @Test
    public void whenUserHasNoJSUserMustConfirm() throws Exception {
        Long inviteId = 2314L;

        mockMvc.perform(
                get(removeUrl + String.format("/%d/confirm", inviteId))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(APPLICATION_CONTRIBUTORS_REMOVE_CONFIRM))
                .andExpect(model().attribute("inviteId", inviteId))
                .andExpect(model().attributeExists("removeContributorForm"));
    }

    private void loginNonLeadUser(String email) {
        UserResource user = newUserResource().withId(2L).withFirstName("test").withLastName("name").withEmail(email).build();
        loginUser(user);
    }

    private void setupUserInvite(String name, String email, Long organisationId) {
        InviteOrganisationResource inviteOrgResource = new InviteOrganisationResource();
        inviteOrgResource.setOrganisation(organisationId);
        inviteOrgResource.setInviteResources(Arrays.asList(new ApplicationInviteResource(null, name, email, null, null, null, null)));
        when(inviteRestService.getInvitesByApplication(isA(Long.class))).thenReturn(restSuccess(Arrays.asList(inviteOrgResource)));
    }
}