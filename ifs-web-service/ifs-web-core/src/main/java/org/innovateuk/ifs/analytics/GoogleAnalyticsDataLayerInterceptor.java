package org.innovateuk.ifs.analytics;

import org.innovateuk.ifs.analytics.service.GoogleAnalyticsDataLayerRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.function.Function;

import static java.lang.Long.parseLong;
import static org.innovateuk.ifs.util.JsonMappingUtil.fromJson;

/**
 * Interceptor to add Google Analytics data layer to the Model.
 */
public class GoogleAnalyticsDataLayerInterceptor extends HandlerInterceptorAdapter {
    static final String ANALYTICS_DATA_LAYER_NAME = "dataLayer";

    @Autowired
    private GoogleAnalyticsDataLayerRestService googleAnalyticsDataLayerRestService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            final Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            final Map<String, Object> model = modelAndView.getModel();
            if (!model.containsKey(ANALYTICS_DATA_LAYER_NAME)) {
                model.put(ANALYTICS_DATA_LAYER_NAME, new GoogleTagManagerDataLayer());
            }
            final GoogleTagManagerDataLayer dl = (GoogleTagManagerDataLayer) model.get(ANALYTICS_DATA_LAYER_NAME);

            if (pathVariables.containsKey("competitionId")) {
                final long competitionId = parseLong((String) pathVariables.get("competitionId"));
                dl.setCompName(fromJson(googleAnalyticsDataLayerRestService.getCompetitionName(competitionId).getSuccessObjectOrThrowException(), String.class));
            }
            else if (pathVariables.containsKey("projectId")) {
                final long projectId = parseLong((String) pathVariables.get("projectId"));
                dl.setCompName(fromJson(googleAnalyticsDataLayerRestService.getCompetitionNameForProject(projectId).getSuccessObjectOrThrowException(), String.class));
            }
            else if (pathVariables.containsKey("applicationId")) {
                final long applicationId = parseLong((String) pathVariables.get("applicationId"));
                dl.setCompName(fromJson(googleAnalyticsDataLayerRestService.getCompetitionNameForApplication(applicationId).getSuccessObjectOrThrowException(), String.class));
            }
            else if (pathVariables.containsKey("assessmentId")) {
                final long assessmentId = parseLong((String) pathVariables.get("assessmentId"));
                dl.setCompName(fromJson(googleAnalyticsDataLayerRestService.getCompetitionNameForAssessment(assessmentId).getSuccessObjectOrThrowException(), String.class));
            }
        }
    }

    private static void foo(GoogleTagManagerDataLayer dl, Function<Long, RestResult<String>> f, final Map pathVariables, String pathVariable) {
        final long id = parseLong((String) pathVariables.get(pathVariable));
        dl.setCompName(fromJson(f.apply(id).getSuccessObjectOrThrowException(), String.class));
    }
}