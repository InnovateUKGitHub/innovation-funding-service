package org.innovateuk.ifs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.controller.CustomFormBindingControllerAdvice;
import org.innovateuk.ifs.controller.LoggedInUserMethodArgumentResolver;
import org.innovateuk.ifs.controller.ValidationHandlerMethodArgumentResolver;
import org.innovateuk.ifs.exception.ErrorControllerAdvice;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.formatter.RejectionReasonFormatter;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;

/**
 * This is the base class for testing Controllers using MockMVC in addition to standard Mockito mocks.  Using MockMVC
 * allows Controllers to be tested via their routes and their responses' HTTP responses tested also.
 */
public abstract class BaseControllerMockMVCTest<ControllerType> extends BaseUnitTest {
    public static final Log LOG = LogFactory.getLog(BaseControllerMockMVCTest.class);

    @InjectMocks
    protected ControllerType controller = supplyControllerUnderTest();

    protected MockMvc mockMvc;

    protected abstract ControllerType supplyControllerUnderTest();

    @Mock
    protected Environment env;

    @Mock
    protected MessageSource messageSource;

    protected UserResource applicant = newUserResource().withId(1L)
            .withFirstName("James")
            .withLastName("Watts")
            .withEmail("james.watts@email.co.uk")
            .withRolesGlobal(singletonList(Role.APPLICANT))
            .withUID("2aerg234-aegaeb-23aer").build();

    protected UserResource assessor = newUserResource().withId(3L)
            .withFirstName("Clark")
            .withLastName("Baker")
            .withEmail("clark.baker@email.co.uk")
            .withRolesGlobal(singletonList(Role.ASSESSOR))
            .withUID("2522-34y34ah-hrt4420").build();

    protected UserResource stakeholder = newUserResource().withId(3L)
            .withFirstName("Troy")
            .withLastName("Perez")
            .withEmail("troy.perez@email.co.uk")
            .withRolesGlobal(singletonList(Role.STAKEHOLDER))
            .withUID("2522-34y34ah-hrt4420").build();

    protected UserResource assessorAndApplicant = newUserResource().withId(4L)
            .withFirstName("Fred")
            .withLastName("Smith")
            .withEmail("fred.smith@email.co.uk")
            .withRolesGlobal(asList(Role.APPLICANT, Role.ASSESSOR))
            .withUID("1234-abcdefgh-abc1234").build();

    protected UserResource innovationLeadAndApplicant = newUserResource().withId(4L)
            .withFirstName("Fred")
            .withLastName("Smith")
            .withEmail("fred.smith@email.co.uk")
            .withRolesGlobal(asList(Role.APPLICANT, Role.INNOVATION_LEAD))
            .withUID("1234-abcdefgh-abc1234").build();

    protected UserResource liveProjectsAndApplicant = newUserResource().withId(4L)
            .withFirstName("Fred")
            .withLastName("Smith")
            .withEmail("fred.smith@email.co.uk")
            .withRolesGlobal(asList(Role.APPLICANT, Role.LIVE_PROJECTS_USER))
            .withUID("1234-abcdefgh-abc1234").build();

    protected UserResource stakeholderAndAssessor = newUserResource().withId(4L)
            .withFirstName("Maria")
            .withLastName("Briggs")
            .withEmail("maria.briggs@email.co.uk")
            .withRolesGlobal(asList(Role.ASSESSOR, Role.STAKEHOLDER))
            .withUID("1234-abcdefgh-abc1234").build();

    protected UserResource stakeholderAndApplicant = newUserResource().withId(4L)
            .withFirstName("Ken")
            .withLastName("Brown")
            .withEmail("ken.brown@email.co.uk")
            .withRolesGlobal(asList(Role.APPLICANT, Role.STAKEHOLDER))
            .withUID("1234-abcdefgh-abc1234").build();

    protected UserResource assessorAndApplicantAndStakeholder = newUserResource().withId(4L)
            .withFirstName("Ollie")
            .withLastName("Jones")
            .withEmail("ollie.jones@email.co.uk")
            .withRolesGlobal(asList(Role.APPLICANT, Role.ASSESSOR, Role.STAKEHOLDER))
            .withUID("1234-abcdefgh-abc1234").build();

