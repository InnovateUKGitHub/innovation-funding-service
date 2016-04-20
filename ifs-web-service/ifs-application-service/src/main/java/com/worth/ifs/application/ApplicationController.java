package com.worth.ifs.application;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.profiling.ProfileExecution;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * This controller will handle all requests that are related to the application overview.
 * Application overview is the page that contains the most basic information about the current application and
 * the basic information about the competition the application is related to.
 */

@Controller
@RequestMapping("/application")
public class ApplicationController extends AbstractApplicationController {
    private static final Log LOG = LogFactory.getLog(ApplicationController.class);

    public static String redirectToApplication(ApplicationResource application){
        return "redirect:/application/"+application.getId();
    }

    @ProfileExecution
    @RequestMapping(value= "/{applicationId}", method = RequestMethod.GET)
    public String applicationDetails(ApplicationForm form, Model model, @PathVariable("applicationId") final Long applicationId,
                                     HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSections(application, competition, user.getId(), Optional.empty(), Optional.empty(), model, form);
        return "application-details";
    }

    @ProfileExecution
    @RequestMapping(value= "/{applicationId}", method = RequestMethod.POST)
    public String applicationDetails(ApplicationForm form, Model model, @PathVariable("applicationId") final Long applicationId,
                                     HttpServletResponse response, HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        assignQuestion(request, applicationId);

        return "redirect:/application/"+applicationId;
    }

    @ProfileExecution
    @RequestMapping("/{applicationId}/section/{sectionId}")
    public String applicationDetailsOpenSection(ApplicationForm form, Model model,
                                     @PathVariable("applicationId") final Long applicationId,
                                     @PathVariable("sectionId") final Long sectionId,
                                                HttpServletRequest request){
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        SectionResource section = sectionService.getById(sectionId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        addApplicationAndSections(application, competition, user.getId(), Optional.ofNullable(section), Optional.empty(), model, form);
        addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, user, model, form);
        return "application-details";
    }

