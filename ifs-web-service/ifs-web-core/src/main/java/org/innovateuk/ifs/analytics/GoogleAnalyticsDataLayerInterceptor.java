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
                model.put(ANALYTICS_DATA_LAYER_NAME, new GoogleAnalyticsDataLayer());
            }
            final GoogleAnalyticsDataLayer dl = (GoogleAnalyticsDataLayer) model.get(ANALYTICS_DATA_LAYER_NAME);

            if (pathVariables.containsKey("competitionId")) {
                setCompetitionName(dl, id -> googleAnalyticsDataLayerRestService.getCompetitionName(id), pathVariables, "competitionId");
            }
            else if (pathVariables.containsKey("projectId")) {
                setCompetitionName(dl, id -> googleAnalyticsDataLayerRestService.getCompetitionNameForProject(id), pathVariables, "projectId");
            }
            else if (pathVariables.containsKey("applicationId")) {
                setCompetitionName(dl, id -> googleAnalyticsDataLayerRestService.getCompetitionNameForApplication(id), pathVariables, "applicationId");
            }
            else if (pathVariables.containsKey("assessmentId")) {
                setCompetitionName(dl, id -> googleAnalyticsDataLayerRestService.getCompetitionNameForAssessment(id), pathVariables, "assessmentId");
            }
        }
    }

    private static void setCompetitionName(GoogleAnalyticsDataLayer dl, Function<Long, RestResult<String>> f, final Map pathVariables, String pathVariable) {
        final long id = parseLong((String) pathVariables.get(pathVariable));
        final String competitionName = f.apply(id).getSuccessObjectOrThrowException();
        if (competitionName != null) {
            dl.setCompetitionName(fromJson(competitionName, String.class));
        }
    }
}