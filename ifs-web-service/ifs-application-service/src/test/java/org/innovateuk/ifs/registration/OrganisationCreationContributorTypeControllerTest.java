package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.Validator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class OrganisationCreationContributorTypeControllerTest extends BaseControllerMockMVCTest<OrganisationCreationContributorTypeController> {

    @Mock
    private Validator validator;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Override
    protected OrganisationCreationContributorTypeController supplyControllerUnderTest() {
        return new OrganisationCreationContributorTypeController();
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

        when(registrationCookieService.getInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(INVITE_HASH));
    }

    @Test
    public void testChooseOrganisationType() throws Exception {
        mockMvc.perform(
                get("/organisation/create/new-account-organisation-type")
                        .cookie(new Cookie(RegistrationCookieService.INVITE_HASH, encryptor.encrypt(INVITE_HASH)))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/organisation-type"))
                .andExpect(model().attributeExists("form", "model"));
    }

    /**
     * Test if the request is redirected forward, to the organisation creation controller.
     */
    @Test
    public void chooseOrganisationTypePostBusiness() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/organisation/create/new-account-organisation-type")
                        .cookie(new Cookie(RegistrationCookieService.INVITE_HASH, encryptor.encrypt(INVITE_HASH)))
                        .param("organisationType", "1")

        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"))
                .andReturn();

        verify(registrationCookieService, times(1)).saveToOrganisationTypeCookie(any(OrganisationTypeForm.class), any(HttpServletResponse.class));
    }


    /**
     * test if request is redirected back to the form for selecting the subtype of research.
     */
    @Test
    public void chooseOrganisationTypePostResearch() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/organisation/create/new-account-organisation-type")
                        .cookie(new Cookie(RegistrationCookieService.INVITE_HASH, encryptor.encrypt(INVITE_HASH)))
                        .param("organisationType", "2")

        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"))
                .andReturn();

        verify(registrationCookieService, times(1)).saveToOrganisationTypeCookie(any(OrganisationTypeForm.class), any(HttpServletResponse.class));
    }
}