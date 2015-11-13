package com.worth.ifs.application;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), Optional.empty(), model);
        return "application-details";
    }

    @RequestMapping("/{applicationId}/section/{sectionId}")
    public String applicationDetailsOpenSection(Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId,
                                                HttpServletRequest request){
        User user = userAuthenticationService.getAuthenticatedUser(request);
        addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), Optional.of(sectionId), model);
        return "application-details";
    }

    @RequestMapping("/{applicationId}/summary")
    public String applicationSummary(Model model, @PathVariable("applicationId") final Long applicationId,
                                     HttpServletRequest request){
        List<Response> responses = responseService.getByApplication(applicationId);

        // TODO DW 578 - responses to forminputresponses
        model.addAttribute("responses", responseService.mapResponsesToQuestion(responses));
        User user = userAuthenticationService.getAuthenticatedUser(request);

        addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), Optional.empty(), model);
        return "application-summary";
    }

    @RequestMapping("/{applicationId}/confirm-submit")
    public String applicationConfirmSubmit(Model model, @PathVariable("applicationId") final Long applicationId,
                                           HttpServletRequest request){
        User user = userAuthenticationService.getAuthenticatedUser(request);
        addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), Optional.empty(), model);
        return "application-confirm-submit";
    }

    @RequestMapping("/{applicationId}/submit")
    public String applicationSubmit(Model model, @PathVariable("applicationId") final Long applicationId,
                                    HttpServletRequest request){
        User user = userAuthenticationService.getAuthenticatedUser(request);
        applicationService.updateStatus(applicationId, ApplicationStatusConstants.SUBMITTED.getId());
        addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), Optional.empty(), model);
        return "application-submitted";
    }

    @RequestMapping("/{applicationId}/track")
    public String applicationTrack(Model model, @PathVariable("applicationId") final Long applicationId,
                                    HttpServletRequest request){
        User user = userAuthenticationService.getAuthenticatedUser(request);
        addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), Optional.empty(), model);
        return "application-track";
    }

    /**
     * This method is for the post request when the users clicks the input[type=submit] button.
     * This is also used when the user clicks the 'mark-as-complete' button or reassigns a question to another user.
     */
    @RequestMapping(value = "/{applicationId}/section/{sectionId}", params= {"singleFragment=true"}, method = RequestMethod.POST)
    public String assignQuestionAndReturnSectionFragment(Model model,
                                                         @PathVariable("applicationId") final Long applicationId,
                                                         @RequestParam("sectionId") final Long sectionId,
                                                         HttpServletRequest request, HttpServletResponse response){

        doAssignQuestion(applicationId, request, response);

        // (* question, * questionAssignee, * questionAssignees, * responses, * currentUser, * userIsLeadApplicant, * section, * currentApplication)

        Application application = applicationService.getById(applicationId);
        User user = userAuthenticationService.getAuthenticatedUser(request);
        addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), Optional.of(sectionId), model);

        Long questionId = extractQuestionProcessRoleIdFromAssignSubmit(request);

        Optional<Section> currentSection = getSection(application.getCompetition().getSections(), Optional.of(sectionId), true);
        Question question = currentSection.get().getQuestions().stream().filter(q -> q.getId().equals(questionId)).collect(Collectors.toList()).get(0);

        model.addAttribute("question", question);

        Organisation userOrganisation = organisationService.getUserOrganisation(application, user.getId()).get();

        List<Question> questions = questionService.findByCompetition(application.getCompetition().getId());

        HashMap<Long, QuestionStatus> questionAssignees = questionService.mapAssigneeToQuestion(questions, userOrganisation.getId());
        QuestionStatus questionAssignee = questionAssignees.get(questionId);
        model.addAttribute("questionAssignee", questionAssignee);

        model.addAttribute("currentUser", user);
        model.addAttribute("section", currentSection);

        return "application/single-section-details";
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
                                 HttpServletRequest request,
                                 HttpServletResponse response){

        doAssignQuestion(applicationId, request, response);

        return "redirect:/application/" + applicationId + "/section/" +sectionId;
    }

    private void doAssignQuestion(@PathVariable("applicationId") Long applicationId, HttpServletRequest request, HttpServletResponse response) {
        assignQuestion(request, applicationId);
        cookieFlashMessageFilter.setFlashMessage(response, "assignedQuestion");
    }

    private Application addApplicationAndSectionsAndFinanceDetails(Long applicationId, Long userId, Optional<Long> currentSectionId, Model model) {
        Application application = super.addApplicationDetails(applicationId, userId, currentSectionId, model, false);
        model.addAttribute("incompletedSections", sectionService.getInCompleted(applicationId));
        model.addAttribute("completedQuestionsPercentage", applicationService.getCompleteQuestionsPercentage(application.getId()));
        addOrganisationFinanceDetails(model, application, userId);
        addFinanceDetails(model, application);
        return application;
    }
}
