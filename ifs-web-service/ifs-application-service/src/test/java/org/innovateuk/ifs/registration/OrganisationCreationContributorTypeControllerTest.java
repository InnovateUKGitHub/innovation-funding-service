package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.registration.controller.OrganisationCreationContributorTypeController;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.util.CookieUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.Validator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.innovateuk.ifs.CookieTestUtil.encryptor;
import static org.innovateuk.ifs.CookieTestUtil.setupCookieUtil;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class OrganisationCreationContributorTypeControllerTest extends AbstractApplicationMockMVCTest<OrganisationCreationContributorTypeController> {

    @Mock
    private Validator validator;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private CookieUtil cookieUtil;

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
        this.setupFinances();
        this.setupInvites();
        this.setupOrganisationTypes();
        setupCookieUtil(cookieUtil);

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