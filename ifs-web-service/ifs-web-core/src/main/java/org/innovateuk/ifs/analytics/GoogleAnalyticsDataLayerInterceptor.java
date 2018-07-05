package org.innovateuk.ifs.analytics;

import org.innovateuk.ifs.analytics.service.GoogleAnalyticsDataLayerRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.lang.Long.parseLong;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
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
        setUserRoles(dl, request);
        setApplicationId(dl, request);
    }

    private static GoogleAnalyticsDataLayer getOrCreateDataLayer(ModelAndView modelAndView) {
        final Map<String, Object> model = modelAndView.getModel();
        if (!model.containsKey(ANALYTICS_DATA_LAYER_NAME)) {
            model.put(ANALYTICS_DATA_LAYER_NAME, new GoogleAnalyticsDataLayer());
        }
        return (GoogleAnalyticsDataLayer) model.get(ANALYTICS_DATA_LAYER_NAME);
    }

    private void setCompetitionName(GoogleAnalyticsDataLayer dataLayer, HttpServletRequest request) {
        final Map<String,String> pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables.containsKey(COMPETITION_ID)) {
            setCompetitionNameFromRestService(dataLayer,
                                              googleAnalyticsDataLayerRestService::getCompetitionName,
                                              getIdFromPathVariable(pathVariables, COMPETITION_ID));
        }
        else if (pathVariables.containsKey(PROJECT_ID)) {
            setCompetitionNameFromRestService(dataLayer,
                                              googleAnalyticsDataLayerRestService::getCompetitionNameForProject,
                                              getIdFromPathVariable(pathVariables, PROJECT_ID));
        }
        else if (pathVariables.containsKey(APPLICATION_ID)) {
            setCompetitionNameFromRestService(dataLayer,
                                              googleAnalyticsDataLayerRestService::getCompetitionNameForApplication,
                                              getIdFromPathVariable(pathVariables, APPLICATION_ID));
        }
        else if (pathVariables.containsKey(ASSESSMENT_ID)) {
            setCompetitionNameFromRestService(dataLayer,
                                              googleAnalyticsDataLayerRestService::getCompetitionNameForAssessment,
                                              getIdFromPathVariable(pathVariables, ASSESSMENT_ID));
        }
    }

    private void setUserRoles(GoogleAnalyticsDataLayer dataLayer, HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UserAuthentication) {
            UserAuthentication userAuth = (UserAuthentication) auth;

            List<Role> userRoles = simpleMap(
                    userAuth.getAuthorities(),
                    authority -> Role.getByName(authority.getAuthority().toLowerCase())
            );

            dataLayer.setUserRoles(userRoles);
        }

        final Map<String,String> pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables.containsKey(APPLICATION_ID)) {
            setApplicationOrProjectSpecificRolesFromRestService(dataLayer,
                                                                googleAnalyticsDataLayerRestService::getRolesByApplicationId,
                                                                getIdFromPathVariable(pathVariables, APPLICATION_ID));
        }

        if (pathVariables.containsKey(PROJECT_ID)) {
            setApplicationOrProjectSpecificRolesFromRestService(dataLayer,
                                                                googleAnalyticsDataLayerRestService::getRolesByProjectId,
                                                                getIdFromPathVariable(pathVariables, PROJECT_ID));
        }
    }

    private void setApplicationId(GoogleAnalyticsDataLayer dataLayer, HttpServletRequest request) {
        final Map<String,String> pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables.containsKey(APPLICATION_ID)) {
            final long applicationId = getIdFromPathVariable(pathVariables, APPLICATION_ID);
            dataLayer.setApplicationId(applicationId);
        }

        if (pathVariables.containsKey(PROJECT_ID)) {
            final long projectId = getIdFromPathVariable(pathVariables, PROJECT_ID);
            final long applicationId = googleAnalyticsDataLayerRestService.getApplicationIdForProject(projectId).getSuccess();
            dataLayer.setApplicationId(applicationId);
        }
    }

    private static void setCompetitionNameFromRestService(GoogleAnalyticsDataLayer dl,
                                                          Function<Long, RestResult<String>> f,
                                                          final long id) {
        final String competitionName = f.apply(id).getSuccess();
        if (competitionName != null) {
            dl.setCompetitionName(fromJson(competitionName, String.class));
        }
    }

    private static void setApplicationOrProjectSpecificRolesFromRestService(GoogleAnalyticsDataLayer dl, Function<Long, RestResult<List<Role>>> f, final long id) {
        final List<Role> roles = f.apply(id).getSuccess();
        dl.addUserRoles(roles);
    }

    private static long getIdFromPathVariable(final Map<String,String> pathVariables, final String pathVariable) {
        return parseLong(pathVariables.get(pathVariable));
    }


}