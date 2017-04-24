package org.innovateuk.ifs.interceptors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
public class AlertMessageHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final String ALERT_MESSAGES = "alertMessages";

    private static final Cache<String, List<AlertResource>> ALERT_CACHE
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
            alerts = ALERT_CACHE.get(ALERT_MESSAGES, () -> alertRestService.findAllVisible().getSuccessObjectOrThrowException());
        } catch (ExecutionException e) {
            alerts = emptyList();
        }

        if (!alerts.isEmpty()) {
            modelAndView.getModelMap().addAttribute(ALERT_MESSAGES, alerts);
        }
    }
}
