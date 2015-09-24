package com.worth.ifs.application;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.exception.ObjectNotFoundException;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class ApplicationController extends AbstractApplicationController {
    private final Log log = LogFactory.getLog(getClass());

    @RequestMapping("/{applicationId}")
    public String applicationDetails(Model model, @PathVariable("applicationId") final Long applicationId,
                                     HttpServletRequest request){
        log.info("Application with id " + applicationId);
        User user = userAuthenticationService.getAuthenticatedUser(request);
        this.addApplicationDetails(applicationId, user.getId(), model);
        return "application-details";
    }

    @RequestMapping("/{applicationId}/section/{sectionId}")
    public String applicationDetailsOpenSection(Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId,
                                                HttpServletRequest request){
        User user = userAuthenticationService.getAuthenticatedUser(request);
        addApplicationDetails(applicationId, user.getId(), model);
        model.addAttribute("currentSectionId", sectionId);
        return "application-details";
    }

    @RequestMapping("/{applicationId}/summary")
    public String applicationSummary(Model model, @PathVariable("applicationId") final Long applicationId,
                                     HttpServletRequest request){
        List<Response> responses = responseService.getByApplication(applicationId);
        model.addAttribute("responses", responseService.mapResponsesToQuestion(responses));
        User user = userAuthenticationService.getAuthenticatedUser(request);

        addApplicationDetails(applicationId, user.getId(), model);
        Application application = applicationService.getById(applicationId);
        addFinanceDetails(model, application, user.getId());
        return "application-summary";
    }

    @RequestMapping("/{applicationId}/confirm-submit")
    public String applicationConfirmSubmit(Model model, @PathVariable("applicationId") final Long applicationId,
                                           HttpServletRequest request){
        User user = userAuthenticationService.getAuthenticatedUser(request);
        addApplicationDetails(applicationId, user.getId(), model);
        return "application-confirm-submit";
    }

    @RequestMapping("/{applicationId}/submit")
    public String applicationSubmit(Model model, @PathVariable("applicationId") final Long applicationId,
                                    HttpServletRequest request){
        User user = userAuthenticationService.getAuthenticatedUser(request);
        applicationService.updateStatus(applicationId, ApplicationStatusConstants.SUBMITTED.getId());
        addApplicationDetails(applicationId, user.getId(), model);
        return "application-submitted";
    }

    /**
     * Get the details of the current application, add this to the model so we can use it in the templates.
     *
     * @param applicationId represents the application
     * @param model model that contains the details for the application detail page
     */
    private void addApplicationDetails(Long applicationId, Long userId, Model model) {
        Application application = applicationService.getById(applicationId);

        if(application == null){
            throw new ObjectNotFoundException("Application not found.");
        }

        Competition competition = application.getCompetition();
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);
        model.addAttribute("incompletedSections", sectionService.getInCompleted(applicationId));
        model.addAttribute("completedQuestionsPercentage", applicationService.getCompleteQuestionsPercentage(application.getId()));

        Organisation userOrganisation = organisationService.getUserOrganisation(application, userId).get();
        addOrganisationDetails(model, application, userOrganisation);
        addQuestionsDetails(model, application, userOrganisation.getId(), userId);
        addSectionsDetails(model, application, userOrganisation.getId(), userOrganisation.getId());
        addDateDetails(model);
        addUserDetails(model, application, userId);
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
    public String assignQuestion(Model model,
                                @PathVariable("applicationId") final Long applicationId,
                                @PathVariable("sectionId") final Long sectionId,
                                 HttpServletRequest request){
        assignQuestion(request, applicationId);
        return "redirect:/application/" + applicationId + "/section/" +sectionId;
    }
}
