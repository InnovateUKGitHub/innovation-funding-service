package org.innovateuk.ifs.registration;

import org.apache.commons.lang3.CharEncoding;
import org.hamcrest.Matchers;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.Validator;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;

import static org.junit.Assert.assertEquals;
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
        this.setupCookieUtil();
    }

    @Test
    public void testChooseOrganisationType() throws Exception {
        mockMvc.perform(
                get("/organisation/create/type/new-account-organisation-type")
                        .cookie(new Cookie(AbstractAcceptInviteController.INVITE_HASH, encryptor.encrypt(INVITE_HASH)))
        )
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("registration/organisation/organisation-type"))
        .andExpect(model().attributeExists("form", "model"));
    }

    @Test
    @Ignore //TODO INFUND-8531 Need to rewrite this test when page is remade
    public void testChooseOrganisationTypeResearchSelected() throws Exception {
        mockMvc.perform(
                get("/organisation/create/type/new-account-organisation-type").param("organisationType", "2")
                        .cookie(new Cookie(AbstractAcceptInviteController.INVITE_HASH, encryptor.encrypt(INVITE_HASH)))
        )
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("registration/organisation/organisation-type"))
        .andExpect(model().attributeExists("form", "model"))
        .andExpect(model().attribute("model", Matchers.hasSize(3)));
    }

    /**
     * Test if the request is redirected forward, to the organisation creation controller.
     */
    @Test
    public void chooseOrganisationTypePostBusiness() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/organisation/create/type/new-account-organisation-type")
                        .cookie(new Cookie(AbstractAcceptInviteController.INVITE_HASH, encryptor.encrypt(INVITE_HASH)))
                        .param("organisationType", "1")

        )
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/organisation/create/find-organisation"))
        .andReturn();

        assertEquals(URLEncoder.encode("{\"organisationType\":1,\"selectedByDefault\":false}", CharEncoding.UTF_8), getDecryptedCookieValue(result.getResponse().getCookies(), "organisationType"));

    }


    /**
     * test if request is redirected back to the form for selecting the subtype of research.
     */
    @Test
    public void chooseOrganisationTypePostResearch() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/organisation/create/type/new-account-organisation-type")
                        .cookie(new Cookie(AbstractAcceptInviteController.INVITE_HASH, encryptor.encrypt(INVITE_HASH)))
                        .param("organisationType", "2")

        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"))
                .andReturn();

        assertEquals(URLEncoder.encode("{\"organisationType\":2,\"selectedByDefault\":false}", CharEncoding.UTF_8), getDecryptedCookieValue(result.getResponse().getCookies(), "organisationType"));
    }
}