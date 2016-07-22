package com.worth.ifs.registration;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import org.apache.commons.lang3.CharEncoding;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.Validator;

import javax.servlet.http.Cookie;

import java.net.URLEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class OrganisationTypeCreationControllerTest extends BaseControllerMockMVCTest<OrganisationTypeCreationController> {
    @Mock
    private Validator validator;
    @Mock
    CookieFlashMessageFilter cookieFlashMessageFilter;

    @Override
    protected OrganisationTypeCreationController supplyControllerUnderTest() {
        return new OrganisationTypeCreationController();
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
        this.setupOrganisationTypes();
    }

    @Test
    public void testChooseOrganisationType() throws Exception {
        mockMvc.perform(
                get("/organisation/create/type/new-account-organisation-type")
                .cookie(new Cookie(AcceptInviteController.INVITE_HASH, INVITE_HASH))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/organisation-type"))
                .andExpect(model().attributeExists("organisationTypeForm", "organisationTypes", "organisationTypeForm", "invite"))
                .andExpect(model().attribute("organisationTypes", Matchers.hasSize(4)));

    }
    @Test
    public void testChooseOrganisationTypeResearchSelected() throws Exception {
        mockMvc.perform(
                get("/organisation/create/type/new-account-organisation-type").param("organisationType", "2")
                        .cookie(new Cookie(AcceptInviteController.INVITE_HASH, INVITE_HASH))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/organisation-type"))
                .andExpect(model().attributeExists("organisationTypeForm", "organisationTypes", "organisationTypeForm", "invite"))
                .andExpect(model().attribute("organisationTypes", Matchers.hasSize(5)));

    }

    /**
     * Test if the request is redirected forward, to the organisation creation controller.
     */
    @Test
    public void chooseOrganisationTypePostBusiness() throws Exception {
        mockMvc.perform(
                post("/organisation/create/type/new-account-organisation-type")
                        .cookie(new Cookie(AcceptInviteController.INVITE_HASH, INVITE_HASH))
                        .param("organisationType", "1")

        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().value("organisationType", URLEncoder.encode("{\"organisationType\":1}", CharEncoding.UTF_8)))
                .andExpect(view().name("redirect:/organisation/create/find-organisation"));
    }


    /**
     * test if request is redirected back to the form for selecting the subtype of research.
     */
    @Test
    public void chooseOrganisationTypePostResearch() throws Exception {
        mockMvc.perform(
                post("/organisation/create/type/new-account-organisation-type")
                        .cookie(new Cookie(AcceptInviteController.INVITE_HASH, INVITE_HASH))
                        .param("organisationType", "2")

        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().value("organisationType", URLEncoder.encode("{\"organisationType\":2}", CharEncoding.UTF_8)))
                .andExpect(view().name("redirect:/organisation/create/type/new-account-organisation-type/?organisationType=2"));
    }
}