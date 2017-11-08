package org.innovateuk.ifs.analytics;

import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static java.lang.Long.parseLong;

public class GoogleAnalyticsDataLayerInterceptor extends HandlerInterceptorAdapter {
    private static final String ANALYTICS_DATA_LAYER_NAME = "dataLayer";

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionsRestService competitionRestService;

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private AssessmentRestService assessmentRestService;

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
                dl.setCompName(competitionRestService.getCompetitionById(competitionId).getSuccessObjectOrThrowException().getName());
            }
            else if (pathVariables.containsKey("projectId")) {
                final long projectId = parseLong((String) pathVariables.get("projectId"));
                dl.setCompName(projectRestService.getProjectById(projectId).getSuccessObjectOrThrowException().getName());
            }
            else if (pathVariables.containsKey("applicationId")) {
                final long applicationId = parseLong((String) pathVariables.get("applicationId"));
                dl.setCompName(applicationRestService.getApplicationById(applicationId).getSuccessObjectOrThrowException().getCompetitionName());
            }
//            else if (pathVariables.containsKey("assessmentId")) {
//                final long assessmentId = parseLong((String) pathVariables.get("assessmentId"));
//                final AssessmentResource assessmentResource = assessmentRestService.getById(assessmentId).getSuccessObjectOrThrowException();
//                dl.setCompName(competitionRestService.getCompetitionById(assessmentResource.getCompetition()).getSuccessObjectOrThrowException().getName());
//            }
        }
    }
}
