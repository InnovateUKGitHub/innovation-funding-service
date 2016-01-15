package com.worth.ifs.application;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.exception.ErrorController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.servlet.http.Cookie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationContributorControllerTest extends BaseUnitTest {

    @InjectMocks
    private ApplicationContributorController applicationContributorController;
    @Mock
    private LocalValidatorFactoryBean validator;

    private Long applicationId;
    private ContributorsForm contributorsForm;
    private String redirectUrl;
    private String viewName;
    private String inviteUrl;


    @Before
    public void setUp() {
        super.setup();
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(applicationContributorController, new ErrorController())
                .setViewResolvers(viewResolver())
                .build();


        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();

        applicationId = applications.get(0).getId();
        inviteUrl = String.format("/application/%d/contributors/invite", applicationId);
        redirectUrl = String.format("redirect:/application/%d/contributors/invite", applicationId);
        viewName = "application-contributors/invite";
    }

    @Test
    public void testInviteContributors() throws Exception {
        mockMvc.perform(get(inviteUrl))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(viewName));
    }

    @Test
    public void testInviteContributorsCookie() throws Exception {
        Cookie cookie = new Cookie("contributor_invite_state", "{\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationId\":3,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\"}]}]}");

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
        mockMvc.perform(post(inviteUrl)
                .param("organisations[0].organisationName", "Empire Ltd")
                .param("organisations[0].organisationId", "1")
                .param("add_person", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationId\":1,\"invites\":[{\"userId\":null,\"personName\":\"\",\"email\":\"\"}]}]}"))
                .andExpect(view().name(redirectUrl));
    }

    @Test
    public void testInviteContributorsPostPerson() throws Exception {
        mockMvc.perform(
                post(inviteUrl)
                        .param("organisations[0].organisationName", "Empire Ltd")
                        .param("organisations[0].organisationId", "1")
                        .param("organisations[0].invites[0].personName", "Nico Bijl")
                        .param("organisations[0].invites[0].email", "nico@worth.systems")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationId\":1,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\"}]}]}"))
                .andExpect(view().name(redirectUrl));
    }

    @Test
    public void testInviteContributorsRemovePerson() throws Exception {
        mockMvc.perform(
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
                .andExpect(cookie().value("contributor_invite_state", "{\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationId\":1,\"invites\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\"}]}]}"))
                .andExpect(view().name(redirectUrl));
    }

    @Test
    public void testInviteContributorsRemovePerson2() throws Exception {
        mockMvc.perform(
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
                .andExpect(cookie().value("contributor_invite_state", "{\"organisations\":[{\"organisationName\":\"Empire Ltd\",\"organisationId\":1,\"invites\":[{\"userId\":null,\"personName\":\"Brent de Kok\",\"email\":\"brent@worth.systems\"}]}]}"))
                .andExpect(view().name(redirectUrl));
    }
}