package org.innovateuk.ifs.navigation;

import org.innovateuk.ifs.util.EncodedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static javax.servlet.DispatcherType.ERROR;
import static org.innovateuk.ifs.navigation.PageHistoryService.PAGE_HISTORY_COOKIE_NAME;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PageHistoryServiceTest {

    @InjectMocks
    private PageHistoryService pageHistoryService;

    @Mock
    private EncodedCookieService encodedCookieService;

    private Deque<PageHistory> history;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ModelAndView modelAndView;
    private HandlerMethod handler;

    @Before
    public void setup() {
        history = new LinkedList<>();
        PageHistory pageFirst = new PageHistory("pageFirst", "/url/pageFirst");
        PageHistory pageSecond = new PageHistory("pageSecond", "/url/pageSecond");
        history.push(pageFirst);
        history.push(pageSecond);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        modelAndView = mock(ModelAndView.class);
        handler = mock(HandlerMethod.class);

        when(encodedCookieService.getCookieAs(eq(request), eq(PAGE_HISTORY_COOKIE_NAME), any()))
                .thenReturn(Optional.of(history));
    }

    @Test
    public void recordPageHistory() {
        Map<String, Object> model = new HashMap<>();
        when(modelAndView.getModel()).thenReturn(model);
        when(request.getRequestURI()).thenReturn("/url/pageThree");
        when(handler.hasMethodAnnotation(NavigationRoot.class)).thenReturn(false);

        pageHistoryService.recordPageHistory(request, response, modelAndView, handler);

        assertEquals(3, history.size());
        assertEquals("/url/pageSecond", model.get("cookieBackLinkUrl"));
        assertEquals("pageSecond", model.get("cookieBackLinkText"));
        verify(encodedCookieService).saveToCookie(response, PAGE_HISTORY_COOKIE_NAME, JsonUtil.getSerializedObject(history));
    }

    @Test
    public void recordPageHistory_navigationRoot() {
        when(request.getRequestURI()).thenReturn("/url/pageThree");
        when(handler.hasMethodAnnotation(NavigationRoot.class)).thenReturn(true);

        pageHistoryService.recordPageHistory(request, response, modelAndView, handler);

        assertEquals(1, history.size());
        verify(encodedCookieService).saveToCookie(response, PAGE_HISTORY_COOKIE_NAME, JsonUtil.getSerializedObject(history));
    }

    @Test
    public void recordPageHistory_ExcludeFromPageHistory() {
        when(request.getRequestURI()).thenReturn("/url/pageOne");
        when(handler.hasMethodAnnotation(NavigationRoot.class)).thenReturn(false);
        when(handler.hasMethodAnnotation(ExcludeFromPageHistory.class)).thenReturn(true);

        pageHistoryService.recordPageHistory(request, response, modelAndView, handler);

        assertEquals(2, history.size());
        verify(encodedCookieService, never()).saveToCookie(response, PAGE_HISTORY_COOKIE_NAME, JsonUtil.getSerializedObject(history));
    }

    @Test
    public void recordPageHistory_alreadyVisitedPage() {
        Map<String, Object> model = new HashMap<>();
        when(modelAndView.getModel()).thenReturn(model);
        when(request.getRequestURI()).thenReturn("/url/pageSecond");
        when(handler.hasMethodAnnotation(NavigationRoot.class)).thenReturn(false);

        pageHistoryService.recordPageHistory(request, response, modelAndView, handler);

        assertEquals(2, history.size());
        assertEquals("/url/pageFirst", model.get("cookieBackLinkUrl"));
        assertEquals("pageFirst", model.get("cookieBackLinkText"));
        verify(encodedCookieService).saveToCookie(response, PAGE_HISTORY_COOKIE_NAME, JsonUtil.getSerializedObject(history));
    }

    @Test
    public void recordPageHistory_errorPage() {
        Map<String, Object> model = new HashMap<>();
        when(modelAndView.getModel()).thenReturn(model);
        when(request.getRequestURI()).thenReturn("/error");
        when(request.getDispatcherType()).thenReturn(ERROR);
        when(handler.hasMethodAnnotation(NavigationRoot.class)).thenReturn(false);

        pageHistoryService.recordPageHistory(request, response, modelAndView, handler);

        assertEquals(2, history.size());
        assertEquals("/url/pageSecond", model.get("cookieBackLinkUrl"));
        assertEquals("pageSecond", model.get("cookieBackLinkText"));
        verify(encodedCookieService, never()).saveToCookie(any(), any(), any());
    }

    @Test
    public void recordPageHistoryWithQueryParams() {
        Map<String, Object> model = new HashMap<>();

        when(modelAndView.getModel()).thenReturn(model);
        when(request.getRequestURI()).thenReturn("/url/pageSecond");
        when(request.getQueryString()).thenReturn("ktp=true");
        when(handler.hasMethodAnnotation(NavigationRoot.class)).thenReturn(false);

        pageHistoryService.recordPageHistory(request, response, modelAndView, handler);


        when(modelAndView.getModel()).thenReturn(model);
        when(request.getRequestURI()).thenReturn("/url/pageThird");
        when(request.getQueryString()).thenReturn("ktp=true");
        when(handler.hasMethodAnnotation(NavigationRoot.class)).thenReturn(false);

        pageHistoryService.recordPageHistory(request, response, modelAndView, handler);

        assertEquals(3, history.size());
        PageHistory pageHistory = pageHistoryService.getPreviousPage(request).get();
        assertEquals("ktp=true", pageHistory.getQuery());
        verify(encodedCookieService).saveToCookie(response, PAGE_HISTORY_COOKIE_NAME, JsonUtil.getSerializedObject(history));
    }

}
