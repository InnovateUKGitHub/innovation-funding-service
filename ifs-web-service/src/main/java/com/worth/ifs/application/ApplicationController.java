package com.worth.ifs.application;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.exception.ObjectNotFoundException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
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

    @RequestMapping("/{applicationId}/track")
    public String applicationTrack(Model model, @PathVariable("applicationId") final Long applicationId,
                                    HttpServletRequest request){
        User user = userAuthenticationService.getAuthenticatedUser(request);
        addApplicationDetails(applicationId, user.getId(), model);
        return "application-track";
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
        addUserDetails(model, application, userId);
    }

    /**
     * This method is for the post request when the users clicks the input[type=submit] button.
     * This is also used when the user clicks the 'mark-as-complete' button or reassigns a question to another user.
     */
    @RequestMapping(value = "/{applicationId}/section/{sectionId}", params= {"singleFragment=true"}, method = RequestMethod.POST)
    public String assignQuestionAndReturnSectionFragment(Model model,
                                                         @PathVariable("applicationId") final Long applicationId,
                                                         @RequestParam("sectionId") final Long sectionId,
                                                         HttpServletRequest request, RedirectAttributes redirectAttributes){

        doAssignQuestion(applicationId, request, redirectAttributes);

        // (* question, * questionAssignee, * questionAssignees, * responses, * currentUser, * userIsLeadApplicant, * section, * currentApplication)

        Application application = applicationService.getById(applicationId);
        User user = userAuthenticationService.getAuthenticatedUser(request);
        this.addApplicationDetails(applicationId, user.getId(), model);

        // TODO DW - quite a lot of rework here to get a hold of more single-section focused
        // TODO DW - sectionId should be determined from the clicked button as per the questionId as grabbed below
        Long questionId = extractQuestionProcessRoleIdFromAssignSubmit(request);

        Section currentSection = getSection(application.getCompetition().getSections(), sectionId);
        Question question = currentSection.getQuestions().stream().filter(q -> q.getId().equals(questionId)).collect(Collectors.toList()).get(0);

        model.addAttribute("question", question);

        Organisation userOrganisation = organisationService.getUserOrganisation(application, user.getId()).get();

//        List<Response> responses = responseService.getByApplication(applicationId);
//        Response response = responseService.mapResponsesToQuestion(responses).get(questionId);
        List<Question> questions = questionService.findByCompetition(application.getCompetition().getId());

        HashMap<Long, QuestionStatus> questionAssignees = questionService.mapAssigneeToQuestion(questions, userOrganisation.getId());
        QuestionStatus questionAssignee = questionAssignees.get(questionId);
        model.addAttribute("questionAssignee", questionAssignee);

        // TODO DW - move into "addUserDetails"?
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
                                 RedirectAttributes redirectAttributes){

        doAssignQuestion(applicationId, request, redirectAttributes);
        return "redirect:/application/" + applicationId + "/section/" +sectionId;
    }

    private void doAssignQuestion(@PathVariable("applicationId") Long applicationId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        assignQuestion(request, applicationId);
        redirectAttributes.addFlashAttribute("assignedQuestion", true);
    }



}
