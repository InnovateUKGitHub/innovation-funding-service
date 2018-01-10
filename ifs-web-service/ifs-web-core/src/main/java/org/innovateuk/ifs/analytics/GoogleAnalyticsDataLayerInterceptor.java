package org.innovateuk.ifs.analytics;

import org.innovateuk.ifs.analytics.service.GoogleAnalyticsDataLayerRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.function.Function;

import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.joining;
import static org.innovateuk.ifs.util.JsonMappingUtil.fromJson;

/**
 * Interceptor to add Google Analytics data layer to the Model.
 */
public class GoogleAnalyticsDataLayerInterceptor extends HandlerInterceptorAdapter {
    static final String ANALYTICS_DATA_LAYER_NAME = "dataLayer";

    private static final String COMPETITION_ID = "competitionId";
    private static final String PROJECT_ID = "projectId";
    private static final String APPLICATION_ID = "applicationId";
    private static final String ASSESSMENT_ID = "assessmentId";

    @Autowired
    private GoogleAnalyticsDataLayerRestService googleAnalyticsDataLayerRestService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView == null) return;
        final GoogleAnalyticsDataLayer dl = getOrCreateDataLayer(modelAndView);

        setCompetitionName(dl, request);
        setUserRoles(dl);
    }

    private static GoogleAnalyticsDataLayer getOrCreateDataLayer(ModelAndView modelAndView) {
        final Map<String, Object> model = modelAndView.getModel();
        if (!model.containsKey(ANALYTICS_DATA_LAYER_NAME)) {
            model.put(ANALYTICS_DATA_LAYER_NAME, new GoogleAnalyticsDataLayer());
        }
        return (GoogleAnalyticsDataLayer) model.get(ANALYTICS_DATA_LAYER_NAME);
    }

    private void setCompetitionName(GoogleAnalyticsDataLayer dl, HttpServletRequest request) {
        final Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables.containsKey(COMPETITION_ID)) {
            setCompetitionNameFromRestService(dl, googleAnalyticsDataLayerRestService::getCompetitionName, pathVariables, COMPETITION_ID);
        }
        else if (pathVariables.containsKey(PROJECT_ID)) {
            setCompetitionNameFromRestService(dl, googleAnalyticsDataLayerRestService::getCompetitionNameForProject, pathVariables, PROJECT_ID);
        }
        else if (pathVariables.containsKey(APPLICATION_ID)) {
            setCompetitionNameFromRestService(dl, googleAnalyticsDataLayerRestService::getCompetitionNameForApplication, pathVariables, APPLICATION_ID);
        }
        else if (pathVariables.containsKey(ASSESSMENT_ID)) {
            setCompetitionNameFromRestService(dl, googleAnalyticsDataLayerRestService::getCompetitionNameForAssessment, pathVariables, ASSESSMENT_ID);
        }
    }

    private static void setUserRoles(GoogleAnalyticsDataLayer dl) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UserAuthentication) {
            UserAuthentication userAuth = (UserAuthentication) auth;
            dl.setUserRole(userAuth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(joining(",")));
        }
        else if (auth instanceof AnonymousAuthenticationToken) {
                dl.setUserRole("anonymous");
        }
    }

    private static void setCompetitionNameFromRestService(GoogleAnalyticsDataLayer dl, Function<Long, RestResult<String>> f, final Map pathVariables, String pathVariable) {
        final long id = parseLong((String) pathVariables.get(pathVariable));
        final String competitionName = f.apply(id).getSuccessObjectOrThrowException();
        if (competitionName != null) {
            dl.setCompetitionName(fromJson(competitionName, String.class));
        }
    }
}