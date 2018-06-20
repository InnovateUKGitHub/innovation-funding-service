package org.innovateuk.ifs.analytics;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.analytics.service.GoogleAnalyticsDataLayerRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.innovateuk.ifs.analytics.GoogleAnalyticsDataLayerInterceptor.ANALYTICS_DATA_LAYER_NAME;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;

public class GoogleAnalyticsDataLayerInterceptorTest extends BaseUnitTest {

    @Mock
    private GoogleAnalyticsDataLayerRestService googleAnalyticsDataLayerRestServiceMock;

    @InjectMocks
    private GoogleAnalyticsDataLayerInterceptor googleAnalyticsDataLayerInterceptor;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private HttpServletResponse httpServletResponseMock;

    private ModelAndView mav;

    private final String expectedCompName = "competition name";

    @Before
    public void setUp() {
        super.setup();
        mav = new ModelAndView();
        setAnonymousAuthentication();
        when(httpServletRequestMock.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(emptyMap());
    }

    @Test
    public void postHandle() {
        final long expectedCompetitionId = 7L;

        when(googleAnalyticsDataLayerRestServiceMock.getCompetitionName(expectedCompetitionId)).thenReturn(RestResult.restSuccess(toJson(expectedCompName)));

        when(httpServletRequestMock.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(singletonMap("competitionId", Long.toString(expectedCompetitionId)));

        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleAnalyticsDataLayer expectedDataLayer = new GoogleAnalyticsDataLayer();
        expectedDataLayer.setCompetitionName(expectedCompName);
        expectedDataLayer.setUserRoles(emptyList());

        assertEquals(expectedDataLayer, mav.getModel().get(ANALYTICS_DATA_LAYER_NAME));

        verify(googleAnalyticsDataLayerRestServiceMock, only()).getCompetitionName(expectedCompetitionId);
    }

    @Test
    public void postHandle_applicationId() {
        final long expectedApplicationId = 7L;

        when(googleAnalyticsDataLayerRestServiceMock.getCompetitionNameForApplication(expectedApplicationId)).thenReturn(RestResult.restSuccess(toJson(expectedCompName)));
        when(googleAnalyticsDataLayerRestServiceMock.getRolesByApplicationId(expectedApplicationId)).thenReturn(RestResult.restSuccess(emptyList()));

        when(httpServletRequestMock.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(singletonMap("applicationId", Long.toString(expectedApplicationId)));

        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleAnalyticsDataLayer expectedDataLayer = new GoogleAnalyticsDataLayer();
        expectedDataLayer.setCompetitionName(expectedCompName);
        expectedDataLayer.setUserRoles(emptyList());
        expectedDataLayer.setApplicationId(expectedApplicationId);

        assertEquals(expectedDataLayer, mav.getModel().get(ANALYTICS_DATA_LAYER_NAME));

        verify(googleAnalyticsDataLayerRestServiceMock).getCompetitionNameForApplication(expectedApplicationId);
        verify(googleAnalyticsDataLayerRestServiceMock).getRolesByApplicationId(expectedApplicationId);

    }


    @Test
    public void postHandle_applicationRole() {
        final long expectedApplicationId = 321L;
        final List<Role> expectedRoles = singletonList(Role.COLLABORATOR);

        when(googleAnalyticsDataLayerRestServiceMock.getCompetitionNameForApplication(expectedApplicationId)).thenReturn(RestResult.restSuccess(toJson(expectedCompName)));
        when(googleAnalyticsDataLayerRestServiceMock.getRolesByApplicationId(expectedApplicationId)).thenReturn(RestResult.restSuccess(expectedRoles));

        when(httpServletRequestMock.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(singletonMap("applicationId", Long.toString(expectedApplicationId)));

        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleAnalyticsDataLayer expectedDataLayer = new GoogleAnalyticsDataLayer();
        expectedDataLayer.setCompetitionName(expectedCompName);
        expectedDataLayer.setUserRoles(expectedRoles);
        expectedDataLayer.setApplicationId(expectedApplicationId);

        assertEquals(expectedDataLayer, mav.getModel().get(ANALYTICS_DATA_LAYER_NAME));
        verify(googleAnalyticsDataLayerRestServiceMock).getCompetitionNameForApplication(expectedApplicationId);
        verify(googleAnalyticsDataLayerRestServiceMock).getRolesByApplicationId(expectedApplicationId);
    }

    @Test
    public void postHandle_projectId() {
        final long expectedProjectId = 7L;
        final long expectedApplicationId = 8L;

        when(googleAnalyticsDataLayerRestServiceMock.getCompetitionNameForProject(expectedProjectId)).thenReturn(RestResult.restSuccess(toJson(expectedCompName)));
        when(googleAnalyticsDataLayerRestServiceMock.getRolesByProjectId(expectedProjectId)).thenReturn(RestResult.restSuccess(emptyList()));
        when(googleAnalyticsDataLayerRestServiceMock.getApplicationIdForProject(expectedProjectId)).thenReturn(RestResult.restSuccess(expectedApplicationId));

        when(httpServletRequestMock.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(singletonMap("projectId", Long.toString(expectedProjectId)));

        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleAnalyticsDataLayer expectedDataLayer = new GoogleAnalyticsDataLayer();
        expectedDataLayer.setCompetitionName(expectedCompName);
        expectedDataLayer.setUserRoles(emptyList());
        expectedDataLayer.setApplicationId(expectedApplicationId);

        assertEquals(expectedDataLayer, mav.getModel().get(ANALYTICS_DATA_LAYER_NAME));

        verify(googleAnalyticsDataLayerRestServiceMock).getCompetitionNameForProject(expectedProjectId);
        verify(googleAnalyticsDataLayerRestServiceMock).getRolesByProjectId(expectedProjectId);
        verify(googleAnalyticsDataLayerRestServiceMock).getApplicationIdForProject(expectedProjectId);
    }

    @Test
    public void postHandle_projectRoles() {
        final long expectedProjectId = 123L;
        final long expectedApplicationId = 456L;
        final List<Role> expectedRoles = asList(Role.PARTNER, Role.PROJECT_MANAGER);

        when(googleAnalyticsDataLayerRestServiceMock.getCompetitionNameForProject(expectedProjectId)).thenReturn(RestResult.restSuccess(toJson(expectedCompName)));
        when(googleAnalyticsDataLayerRestServiceMock.getRolesByProjectId(expectedProjectId)).thenReturn(RestResult.restSuccess(expectedRoles));
        when(googleAnalyticsDataLayerRestServiceMock.getApplicationIdForProject(expectedProjectId)).thenReturn(RestResult.restSuccess(expectedApplicationId));

        when(httpServletRequestMock.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(singletonMap("projectId", Long.toString(expectedProjectId)));

        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleAnalyticsDataLayer expectedDataLayer = new GoogleAnalyticsDataLayer();
        expectedDataLayer.setCompetitionName(expectedCompName);
        expectedDataLayer.setUserRoles(expectedRoles);
        expectedDataLayer.setApplicationId(expectedApplicationId);

        assertEquals(expectedDataLayer, mav.getModel().get(ANALYTICS_DATA_LAYER_NAME));

        verify(googleAnalyticsDataLayerRestServiceMock).getCompetitionNameForProject(expectedProjectId);
        verify(googleAnalyticsDataLayerRestServiceMock).getRolesByProjectId(expectedProjectId);
        verify(googleAnalyticsDataLayerRestServiceMock).getApplicationIdForProject(expectedProjectId);
    }

    @Test
    public void postHandle_assessmentId() {
        final long expectedAssessmentId = 7L;

        when(googleAnalyticsDataLayerRestServiceMock.getCompetitionNameForAssessment(expectedAssessmentId)).thenReturn(RestResult.restSuccess(toJson(expectedCompName)));

        when(httpServletRequestMock.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(singletonMap("assessmentId", Long.toString(expectedAssessmentId)));

        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleAnalyticsDataLayer expectedDataLayer = new GoogleAnalyticsDataLayer();
        expectedDataLayer.setCompetitionName(expectedCompName);
        expectedDataLayer.setUserRoles(emptyList());

        assertEquals(expectedDataLayer, mav.getModel().get(ANALYTICS_DATA_LAYER_NAME));

        verify(googleAnalyticsDataLayerRestServiceMock, only()).getCompetitionNameForAssessment(expectedAssessmentId);
    }

    @Test
    public void postHandle_noParam() {
        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleAnalyticsDataLayer expectedDataLayer = new GoogleAnalyticsDataLayer();
        expectedDataLayer.setUserRoles(emptyList());

        assertEquals(expectedDataLayer, mav.getModel().get(ANALYTICS_DATA_LAYER_NAME));

        verifyZeroInteractions(googleAnalyticsDataLayerRestServiceMock);
    }

    @Test
    public void postHandle_singleRole() {
        Role [] expectedRoles = setAuthenticatedRoleTypes(Role.COMP_ADMIN);

        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleAnalyticsDataLayer expectedDataLayer = new GoogleAnalyticsDataLayer();
        expectedDataLayer.setUserRoles(singletonList(Role.COMP_ADMIN));

        assertEquals(expectedDataLayer, mav.getModel().get(ANALYTICS_DATA_LAYER_NAME));
    }

    @Test
    public void postHandle_multipleRoles() {
        Role [] expectedRoles = setAuthenticatedRoleTypes(Role.COMP_ADMIN, Role.IFS_ADMINISTRATOR);

        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleAnalyticsDataLayer expectedDataLayer = new GoogleAnalyticsDataLayer();
        expectedDataLayer.setUserRoles(asList(Role.COMP_ADMIN, Role.IFS_ADMINISTRATOR));

        assertEquals(expectedDataLayer, mav.getModel().get(ANALYTICS_DATA_LAYER_NAME));
    }

    @Test
    public void postHandle_anon() {
        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleAnalyticsDataLayer expectedDataLayer = new GoogleAnalyticsDataLayer();
        expectedDataLayer.setUserRoles(emptyList());

        assertEquals(expectedDataLayer, mav.getModel().get(ANALYTICS_DATA_LAYER_NAME));
    }

    private Role[] setAuthenticatedRoleTypes(Role... expectedRoles) {
        UserResource user = newUserResource()
                .withRolesGlobal(asList(expectedRoles))
                .build();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
        return expectedRoles;
    }

    private void setAnonymousAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("key", "principal", singletonList(new SimpleGrantedAuthority("anon"))));
    }
}