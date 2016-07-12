package com.worth.ifs.application;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.exception.ErrorControllerAdvice;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.registration.AcceptInviteController;
import com.worth.ifs.registration.service.RegistrationService;
import com.worth.ifs.user.resource.UserResource;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AcceptInviteControllerTest extends BaseUnitTest {

    @InjectMocks
    private AcceptInviteController acceptInviteController;

    @Mock
    private Validator validator;
    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    
    @Mock
    private RegistrationService registrationService;

    @Before
    public void setUp() throws Exception {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        super.setup();

        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieDomain("domain");

        final StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("exceptionHandler", ErrorControllerAdvice.class);

        final WebMvcConfigurationSupport webMvcConfigurationSupport = new WebMvcConfigurationSupport();
        webMvcConfigurationSupport.setApplicationContext(applicationContext);

        mockMvc = MockMvcBuilders.standaloneSetup(acceptInviteController, new ErrorControllerAdvice())
                .setHandlerExceptionResolvers(webMvcConfigurationSupport.handlerExceptionResolver())
                .setViewResolvers(viewResolver())
                .setLocaleResolver(localeResolver)
                .addFilters(new CookieFlashMessageFilter())
                .build();


        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();
        this.setupInvites();
    }

    @Test
    public void testInviteEntryPage() throws Exception {
        mockMvc.perform(
                get(String.format("/accept-invite/%s", INVITE_HASH))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().exists(AcceptInviteController.INVITE_HASH))
                .andExpect(cookie().value(AcceptInviteController.INVITE_HASH, INVITE_HASH))
                .andExpect(view().name("registration/accept-invite"));
    }

    @Test
    public void testInviteEntryPageExistingUser() throws Exception {
    	Map<String, String> errors = new HashMap<>();
    	errors.put("errorkey", "errorvalue");
        when(registrationService.getInvalidInviteMessages(isA(UserResource.class), isA(InviteResource.class), isA(InviteOrganisationResource.class))).thenReturn(errors);

        mockMvc.perform(
                get(String.format("/accept-invite/%s", INVITE_HASH_EXISTING_USER))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().exists(AcceptInviteController.INVITE_HASH))
                .andExpect(cookie().value(AcceptInviteController.INVITE_HASH, INVITE_HASH_EXISTING_USER))
                .andExpect(model().attribute("emailAddressRegistered", "true"))
                .andExpect(model().attribute("errorkey", "errorvalue"))
                .andExpect(view().name("registration/accept-invite-failure"));
    }

    @Test(expected = Exception.class)
    public void testInviteEntryPageInvalid() throws Exception {
        mockMvc.perform(
                get(String.format("/accept-invite/%s", INVALID_INVITE_HASH))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().value(AcceptInviteController.INVITE_HASH, ""))
                .andExpect(view().name("url-hash-invalid"));
    }
    @Test
    public void testInviteEntryPageAccepted() throws Exception {
        mockMvc.perform(
                get(String.format("/accept-invite/%s", ACCEPTED_INVITE_HASH))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().value(AcceptInviteController.INVITE_HASH, ""))
                .andExpect(view().name("redirect:/login"));
    }
}