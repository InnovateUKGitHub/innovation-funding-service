package com.worth.ifs.controller;

import com.worth.ifs.constant.ApplicationStatusConstants;
import com.worth.ifs.domain.*;
import com.worth.ifs.exception.ObjectNotFoundException;
import com.worth.ifs.helper.ApplicationHelper;
import com.worth.ifs.helper.SectionHelper;
import com.worth.ifs.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.security.x509.OIDMap;

import java.util.*;
import java.util.stream.Collectors;

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
    OrganisationService organisationService;

    @Autowired
    SectionService sectionService;

    @Autowired
    UserService userService;

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

        model.addAttribute("applicationOrganisations", applicationHelper.getApplicationOrganisations(application));
        model.addAttribute("leadOrganisation", applicationHelper.getApplicationLeadOrganisation(application).orElseGet(() ->  null ));

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

}