    protected UserResource collaborator = newUserResource().withId(2L)
            .withFirstName("John")
            .withLastName("Patricks")
            .withEmail("john.patricks@email.co.uk")
            .withRolesGlobal(singletonList(Role.APPLICANT))
            .withUID("6573ag-aeg32aeb-23aerr").build();

    protected UserResource support = newUserResource().withId(2L)
            .withFirstName("Support")
            .withLastName("Support")
            .withEmail("support@email.co.uk")
            .withRolesGlobal(singletonList(Role.SUPPORT))
            .withUID("6573ag-aeg32aeb-23aerr").build();

    protected UserResource admin = newUserResource().withId(2L)
            .withFirstName("Admin")
            .withLastName("Admin")
            .withEmail("admin@email.co.uk")
            .withRolesGlobal(singletonList(Role.IFS_ADMINISTRATOR))
            .withUID("6573ag-aeg32aeb-23aerr").build();

    protected UserResource loggedInUser = applicant;

    @Before
    public void logInUserBeforeTests() {

        mockMvc = setupMockMvc(controller, this::getLoggedInUser, env, messageSource);

        setLoggedInUser(loggedInUser);
    }

    public static <ControllerType> MockMvc setupMockMvc(ControllerType controller, Supplier<UserResource> loggedInUserSupplier, Environment environment, MessageSource messageSource) {

        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieDomain("domain");

        FormattingConversionService formattingConversionService = new DefaultFormattingConversionService();
        formattingConversionService.addFormatter(new RejectionReasonFormatter());

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setConversionService(formattingConversionService)
                .setControllerAdvice(
                        new ErrorControllerAdvice(),
                        new CustomFormBindingControllerAdvice()
                )
                .addFilter(new CookieFlashMessageFilter())
                .setLocaleResolver(localeResolver)
                .setHandlerExceptionResolvers(createExceptionResolver(environment, messageSource))
                .setCustomArgumentResolvers(
                        new ValidationHandlerMethodArgumentResolver(),
                        getLoggedInUserMethodArgumentResolver(loggedInUserSupplier)

                )
                .setViewResolvers(viewResolver())
                .build();

        return mockMvc;
    }

    private static InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/resources");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    private static LoggedInUserMethodArgumentResolver getLoggedInUserMethodArgumentResolver(Supplier<UserResource> loggedInUserSupplier) {

        LoggedInUserMethodArgumentResolver argumentResolver = new LoggedInUserMethodArgumentResolver();

        ReflectionTestUtils.setField(argumentResolver, "userAuthenticationService", new UserAuthenticationService() {

            @Override
            public Authentication getAuthentication(HttpServletRequest request) {
                return new UserAuthentication(loggedInUserSupplier.get());
            }

            @Override
            public UserResource getAuthenticatedUser(HttpServletRequest request) {
                return loggedInUserSupplier.get();
            }
        });

        return argumentResolver;
    }

    public static ExceptionHandlerExceptionResolver createExceptionResolver(Environment env, MessageSource messageSource) {
        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(ErrorControllerAdvice.class).resolveMethod(exception);
                return new ServletInvocableHandlerMethod(new ErrorControllerAdvice(env, messageSource), method);
            }
        };
        exceptionResolver.afterPropertiesSet();
        return exceptionResolver;
    }

    protected void setLoggedInUserAuthentication(UserAuthentication user) {
        SecurityContextHolder.getContext().setAuthentication(user);
    }

    /**
     * Get the user on the Spring Security ThreadLocals
     */
    protected UserResource getLoggedInUser() {
        UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getDetails() : null;
    }

    /**
     * Set a user on the Spring Security ThreadLocals
     *
     * @param user
     */
    protected void setLoggedInUser(UserResource user) {
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
    }

    protected void logoutCurrentUser() {
        setLoggedInUser(null);
    }
}