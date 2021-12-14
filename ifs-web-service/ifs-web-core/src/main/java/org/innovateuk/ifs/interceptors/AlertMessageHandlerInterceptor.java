package org.innovateuk.ifs.interceptors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.alert.service.AlertRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyList;

/**
 * Look for alertmessages on every page that has a modelAndView
 */
@Slf4j
public class AlertMessageHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final String ALERT_MESSAGES = "alertMessages";

    private Cache<String, List<AlertResource>> alertCache
            = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();

    @Autowired
    private AlertRestService alertRestService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null) {
            addAlertMessages(modelAndView);
        }
    }

    private void addAlertMessages(ModelAndView modelAndView) {
        List<AlertResource> alerts;
        try {
            alerts = alertCache.get(ALERT_MESSAGES, () -> alertRestService.findAllVisible().getSuccess());
        } catch (ExecutionException | UncheckedExecutionException e) {
            log.error("exception thrown getting alert messages", e);
            alerts = emptyList();
        }

        if (!alerts.isEmpty()) {
            modelAndView.getModelMap().addAttribute(ALERT_MESSAGES, alerts);
        }
    }

}
