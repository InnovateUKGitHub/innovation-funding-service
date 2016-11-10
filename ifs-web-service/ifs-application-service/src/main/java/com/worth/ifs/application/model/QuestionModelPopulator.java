package com.worth.ifs.application.model;

import java.util.*;
import java.util.stream.Collectors;

import com.worth.ifs.application.ApplicationFormController;
import com.worth.ifs.application.UserApplicationRole;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.application.service.QuestionStatusRestService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionStatus;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.form.service.FormInputResponseService;
import com.worth.ifs.form.service.FormInputService;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.ProcessRoleService;
import com.worth.ifs.user.service.UserService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import static com.worth.ifs.application.ApplicationFormController.*;

/**
 * View model for the single question pages
 */
@Component
public class QuestionModelPopulator {
    private static final Log LOG = LogFactory.getLog(QuestionModelPopulator.class);
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private ProcessRoleService processRoleService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private InviteRestService inviteRestService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private FormInputService formInputService;
    @Autowired
    private FormInputResponseService formInputResponseService;

    public void populateModel(final Long questionId, final Long applicationId, final UserResource user, final Model model, final ApplicationForm form) {
        QuestionResource question = questionService.getById(questionId);
        List<FormInputResource> formInputs = formInputService.findApplicationInputsByQuestion(questionId);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());