    @ProfileExecution
    @RequestMapping(value = "/{applicationId}/summary", method = RequestMethod.GET)
    public String applicationSummary(@ModelAttribute("form") ApplicationForm form, Model model, @PathVariable("applicationId") final Long applicationId,
                                     HttpServletRequest request) {
        List<FormInputResponseResource> responses = formInputResponseService.getByApplication(applicationId);
        model.addAttribute("incompletedSections", sectionService.getInCompleted(applicationId));
        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));

        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSections(application, competition, user.getId(), Optional.empty(), Optional.empty(), model, form);
        addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, user, model, form);
        model.addAttribute("applicationReadyForSubmit", applicationService.isApplicationReadyForSubmit(application.getId()));

        return "application-summary";
    }
    @ProfileExecution
    @RequestMapping(value = "/{applicationId}/summary", method = RequestMethod.POST)
    public String applicationSummarySubmit(@RequestParam(MARK_AS_COMPLETE) Long markQuestionCompleteId, @PathVariable("applicationId") final Long applicationId,
                                           HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        if(markQuestionCompleteId!=null) {
            questionService.markAsComplete(markQuestionCompleteId, applicationId, user.getId());
        }
        return "redirect:/application/" + applicationId + "/summary";
    }
    @ProfileExecution
    @RequestMapping("/{applicationId}/confirm-submit")
    public String applicationConfirmSubmit(ApplicationForm form, Model model, @PathVariable("applicationId") final Long applicationId,
                                           HttpServletRequest request){
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSections(application, competition, user.getId(), Optional.empty(), Optional.empty(), model, form);
        return "application-confirm-submit";
    }

    @RequestMapping(value = "/{applicationId}/submit", method = RequestMethod.POST)
    public String applicationSubmit(ApplicationForm form, Model model, @RequestParam(value = "agreeTerms", required = false) boolean agreeTerms, @PathVariable("applicationId") final Long applicationId,
                                    HttpServletRequest request, HttpServletResponse response){
    	if(!agreeTerms) {
    		cookieFlashMessageFilter.setFlashMessage(response, "agreeToTerms");
    		return "redirect:/application/" + applicationId + "/confirm-submit";
    	}
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        applicationService.updateStatus(applicationId, ApplicationStatusConstants.SUBMITTED.getId());
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSections(application, competition, user.getId(), Optional.empty(), Optional.empty(), model, form);
        return "application-submitted";
    }

    @ProfileExecution
    @RequestMapping("/{applicationId}/track")
    public String applicationTrack(ApplicationForm form, Model model, @PathVariable("applicationId") final Long applicationId,
                                    HttpServletRequest request){
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        addApplicationAndSections(application, competition, user.getId(), Optional.empty(), Optional.empty(), model, form);
        return "application-track";
    }
    @ProfileExecution
    @RequestMapping("/create/{competitionId}")
    public String applicationCreatePage(){
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
    public String competitionCreateApplication(){
        return "application-create-confirm-competition";
    }

    @RequestMapping(value = "/terms-and-conditions")
    public String termsAndConditions(){
        return "application-terms-and-conditions";
    }

    /**
     * This method is for the post request when the users clicks the input[type=submit] button.
     * This is also used when the user clicks the 'mark-as-complete' button or reassigns a question to another user.
     */
    @ProfileExecution
    @RequestMapping(value = "/{applicationId}/section/{sectionId}", params= {"singleFragment=true"}, method = RequestMethod.POST)
    public String assignQuestionAndReturnSectionFragmentIndividualSection(ApplicationForm form, Model model,
                                                         @PathVariable("applicationId") final Long applicationId,
                                                         @RequestParam("sectionId") final Optional<Long> sectionId,
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
                                                         @RequestParam("sectionId") final Optional<Long> sectionId,
                                                         HttpServletRequest request, HttpServletResponse response){

        return doAssignQuestionAndReturnSectionFragment(model, applicationId, sectionId, request, response, form);
    }

    private String doAssignQuestionAndReturnSectionFragment(Model model,
                                                            @PathVariable("applicationId") Long applicationId,
                                                            @RequestParam("sectionId") Optional<Long> sectionId,
                                                            HttpServletRequest request,
                                                            HttpServletResponse response,
                                                            ApplicationForm form) {
        doAssignQuestion(applicationId, request, response);

        ApplicationResource application = applicationService.getById(applicationId);
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);

        CompetitionResource  competition = competitionService.getById(application.getCompetition());

        Optional<SectionResource> currentSection = getSectionByIds(competition.getSections(), sectionId, false);

//        addApplicationAndSections(application, competition, user.getId(), Optional.empty(), Optional.empty(), model, form);
//        addOrganisationAndUserFinanceDetails(applicationId, user, model, form);

        Long questionId = extractQuestionProcessRoleIdFromAssignSubmit(request);
        Optional<Question> question = getQuestion(currentSection, questionId);

        super.addApplicationAndSections(application, competition, user.getId(), currentSection, question.map(Question::getId), model, form);
        super.addOrganisationAndUserFinanceDetails(competition.getId(), applicationId, user, model, form);

        model.addAttribute("currentUser", user);
        model.addAttribute("section", currentSection.get());

        Map<Long, List<Question>> sectionQuestions = new HashMap<>();
        if(questionId != null && question.isPresent()){
            sectionQuestions.put(currentSection.get().getId(), Arrays.asList(questionService.getById(questionId)));
        }else{
            sectionQuestions.put(currentSection.get().getId(), currentSection.get().getQuestions().stream().map(questionService::getById).collect(Collectors.toList()));
        }
        model.addAttribute("sectionQuestions", sectionQuestions);
        List<SectionResource> childSections = simpleMap(currentSection.get().getChildSections(), sectionService::getById);
        model.addAttribute("childSections", childSections);
        model.addAttribute("childSectionsSize", childSections.size());
        return "application/single-section-details";
    }

    private Optional<Question> getQuestion(Optional<SectionResource> currentSection, Long questionId) {
        return currentSection.get().getQuestions().stream()
                    .map(questionService::getById)
                    .filter(q -> q.getId().equals(questionId))
                    .findAny();
    }


    /**
     * Assign a question to a user
     *
     * @param applicationId the application for which the user is assigned
     * @param sectionId section id for showing details
     * @param request request parameters
     * @return
     */
    @ProfileExecution
    @RequestMapping(value = "/{applicationId}/section/{sectionId}", method = RequestMethod.POST)
    public String assignQuestion(@PathVariable("applicationId") final Long applicationId,
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

    /**
     * Printable version of the application
     */
    @RequestMapping(value="/{applicationId}/print")
    public String print(@PathVariable("applicationId") final Long applicationId,
            Model model, HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        List<FormInputResponseResource> responses = formInputResponseService.getByApplication(applicationId);
        model.addAttribute("responses", formInputResponseService.mapFormInputResponsesToFormInput(responses));
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);

        List<ProcessRole> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> userOrganisation = getUserOrganisation(user.getId(), userApplicationRoles);

        addOrganisationDetails(model, application, userOrganisation, userApplicationRoles);
        addQuestionsDetails(model, application, null);
        addUserDetails(model, application, user.getId());
        addApplicationInputs(application, model);
        addMappedSectionsDetails(model, application, competition, Optional.empty(), userOrganisation);
        financeOverviewModelManager.addFinanceDetails(model, competition.getId(), applicationId);

        return "/application/print";
    }
}
