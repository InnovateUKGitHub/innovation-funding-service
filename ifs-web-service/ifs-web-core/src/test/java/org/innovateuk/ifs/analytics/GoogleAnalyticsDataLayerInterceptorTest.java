package org.innovateuk.ifs.analytics;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.analytics.service.GoogleAnalyticsDataLayerRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;

public class GoogleAnalyticsDataLayerInterceptorTest extends BaseUnitTestMocksTest {

    @Mock
    private GoogleAnalyticsDataLayerRestService googleAnalyticsDataLayerRestServiceMock;

    @InjectMocks
    private GoogleAnalyticsDataLayerInterceptor googleAnalyticsDataLayerInterceptor;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private HttpServletResponse httpServletResponseMock;

    private ModelAndView mav;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        mav = new ModelAndView();
    }

    @Test
    public void postHandle() throws Exception {
        final String expectedCompName = "competition name";
        final long expectedCompetitionId = 7L;

        when(googleAnalyticsDataLayerRestServiceMock.getCompetitionName(expectedCompetitionId)).thenReturn(RestResult.restSuccess(toJson(expectedCompName)));

        when(httpServletRequestMock.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(singletonMap("competitionId", Long.toString(expectedCompetitionId)));

        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleTagManagerDataLayer expectedDataLayer = new GoogleTagManagerDataLayer();
        expectedDataLayer.setCompName(expectedCompName);

        assertEquals(expectedDataLayer, mav.getModel().get("dataLayer"));

        verify(googleAnalyticsDataLayerRestServiceMock, only()).getCompetitionName(expectedCompetitionId);
    }

    @Test
    public void postHandle_applicationId() throws Exception {
        final String expectedCompName = "competition name";
        final long expectedApplicationId = 7L;

        when(googleAnalyticsDataLayerRestServiceMock.getCompetitionNameForApplication(expectedApplicationId)).thenReturn(RestResult.restSuccess(toJson(expectedCompName)));

        when(httpServletRequestMock.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(singletonMap("applicationId", Long.toString(expectedApplicationId)));

        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleTagManagerDataLayer expectedDataLayer = new GoogleTagManagerDataLayer();
        expectedDataLayer.setCompName(expectedCompName);

        assertEquals(expectedDataLayer, mav.getModel().get("dataLayer"));

        verify(googleAnalyticsDataLayerRestServiceMock, only()).getCompetitionNameForApplication(expectedApplicationId);
    }

    @Test
    public void postHandle_projectId() throws Exception {
        final String expectedCompName = "competition name";
        final long expectedProjectId = 7L;

        when(googleAnalyticsDataLayerRestServiceMock.getCompetitionNameForProject(expectedProjectId)).thenReturn(RestResult.restSuccess(toJson(expectedCompName)));

        when(httpServletRequestMock.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(singletonMap("projectId", Long.toString(expectedProjectId)));

        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleTagManagerDataLayer expectedDataLayer = new GoogleTagManagerDataLayer();
        expectedDataLayer.setCompName(expectedCompName);

        assertEquals(expectedDataLayer, mav.getModel().get("dataLayer"));

        verify(googleAnalyticsDataLayerRestServiceMock, only()).getCompetitionNameForProject(expectedProjectId);
    }

    @Test
    public void postHandle_assessmentId() throws Exception {
        final String expectedCompName = "competition name";
        final long expectedAssessmentId = 7L;

        when(googleAnalyticsDataLayerRestServiceMock.getCompetitionNameForAssessment(expectedAssessmentId)).thenReturn(RestResult.restSuccess(toJson(expectedCompName)));

        when(httpServletRequestMock.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(singletonMap("assessmentId", Long.toString(expectedAssessmentId)));

        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleTagManagerDataLayer expectedDataLayer = new GoogleTagManagerDataLayer();
        expectedDataLayer.setCompName(expectedCompName);

        assertEquals(expectedDataLayer, mav.getModel().get("dataLayer"));

        verify(googleAnalyticsDataLayerRestServiceMock, only()).getCompetitionNameForAssessment(expectedAssessmentId);
    }

    @Test
    public void postHandle_noParam() throws Exception {
        when(httpServletRequestMock.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(emptyMap());

        googleAnalyticsDataLayerInterceptor.postHandle(httpServletRequestMock, httpServletResponseMock, null, mav);

        GoogleTagManagerDataLayer expectedDataLayer = new GoogleTagManagerDataLayer();

        assertEquals(expectedDataLayer, mav.getModel().get("dataLayer"));

        verifyZeroInteractions(googleAnalyticsDataLayerRestServiceMock);
    }
}