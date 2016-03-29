package com.worth.ifs.registration;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.application.AcceptInviteController;
import com.worth.ifs.exception.ErrorControllerAdvice;
import com.worth.ifs.security.CookieFlashMessageFilter;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class OrganisationTypeCreationControllerTest extends BaseUnitTest {
    @InjectMocks
    private OrganisationTypeCreationController organisationTypeCreationController;
    @Mock
    private Validator validator;
    @Mock
    CookieFlashMessageFilter cookieFlashMessageFilter;

    @Before
    public void setUp() throws Exception {

        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieDomain("domain");

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(organisationTypeCreationController, new ErrorControllerAdvice())
                .setViewResolvers(viewResolver())
                .setLocaleResolver(localeResolver)
                .addFilters(new CookieFlashMessageFilter())
                .build();


        super.setup();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();
        this.setupInvites();
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
                .andExpect(cookie().value("organisationType", "{\"organisationType\":1}"))
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
                .andExpect(cookie().value("organisationType", "{\"organisationType\":2}"))
                .andExpect(view().name("redirect:/organisation/create/type/new-account-organisation-type/?organisationType=2"));
    }
}