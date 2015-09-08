package com.worth.ifs.application;

import com.worth.ifs.constant.ApplicationStatusConstants;
import com.worth.ifs.domain.*;
import com.worth.ifs.exception.ObjectNotFoundException;
import com.worth.ifs.application.helper.ApplicationHelper;
import com.worth.ifs.application.helper.SectionHelper;
import com.worth.ifs.security.TokenAuthenticationService;
import com.worth.ifs.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
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
    TokenAuthenticationService tokenAuthenticationService;

    /**
     * Get the details of the current application, add this to the model so we can use it in the templates.
     *
     * @param applicationId represents the application
     * @param model model that contains the details for the application detail page
     */
    private void addApplicationDetails(Long applicationId, Model model) {
        ApplicationHelper applicationHelper = new ApplicationHelper();
        Application application = applicationService.getApplicationById(applicationId);
        SectionHelper sectionHelper = new SectionHelper();

        if(application == null){
            throw new ObjectNotFoundException("Application not found.");
        }

        model.addAttribute("currentApplication", application);

        Competition competition = application.getCompetition();
        model.addAttribute("currentCompetition", competition);

        model.addAttribute("assignableUsers", userService.findAssignableUsers(application.getId()));


        model.addAttribute("applicationOrganisations", applicationHelper.getApplicationOrganisations(application));
        model.addAttribute("leadOrganisation", applicationHelper.getApplicationLeadOrganisation(application).orElseGet(() -> null ));

        List<Long> completedSections = sectionService.getCompletedSectionIds(applicationId);
        model.addAttribute("completedSections", completedSections);
        List<Long> incompletedSections = sectionService.getIncompletedSectionIds(applicationId);
        model.addAttribute("incompletedSections", incompletedSections);

        List<Response> responses = responseService.getResponsesByApplicationId(applicationId);
        HashMap<Long, Response> responseMap = new HashMap<>();
        for (Response response : responses) {
            responseMap.put(response.getQuestion().getId(), response);
        }
        model.addAttribute("responses", responseMap);

        Double completedQuestionsPercentage = applicationService.getCompleteQuestionsPercentage(application.getId());
        model.addAttribute("completedQuestionsPercentage", completedQuestionsPercentage.intValue());


        List<Section> sections = sectionHelper.getParentSections(competition.getSections());
        model.addAttribute("sections", sections);
    }

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
        this.addApplicationDetails(applicationId, model);
        model.addAttribute("currentSectionId", sectionId);
        return "application-details";
    }

    @RequestMapping("/{applicationId}/summary")
      public String applicationSummary(Model model, @PathVariable("applicationId") final Long applicationId){
        List<Response> responses = responseService.getResponsesByApplicationId(applicationId);
        HashMap<Long, Response> responseMap = new HashMap<>();
        for (Response response : responses) {
            responseMap.put(response.getQuestion().getId(), response);
        }
        model.addAttribute("responses", responseMap);

        this.addApplicationDetails(applicationId, model);
        return "application-summary";
    }
    @RequestMapping("/{applicationId}/confirm-submit")
    public String applicationConfirmSubmit(Model model, @PathVariable("applicationId") final Long applicationId){
        this.addApplicationDetails(applicationId, model);
        return "application-confirm-submit";
    }

    @RequestMapping("/{applicationId}/submit")
    public String applicationSubmit(Model model, @PathVariable("applicationId") final Long applicationId){
        applicationService.updateApplicationStatus(applicationId, ApplicationStatusConstants.SUBMITTED.getId());
        this.addApplicationDetails(applicationId, model);
        return "application-submitted";
    }



    /**
     * This method is for the post request when the users clicks the input[type=submit] button.
     * This is also used when the user clicks the 'mark-as-complete' button.
     */
    @RequestMapping(value = "/{applicationId}/section/{sectionId}", method = RequestMethod.POST)
    public String applicationFormSubmit(Model model,
                                        @PathVariable("applicationId") final Long applicationId,
                                        @PathVariable("sectionId") final Long sectionId,
                                        HttpServletRequest request){

        // save application details if they are in the request
        Map<String, String[]> params = request.getParameterMap();
        params.forEach((key, value) -> log.info("key "+ key));

        if(params.containsKey("assign_question")) {
            log.info("assign question now.");
            String[] assign = request.getParameter("assign_question").split("_");
            Long questionId = Long.valueOf(assign[0]);
            Long assigneeId = Long.valueOf(assign[1]);

            //gets the logged user details
            User user = (User)tokenAuthenticationService.getAuthentication(request).getDetails();
            //process chain starts...
            responseService.assignQuestion(applicationId, questionId, user.getId(), assigneeId);
        }

        //set all the application details
        addApplicationDetails(applicationId, model);
        //sets success feedback
        model.addAttribute("applicationSaved", true);

        return "application-details";
    }
}
