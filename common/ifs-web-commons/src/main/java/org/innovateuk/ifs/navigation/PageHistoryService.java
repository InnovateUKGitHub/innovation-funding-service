package org.innovateuk.ifs.navigation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.innovateuk.ifs.util.EncodedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

import static javax.servlet.DispatcherType.ERROR;

@Service
public class PageHistoryService {

    private static final String PAGE_HISTORY_COOKIE_NAME = "pageHistory";
    private static final String APPLICATION_OVERVIEW_PAGE_HISTORY = "appOverviewPageHistory";

    @Autowired
    private EncodedCookieService encodedCookieService;

    public void recordPageHistory(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView, HandlerMethod handler) {
        Deque<PageHistory> history = getPageHistory(request).orElse(new LinkedList<>());
        while (history.contains(new PageHistory(request.getRequestURI()))) {
            history.pop();
        }

        if (handler.hasMethodAnnotation(NavigationRoot.class)) {
            history.clear();
        }

        if (!history.isEmpty()) {
            modelAndView.getModel().put("cookieBackLinkUrl", history.peek().buildUrl());
            modelAndView.getModel().put("cookieBackLinkText", history.peek().getName());
        }

        if (!handler.hasMethodAnnotation(ExcludeFromPageHistory.class)) {
            if (!ERROR.equals(request.getDispatcherType())) {
                history.push(new PageHistory(null, request.getRequestURI(), request.getQueryString()));
            }
        }
        encodedCookieService.saveToCookie(response, PAGE_HISTORY_COOKIE_NAME, JsonUtil.getSerializedObject(history));
    }

    public Optional<PageHistory> getPreviousPage(HttpServletRequest request) {
        return getPageHistory(request)
                .map(queue -> {
                    queue.pop(); // This is the current page.
                    return queue.peek();
                });
    }

    private Optional<Deque<PageHistory>> getPageHistory(HttpServletRequest request) {
        return encodedCookieService.getCookieAs(request, PAGE_HISTORY_COOKIE_NAME, new TypeReference<Deque<PageHistory>>() {
        });
    }

    private Optional<Deque<PageHistory>> getApplicationOverviewPageHistory(HttpServletRequest request) {
        return encodedCookieService.getCookieAs(request, APPLICATION_OVERVIEW_PAGE_HISTORY, new TypeReference<Deque<PageHistory>>() {
        });
    }

    public void recordLoanApplicationOverviewPageHistory(HttpServletRequest request, HttpServletResponse response, String name, String uri) {
        Deque<PageHistory> appOverviewHistory = getApplicationOverviewPageHistory(request).orElse(new LinkedList<>());
        appOverviewHistory.push(new PageHistory(name, uri, null));
        this.encodedCookieService.saveToCookie(response, APPLICATION_OVERVIEW_PAGE_HISTORY, JsonUtil.getSerializedObject(appOverviewHistory));
    }

    public Optional<PageHistory> getApplicationOverviewPage(HttpServletRequest request) {
        return getApplicationOverviewPageHistory(request)
                .map(queue -> {
                    return queue.pop();
                });
    }
}
