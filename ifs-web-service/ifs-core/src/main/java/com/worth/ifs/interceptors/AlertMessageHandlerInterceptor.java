package com.worth.ifs.interceptors;

import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.application.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * Have the menu links globally available for each controller.
 * So it does not have to be added to each call separately anymore.
 */
public class AlertMessageHandlerInterceptor extends HandlerInterceptorAdapter {

    public static final String ALERT_MESSAGES = "alertMessages";

    @Autowired
    private AlertService alertService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if(modelAndView != null) {
            addAlertMessages(modelAndView);
        }
    }

    private void addAlertMessages(ModelAndView modelAndView) {
        List<AlertResource> alerts = alertService.findAllVisible();

        if(!alerts.isEmpty()) {
            modelAndView.getModelMap().addAttribute(ALERT_MESSAGES, alerts);
        }
    }
}
