package com.worth.ifs.application;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ResponseService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.application.service.UserService;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.exception.ObjectNotFoundException;
import com.worth.ifs.application.helper.ApplicationHelper;
import com.worth.ifs.security.UserAuthenticationService;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * This controller will handle all requests that are related to the application overview.
 * Application overview is the page that contains the most basic information about the current application and
 * the basic information about the competition the application is related to.
 */
@Controller
@RequestMapping("/application")
public class ApplicationController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    ResponseService responseService;

    @Autowired
    ApplicationService applicationService;

    @Autowired
    SectionService sectionService;

    @Autowired
    UserService userService;

    @Autowired
    UserAuthenticationService userAuthenticationService;


    @RequestMapping("/{applicationId}")
    public String applicationDetails(Model model, @PathVariable("applicationId") final Long applicationId){
        log.info("Application with id " + applicationId);
        this.addApplicationDetails(applicationId, model);
        return "application-details";
    }

    @RequestMapping("/{applicationId}/section/{sectionId}")
    public String applicationDetailsOpenSection(Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId){
        addApplicationDetails(applicationId, model);
        model.addAttribute("currentSectionId", sectionId);
        return "application-details";
    }

    @RequestMapping("/{applicationId}/summary")
    public String applicationSummary(Model model, @PathVariable("applicationId") final Long applicationId){
        List<Response> responses = responseService.getByApplication(applicationId);
        model.addAttribute("responses", responseService.mapResponsesToQuestion(responses));

        addApplicationDetails(applicationId, model);
        return "application-summary";
    }

    @RequestMapping("/{applicationId}/confirm-submit")
    public String applicationConfirmSubmit(Model model, @PathVariable("applicationId") final Long applicationId){
        addApplicationDetails(applicationId, model);
        return "application-confirm-submit";
    }

    @RequestMapping("/{applicationId}/submit")
    public String applicationSubmit(Model model, @PathVariable("applicationId") final Long applicationId){
        applicationService.updateStatus(applicationId, ApplicationStatusConstants.SUBMITTED.getId());
        addApplicationDetails(applicationId, model);
        return "application-submitted";
    }

    /**
     * Get the details of the current application, add this to the model so we can use it in the templates.
     *
     * @param applicationId represents the application
     * @param model model that contains the details for the application detail page
     */
    private void addApplicationDetails(Long applicationId, Model model) {
        ApplicationHelper applicationHelper = new ApplicationHelper();
        Application application = applicationService.getById(applicationId);

        if(application == null){
            throw new ObjectNotFoundException("Application not found.");
        }

        Competition competition = application.getCompetition();
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);
        model.addAttribute("applicationOrganisations", applicationHelper.getApplicationOrganisations(application));
        model.addAttribute("leadOrganisation", applicationHelper.getApplicationLeadOrganisation(application).orElseGet(() -> null));
        model.addAttribute("sections", sectionService.getParentSections(competition.getSections()));
        model.addAttribute("completedSections", sectionService.getCompleted(applicationId));
        model.addAttribute("incompletedSections", sectionService.getInCompleted(applicationId));
        List<Response> responses = responseService.getByApplication(applicationId);
        model.addAttribute("responses", responseService.mapResponsesToQuestion(responses));
        model.addAttribute("completedQuestionsPercentage", applicationService.getCompleteQuestionsPercentage(application.getId()));
        model.addAttribute("assignableUsers", userService.getAssignable(application.getId()));

        int todayDay =  LocalDateTime.now().getDayOfYear();
        model.addAttribute("todayDay", todayDay);
        model.addAttribute("yesterdayDay", todayDay-1);
    }

    /**
     * Assign a question to a user
     *
     * @param model showing details
     * @param applicationId the application for which the user is assigned
     * @param sectionId section id for showing details
     * @param request request parameters
     * @return
     */
    @RequestMapping(value = "/{applicationId}/section/{sectionId}", method = RequestMethod.POST)
    public String assignQuestionToUser(Model model,
                                        @PathVariable("applicationId") final Long applicationId,
                                        @PathVariable("sectionId") final Long sectionId,
                                        HttpServletRequest request){

        // save application details if they are in the request
        Map<String, String[]> params = request.getParameterMap();
        if(params.containsKey("assign_question")) {
            assignQuestion(request, applicationId);
        }

        addApplicationDetails(applicationId, model);
        model.addAttribute("applicationSaved", true);

        return "application-details";
    }

    private void assignQuestion(HttpServletRequest request, Long applicationId) {
        String[] assign = request.getParameter("assign_question").split("_");
        Long questionId = Long.valueOf(assign[0]);
        Long assigneeId = Long.valueOf(assign[1]);

        User user = userAuthenticationService.getAuthenticatedUser(request);
        responseService.assignQuestion(applicationId, questionId, user.getId(), assigneeId);
    }
}
