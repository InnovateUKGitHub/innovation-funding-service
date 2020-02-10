package org.innovateuk.ifs.interceptors;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.navigation.PageHistoryService;
import org.innovateuk.ifs.util.NavigationUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MenuLinksHandlerInterceptorTest extends BaseUnitTest {

    private static final String USER_DASHBOARD_LINK = "userDashboardLink";
    private static final String USER_PROFILE_LINK = "userProfileLink";
    private static final String ASSESSOR_PROFILE_URL = "/assessment/profile/details";
    private static final String USER_PROFILE_URL = "/profile/view";
    private static final String APPLICANT_DIRECT_LANDING_PAGE_URL ="applicant/dashboard";
    private static final String ASSESSOR_DIRECT_LANDING_PAGE_URL ="assessment/assessor/dashboard";


    @InjectMocks
    private MenuLinksHandlerInterceptor menuLinksHandlerInterceptor;

    @Mock
    private UserAuthenticationService userAuthenticationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private NavigationUtils navigationUtils;

    @Mock
    private PageHistoryService pageHistoryService;

    @Value("${logout.url}")
    private String logoutUrl;

    private ModelAndView mav;
    private final Object handler = new Object();

    @Before
    public void setup() {

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        Map<String, Object> view = new HashMap<>();
        mav = new ModelAndView("view", view);
        pageHistoryService.getPreviousPage(request);
    }

    @Test
    public void applicantShouldGetApplicantProfileLink() {

        UserAuthentication authentication = new UserAuthentication(newUserResource().withRoleGlobal(APPLICANT).build());
        ModelMap modelMap = mav.getModelMap();
        modelMap.addAttribute(USER_DASHBOARD_LINK, APPLICANT_DIRECT_LANDING_PAGE_URL );
        modelMap.addAttribute(USER_PROFILE_LINK, USER_PROFILE_URL );

        when(userAuthenticationService.getAuthentication(request)).thenReturn(authentication);
        when(navigationUtils.getDirectLandingPageUrl(request)).thenReturn(APPLICANT_DIRECT_LANDING_PAGE_URL);

        menuLinksHandlerInterceptor.postHandle(request, response, handler, mav);

        assertEquals(APPLICANT_DIRECT_LANDING_PAGE_URL, mav.getModelMap().get(USER_DASHBOARD_LINK));
        assertEquals(USER_PROFILE_URL, mav.getModelMap().get(USER_PROFILE_LINK));
        verify(userAuthenticationService, times(2)).getAuthenticatedUser(request);
        verify(navigationUtils, times(1)).getDirectLandingPageUrl(request);
    }

    @Test
    public void assessorShouldGetAssessorProfileLink() {

        UserAuthentication authentication = new UserAuthentication(newUserResource().withRoleGlobal(ASSESSOR).build());
        ModelMap modelMap = mav.getModelMap();
        modelMap.addAttribute(USER_DASHBOARD_LINK, ASSESSOR_DIRECT_LANDING_PAGE_URL );
        modelMap.addAttribute(USER_PROFILE_LINK, ASSESSOR_PROFILE_URL );

        when(userAuthenticationService.getAuthentication(request)).thenReturn(authentication);
        when(navigationUtils.getDirectLandingPageUrl(request)).thenReturn(ASSESSOR_DIRECT_LANDING_PAGE_URL);

        menuLinksHandlerInterceptor.postHandle(request, response, handler, mav);

        assertEquals(ASSESSOR_DIRECT_LANDING_PAGE_URL, mav.getModelMap().get(USER_DASHBOARD_LINK));
        assertEquals(USER_PROFILE_URL, mav.getModelMap().get(USER_PROFILE_LINK));
        verify(userAuthenticationService, times(2)).getAuthenticatedUser(request);
        verify(navigationUtils, times(1)).getDirectLandingPageUrl(request);
    }

    @Test
    public void multiRoleUserWithApplicantRoleChosenShouldGetUserProfileLink() {

        UserAuthentication authentication = new UserAuthentication(newUserResource().withRolesGlobal(asList(ASSESSOR, APPLICANT)).build());
        ModelMap modelMap = mav.getModelMap();
        modelMap.addAttribute(USER_DASHBOARD_LINK, APPLICANT_DIRECT_LANDING_PAGE_URL);
        modelMap.addAttribute(USER_PROFILE_LINK, USER_PROFILE_URL);

        when(userAuthenticationService.getAuthentication(request)).thenReturn(authentication);
        when(navigationUtils.getDirectLandingPageUrl(request)).thenReturn(APPLICANT_DIRECT_LANDING_PAGE_URL);

        menuLinksHandlerInterceptor.postHandle(request, response, handler, mav);

        assertEquals(APPLICANT_DIRECT_LANDING_PAGE_URL, mav.getModelMap().get(USER_DASHBOARD_LINK));
        assertEquals(USER_PROFILE_URL, mav.getModelMap().get(USER_PROFILE_LINK));
        verify(userAuthenticationService, times(2)).getAuthenticatedUser(request);
        verify(navigationUtils, times(1)).getDirectLandingPageUrl(request);
    }
}