        addFormAttributes(application, competition, user, model, form,
            question, formInputs, userApplicationRoles);
    }

    private void addFormAttributes(ApplicationResource application,
        CompetitionResource competition,
        UserResource user, Model model,
        ApplicationForm form, QuestionResource question,
        List<FormInputResource> formInputs,
        List<ProcessRoleResource> userApplicationRoles){

        addApplicationDetails(application, competition, user.getId(), question, model, form, userApplicationRoles);
        addNavigation(question, application.getId(), model);

        Map<Long, List<FormInputResource>> questionFormInputs = new HashMap<>();
        questionFormInputs.put(question.getId(), formInputs);

        model.addAttribute("currentQuestion", question);
        model.addAttribute("questionFormInputs", questionFormInputs);
        model.addAttribute("currentUser", user);
        model.addAttribute("form", form);
        model.addAttribute("title", question.getShortName());
    }

    private void addApplicationDetails(ApplicationResource application,
        CompetitionResource competition,
        Long userId,
        QuestionResource questionResource,
        Model model,
        ApplicationForm form,
        List<ProcessRoleResource> userApplicationRoles) {

        if(form == null){
            form = new ApplicationForm();
        }
        form.setApplication(application);
        OrganisationResource userOrganisation = getUserOrganisation(userId, userApplicationRoles);

        addQuestionsDetails(model, application, form);
        addUserDetails(model, application, userId);
        addApplicationFormDetailInputs(application, form);
        addAssignableDetails(model, application, userOrganisation, userId, questionResource.getId());

        List<QuestionStatusResource> questionStatuses = getQuestionStatuses(questionResource.getId(), application.getId());
        Set<Long> completedDetails = getCompletedDetails(questionResource, application.getId(), questionStatuses);

        model.addAttribute("markedAsComplete", completedDetails);
        model.addAttribute("allReadOnly", calculateAllReadOnly(competition, questionResource, questionStatuses, userId, completedDetails));

        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);
        leadOrganisation.ifPresent(org ->
            model.addAttribute("leadOrganisation", org)
        );

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);
        model.addAttribute("userOrganisation", userOrganisation);
    }

    private Boolean calculateAllReadOnly(CompetitionResource competition, QuestionResource questionResource, List<QuestionStatusResource> questionStatuses, Long userId, Set<Long> completedDetails) {
        if(competition.getCompetitionStatus().equals(CompetitionStatus.OPEN)) {
            Set<Long> assignedQuestions = getAssigneeQuestions(questionResource, questionStatuses, userId);
            return questionStatuses.size() > 0 &&
                    (completedDetails.contains(questionResource.getId()) || !assignedQuestions.contains(questionResource.getId()));
        } else {
            return true;
        }
    }

    private void addQuestionsDetails(Model model, ApplicationResource application, Form form) {
        List<FormInputResponseResource> responses = getFormInputResponses(application);
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);
        model.addAttribute("responses",mappedResponses);
        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((k, v) ->
            values.put(k.toString(), v.getValue())
        );
        form.setFormInput(values);
        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);
    }

    private void addUserDetails(Model model, ApplicationResource application, Long userId) {
        Boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());

        model.addAttribute("userIsLeadApplicant", userIsLeadApplicant);
        model.addAttribute("leadApplicant", leadApplicant);
    }

    private void addAssignableDetails(Model model, ApplicationResource application, OrganisationResource userOrganisation,
        Long userId, Long currentQuestionId) {

        if (isApplicationInViewMode(model, application, userOrganisation))
            return;

        Map<Long, QuestionStatusResource> questionAssignees;
        QuestionStatusResource questionStatusResource = questionService.getByQuestionIdAndApplicationIdAndOrganisationId(currentQuestionId, application.getId(), userOrganisation.getId());
        questionAssignees = new HashMap<>();
        if(questionStatusResource != null) {
            questionAssignees.put(currentQuestionId, questionStatusResource);
        }
        QuestionStatusResource questionAssignee = questionAssignees.get(currentQuestionId);

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        questionService.removeNotifications(notifications);

        List<ApplicationInviteResource> pendingAssignableUsers = pendingInvitations(application);

        model.addAttribute("questionAssignee", questionAssignee);
        model.addAttribute("assignableUsers", processRoleService.findAssignableProcessRoles(application.getId()));
        model.addAttribute("pendingAssignableUsers", pendingAssignableUsers);
        model.addAttribute("questionAssignees", questionAssignees);
        model.addAttribute("notifications", notifications);
    }

    private boolean isApplicationInViewMode(Model model, ApplicationResource application, OrganisationResource userOrganisation) {
        if(!application.isOpen() || userOrganisation == null){
            //Application Not open, so add empty lists
            model.addAttribute("assignableUsers", new ArrayList<ProcessRoleResource>());
            model.addAttribute("pendingAssignableUsers", new ArrayList<ApplicationInviteResource>());
            model.addAttribute("questionAssignees", new HashMap<Long, QuestionStatusResource>());
            model.addAttribute("notifications", new ArrayList<QuestionStatusResource>());
            return true;
        }
        return false;
    }

    private OrganisationResource getUserOrganisation(Long userId, List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
            .filter(uar -> uar.getUser().equals(userId))
            .map(uar -> organisationService.getOrganisationById(uar.getOrganisation()))
            .findFirst().get();
    }

    private  void addApplicationFormDetailInputs(ApplicationResource application, Form form) {
        Map<String, String> formInputs = form.getFormInput();
        formInputs.put("application_details-title", application.getName());
        formInputs.put("application_details-duration", String.valueOf(application.getDurationInMonths()));
        if(application.getStartDate() == null){
            formInputs.put("application_details-startdate_day", "");
            formInputs.put("application_details-startdate_month", "");
            formInputs.put("application_details-startdate_year", "");
        }else{
            formInputs.put("application_details-startdate_day", String.valueOf(application.getStartDate().getDayOfMonth()));
            formInputs.put("application_details-startdate_month", String.valueOf(application.getStartDate().getMonthValue()));
            formInputs.put("application_details-startdate_year", String.valueOf(application.getStartDate().getYear()));
        }
        form.setFormInput(formInputs);
    }

    private void addNavigation(QuestionResource question, Long applicationId, Model model) {
        if (question == null) {
            return;
        }

        Optional<QuestionResource> previousQuestion = questionService.getPreviousQuestion(question.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, model);
        Optional<QuestionResource> nextQuestion = questionService.getNextQuestion(question.getId());
        addNextQuestionToModel(nextQuestion, applicationId, model);
    }

    private void addPreviousQuestionToModel(Optional<QuestionResource> previousQuestionOptional, Long applicationId, Model model) {
        String previousUrl;
        String previousText;

        if (previousQuestionOptional.isPresent()) {
            QuestionResource previousQuestion = previousQuestionOptional.get();
            SectionResource previousSection = sectionService.getSectionByQuestionId(previousQuestion.getId());
            if (previousSection.isQuestionGroup()) {
                previousUrl = APPLICATION_BASE_URL + applicationId + "/form" + SECTION_URL + previousSection.getId();
                previousText = previousSection.getName();
            } else {
                previousUrl = APPLICATION_BASE_URL + applicationId + "/form" + QUESTION_URL + previousQuestion.getId();
                previousText = previousQuestion.getShortName();
            }
            model.addAttribute("previousUrl", previousUrl);
            model.addAttribute("previousText", previousText);
        }
    }

    private void addNextQuestionToModel(Optional<QuestionResource> nextQuestionOptional, Long applicationId, Model model) {
        String nextUrl;
        String nextText;

        if (nextQuestionOptional.isPresent()) {
            QuestionResource nextQuestion = nextQuestionOptional.get();
            SectionResource nextSection = sectionService.getSectionByQuestionId(nextQuestion.getId());

            if (nextSection.isQuestionGroup()) {
                nextUrl = APPLICATION_BASE_URL + applicationId + "/form" + SECTION_URL + nextSection.getId();
                nextText = nextSection.getName();
            } else {
                nextUrl = APPLICATION_BASE_URL + applicationId + "/form" + QUESTION_URL + nextQuestion.getId();
                nextText = nextQuestion.getShortName();
            }

            model.addAttribute("nextUrl", nextUrl);
            model.addAttribute("nextText", nextText);
        }
    }

    private List<ApplicationInviteResource> pendingInvitations(ApplicationResource application) {
        RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(application.getId());

        return pendingAssignableUsersResult.handleSuccessOrFailure(
            failure -> new ArrayList<>(0),
            success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                .filter(item -> !InviteStatus.OPENED.equals(item.getStatus()))
                .collect(Collectors.toList()));
    }

    private List<FormInputResponseResource> getFormInputResponses(ApplicationResource application) {
        return formInputResponseService.getByApplication(application.getId());
    }

    private List<QuestionStatusResource> getQuestionStatuses(Long questionId, Long applicationId) {
        return questionService.findQuestionStatusesByQuestionAndApplicationId(questionId, applicationId);
    }


    private Set<Long> getAssigneeQuestions(QuestionResource question, List<QuestionStatusResource> questionStatuses, Long userId) {
        Set<Long> assigned = new HashSet<Long>();

        if(!question.getMultipleStatuses()) {
            if(questionStatuses
                    .stream()
                    .anyMatch(qs ->
                            (qs.getAssigneeUserId() == userId || qs.getAssignee() == null))) {
                assigned.add(question.getId());
            }
        }

        return assigned;
    }

    private Set<Long> getCompletedDetails(QuestionResource question, Long applicationId, List<QuestionStatusResource> questionStatuses) {
        Set<Long> markedAsComplete = new HashSet<Long>();

        if(question.getMarkAsCompletedEnabled() && !question.getMultipleStatuses()) {
            if(questionStatuses
                    .stream()
                    .anyMatch(qs ->
                            (isMarkedAsCompleteForSingleStatus(qs).orElse(Boolean.FALSE)))) {
                markedAsComplete.add(question.getId());
            }
        }

        return markedAsComplete;
    }

    private Optional<Boolean> isMarkedAsCompleteForSingleStatus(QuestionStatusResource questionStatus) {
        Boolean markedAsComplete = null;
        if (questionStatus != null) {
            markedAsComplete = questionStatus.getMarkedAsComplete();
        }
        return Optional.ofNullable(markedAsComplete);
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
            .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
            .map(uar -> organisationService.getOrganisationById(uar.getOrganisation()))
            .findFirst();
    }
}
