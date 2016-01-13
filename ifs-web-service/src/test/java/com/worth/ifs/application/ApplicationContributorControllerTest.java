package com.worth.ifs.application;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.exception.ErrorController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.Cookie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationContributorControllerTest extends BaseUnitTest
{

    @InjectMocks
    private ApplicationContributorController applicationContributorController;

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
        Cookie cookie = new Cookie("contributor_invite_state", "{\"organisationMap\":{\"SomeName\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\"}]}}");

//        contributorsForm.getOrganisationMap()
        MvcResult mockResult = mockMvc.perform(get(inviteUrl).cookie(cookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(viewName))
                .andExpect(model().attributeExists("organisationMap", "leadOrganisation", "leadApplicant", "contributorsForm"))
                .andReturn();

        ContributorsForm contributorsFormResult = (ContributorsForm) mockResult.getModelAndView().getModelMap().get("contributorsForm");
        assertNotNull(contributorsFormResult.getOrganisationMap().get("SomeName"));
        assertEquals(1, contributorsFormResult.getOrganisationMap().get("SomeName").size());
        assertEquals("Nico Bijl", contributorsFormResult.getOrganisationMap().get("SomeName").get(0).getPersonName());
        assertEquals("nico@worth.systems", contributorsFormResult.getOrganisationMap().get("SomeName").get(0).getEmail());

    }

    @Test
    public void testInviteContributorsPostAddPerson() throws Exception {

        mockMvc.perform(post(inviteUrl).param("add_person", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"organisationMap\":{\"3\":[{\"userId\":0,\"personName\":\"\",\"email\":\"\"}]}}"))
                .andExpect(view().name(redirectUrl));
    }

    @Test
    public void testInviteContributorsPostPerson() throws Exception {

        mockMvc.perform(
                    post(inviteUrl)
                    .param("organisationMap[3][0].personName", "Nico Bijl")
                    .param("organisationMap[3][0].email", "nico@worth.systems")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"organisationMap\":{\"3\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\"}]}}"))
                .andExpect(view().name(redirectUrl));

    }
    @Test
    public void testInviteContributorsRemovePerson() throws Exception {

        mockMvc.perform(
                post(inviteUrl)
                        .param("organisationMap[3][0].personName", "Nico Bijl")
                        .param("organisationMap[3][0].email", "nico@worth.systems")
                        .param("remove_person", "3_0")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"organisationMap\":{\"3\":[]}}"))
                .andExpect(view().name(redirectUrl));

    }
    @Test
    public void testInviteContributorsRemovePerson2() throws Exception {

        mockMvc.perform(
                post(inviteUrl)
                        .param("organisationMap[3][0].personName", "Nico Bijl")
                        .param("organisationMap[3][0].email", "nico@worth.systems")
                        .param("organisationMap[3][1].personName", "Brent de Kok")
                        .param("organisationMap[3][1].email", "brent@worth.systems")
                        .param("remove_person", "3_1")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("contributor_invite_state"))
                .andExpect(cookie().value("contributor_invite_state", "{\"organisationMap\":{\"3\":[{\"userId\":null,\"personName\":\"Nico Bijl\",\"email\":\"nico@worth.systems\"}]}}"))
                .andExpect(view().name(redirectUrl));

    }
}