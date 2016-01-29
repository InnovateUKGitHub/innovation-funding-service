package com.worth.ifs.application;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.profiling.ProfileExecution;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    private boolean selectFirstSectionIfNoneCurrentlySelected = false;


    public static String redirectToApplication(ApplicationResource application){
        return "redirect:/application/"+application.getId();
    }

    @ProfileExecution
    @RequestMapping("/{applicationId}")
    public String applicationDetails(ApplicationForm form, Model model, @PathVariable("applicationId") final Long applicationId,
                                     HttpServletRequest request){
        User user = userAuthenticationService.getAuthenticatedUser(request);
        addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), Optional.empty(), Optional.empty(), model, form, selectFirstSectionIfNoneCurrentlySelected);
        return "application-details";
    }

    @ProfileExecution
    @RequestMapping("/{applicationId}/section/{sectionId}")
    public String applicationDetailsOpenSection(ApplicationForm form, Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId,
                                                HttpServletRequest request){
        User user = userAuthenticationService.getAuthenticatedUser(request);
        Section section = sectionService.getById(sectionId);
        addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), Optional.ofNullable(section), Optional.empty(), model, form, selectFirstSectionIfNoneCurrentlySelected);
        return "application-details";
    }

    @ProfileExecution
    @RequestMapping(value = "/{applicationId}/summary", method = RequestMethod.GET)
    public String applicationSummary(@ModelAttribute("form") ApplicationForm form, Model model, @PathVariable("applicationId") final Long applicationId,
                                     HttpServletRequest request) {
        List<FormInputResponse> responses = formInputResponseService.getByApplication(applicationId);
        model.addAttribute("incompletedSections", sectionService.getInCompleted(applicationId));
        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));
        User user = userAuthenticationService.getAuthenticatedUser(request);

        addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), Optional.empty(), Optional.empty() , model, form, selectFirstSectionIfNoneCurrentlySelected);
        
        return "application-summary";
    }
    @ProfileExecution
    @RequestMapping(value = "/{applicationId}/summary", method = RequestMethod.POST)
    public String applicationSummarySubmit(@RequestParam("mark_as_complete") Long markQuestionCompleteId, Model model, @PathVariable("applicationId") final Long applicationId,
                                     HttpServletRequest request) {
        User user = userAuthenticationService.getAuthenticatedUser(request);
        if(markQuestionCompleteId!=null) {
            questionService.markAsComplete(markQuestionCompleteId, applicationId, user.getId());
        }
        return "redirect:/application/" + applicationId + "/summary";
    }
    @ProfileExecution
    @RequestMapping("/{applicationId}/confirm-submit")
    public String applicationConfirmSubmit(ApplicationForm form, Model model, @PathVariable("applicationId") final Long applicationId,
                                           HttpServletRequest request){
        User user = userAuthenticationService.getAuthenticatedUser(request);
        addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), Optional.empty(), Optional.empty(), model, form, selectFirstSectionIfNoneCurrentlySelected);
        return "application-confirm-submit";
    }

    @RequestMapping("/{applicationId}/submit")
    public String applicationSubmit(ApplicationForm form, Model model, @PathVariable("applicationId") final Long applicationId,
                                    HttpServletRequest request){
        User user = userAuthenticationService.getAuthenticatedUser(request);
        applicationService.updateStatus(applicationId, ApplicationStatusConstants.SUBMITTED.getId());
        addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), Optional.empty(), Optional.empty(), model, form, selectFirstSectionIfNoneCurrentlySelected);
        return "application-submitted";
    }

    @ProfileExecution
    @RequestMapping("/{applicationId}/track")
    public String applicationTrack(ApplicationForm form, Model model, @PathVariable("applicationId") final Long applicationId,
                                    HttpServletRequest request){
        User user = userAuthenticationService.getAuthenticatedUser(request);
        addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), Optional.empty(), Optional.empty(), model, form, selectFirstSectionIfNoneCurrentlySelected);
        return "application-track";
    }

    @ProfileExecution
    @RequestMapping("/create/{competitionId}")
    public String applicationCreatePage(Model model, @PathVariable("competitionId") final Long competitionId, HttpServletRequest request){
        return "application-create";
    }

    @ProfileExecution
    @RequestMapping(value = "/create/{competitionId}", method = RequestMethod.POST)
    public String applicationCreate(Model model,
                                    @PathVariable("competitionId") final Long competitionId,
                                    @RequestParam(value = "application_name", required = true) String applicationName,
                                    HttpServletRequest request){
        Long userId = userAuthenticationService.getAuthenticatedUser(request).getId();

        String applicationNameWithoutWhiteSpace= applicationName.replaceAll("\\s","");

        if(applicationNameWithoutWhiteSpace.length() > 0) {
            ApplicationResource application = applicationService.createApplication(competitionId, userId, applicationName);
            return "redirect:/application/"+application.getId();
        }
        else {
            model.addAttribute("applicationNameEmpty", true);
            return "application-create";
        }
    }

    @ProfileExecution
    @RequestMapping(value = "/create-confirm-competition")
    public String competitionCreateApplication(Model model, HttpServletRequest request){
        return "application-create-confirm-competition";
    }



    /**
     * This method is for the post request when the users clicks the input[type=submit] button.
     * This is also used when the user clicks the 'mark-as-complete' button or reassigns a question to another user.
     */
    @ProfileExecution
    @RequestMapping(value = "/{applicationId}/section/{sectionId}", params= {"singleFragment=true"}, method = RequestMethod.POST)
    public String assignQuestionAndReturnSectionFragmentIndividualSection(ApplicationForm form, Model model,
                                                         @PathVariable("applicationId") final Long applicationId,
                                                         @RequestParam("sectionId") final Long sectionId,
                                                         HttpServletRequest request, HttpServletResponse response){

        return doAssignQuestionAndReturnSectionFragment(model, applicationId, sectionId, request, response, form);
    }

    /**
     * This method is for the post request when the users clicks the input[type=submit] button.
     * This is also used when the user clicks the 'mark-as-complete' button or reassigns a question to another user.
     */
    @ProfileExecution
    @RequestMapping(value = "/{applicationId}", params = {"singleFragment=true"}, method = RequestMethod.POST)
    public String assignQuestionAndReturnSectionFragment(ApplicationForm form, Model model,
                                                         @PathVariable("applicationId") final Long applicationId,
                                                         @RequestParam("sectionId") final Long sectionId,
                                                         HttpServletRequest request, HttpServletResponse response){

        return doAssignQuestionAndReturnSectionFragment(model, applicationId, sectionId, request, response, form);
    }

    private String doAssignQuestionAndReturnSectionFragment(Model model, @PathVariable("applicationId") Long applicationId, @RequestParam("sectionId") Long sectionId, HttpServletRequest request, HttpServletResponse response, ApplicationForm form) {
        doAssignQuestion(applicationId, request, response);

        ApplicationResource application = applicationService.getById(applicationId);
        User user = userAuthenticationService.getAuthenticatedUser(request);
        Competition competition = competitionService.getById(application.getCompetition());
        Optional<Section> currentSection = getSection(competition.getSections(), Optional.of(sectionId), true);
        super.addApplicationAndSectionsAndFinanceDetails(applicationId, user.getId(), currentSection, Optional.empty(), model, form, selectFirstSectionIfNoneCurrentlySelected);

        Long questionId = extractQuestionProcessRoleIdFromAssignSubmit(request);

        Question question = currentSection.get().getQuestions().stream().filter(q -> q.getId().equals(questionId)).collect(Collectors.toList()).get(0);

        model.addAttribute("question", question);

        Organisation userOrganisation = organisationService.getUserOrganisation(application, user.getId()).get();

        List<Question> questions = questionService.findByCompetition(application.getCompetition());

        HashMap<Long, QuestionStatus> questionAssignees = questionService.mapAssigneeToQuestionByApplicationId(questions, userOrganisation.getId(), applicationId);
        QuestionStatus questionAssignee = questionAssignees.get(questionId);
        model.addAttribute("questionAssignee", questionAssignee);

        model.addAttribute("currentUser", user);
        model.addAttribute("section", currentSection.get());

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
    @ProfileExecution
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


}
