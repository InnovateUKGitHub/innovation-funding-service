package com.worth.ifs.application.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.worth.ifs.ViewModel;
import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static com.worth.ifs.application.AbstractApplicationController.APPLICATION_BASE_URL;
import static com.worth.ifs.application.AbstractApplicationController.FORM_MODEL_ATTRIBUTE;
import static com.worth.ifs.application.AbstractApplicationController.QUESTION_URL;
import static com.worth.ifs.application.AbstractApplicationController.SECTION_URL;
import static java.util.Collections.singletonList;

/**
 * View model for the single question pages
 */
public class QuestionModel implements ViewModel {
    private static final Log LOG = LogFactory.getLog(QuestionModel.class);

    private final Services s;
    private final Model model;
    private final ApplicationForm form;

    public QuestionModel(final Long questionId, final Long applicationId, final UserResource user, final Services s, final Model model, final ApplicationForm form, final BindingResult bindingResult){
        this.s = s;
        this.model = model;
        this.form = form;
        QuestionResource question = s.getQuestionService().getById(questionId);
        List<FormInputResource> formInputs = s.getFormInputService().findByQuestion(questionId);
        SectionResource section = s.getSectionService().getSectionByQuestionId(questionId);
        ApplicationResource application = s.getApplicationService().getById(applicationId);
        CompetitionResource competition = s.getCompetitionService().getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = s.getProcessRoleService().findProcessRolesByApplicationId(application.getId());

        addFormAttributes(application, competition, section, user, model, form,
            question, formInputs, userApplicationRoles);
        form.setBindingResult(bindingResult);
        form.setObjectErrors(bindingResult.getAllErrors());
    }
    
    @Override public Model getModel() {
        return model;
    }

    private void addFormAttributes(ApplicationResource application,
        CompetitionResource competition,
        SectionResource section,
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
        addCompletedDetails(model, questionResource, application.getId());

        model.addAttribute(FORM_MODEL_ATTRIBUTE, form);
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);
        model.addAttribute("userOrganisation", userOrganisation);
    }

    private void addQuestionsDetails(Model model, ApplicationResource application, Form form) {
        List<FormInputResponseResource> responses = getFormInputResponses(application);
        Map<Long, FormInputResponseResource> mappedResponses = s.getFormInputResponseService().mapFormInputResponsesToFormInput(responses);
        model.addAttribute("responses",mappedResponses);
        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((k, v) ->
            values.put(k.toString(), v.getValue())
        );
        form.setFormInput(values);
        model.addAttribute(FORM_MODEL_ATTRIBUTE, form);
    }

    private void addUserDetails(Model model, ApplicationResource application, Long userId) {
        Boolean userIsLeadApplicant = s.getUserService().isLeadApplicant(userId, application);
        ProcessRoleResource leadApplicantProcessRole = s.getUserService().getLeadApplicantProcessRoleOrNull(application);
        UserResource leadApplicant = s.getUserService().findById(leadApplicantProcessRole.getUser());

        model.addAttribute("userIsLeadApplicant", userIsLeadApplicant);
        model.addAttribute("leadApplicant", leadApplicant);
    }

    private void addAssignableDetails(Model model, ApplicationResource application, OrganisationResource userOrganisation,
        Long userId, Long currentQuestionId) {

        if (isApplicationInViewMode(model, application, userOrganisation))
            return;

        Map<Long, QuestionStatusResource> questionAssignees;
        QuestionStatusResource questionStatusResource = s.getQuestionService().getByQuestionIdAndApplicationIdAndOrganisationId(currentQuestionId, application.getId(), userOrganisation.getId());
        questionAssignees = new HashMap<>();
        if(questionStatusResource != null) {
            questionAssignees.put(currentQuestionId, questionStatusResource);
        }
        QuestionStatusResource questionAssignee = questionAssignees.get(currentQuestionId);

        List<QuestionStatusResource> notifications = s.getQuestionService().getNotificationsForUser(questionAssignees.values(), userId);
        s.getQuestionService().removeNotifications(notifications);

        List<InviteResource> pendingAssignableUsers = pendingInvitations(application);

        model.addAttribute("questionAssignee", questionAssignee);
        model.addAttribute("assignableUsers", s.getProcessRoleService().findAssignableProcessRoles(application.getId()));
        model.addAttribute("pendingAssignableUsers", pendingAssignableUsers);
        model.addAttribute("questionAssignees", questionAssignees);
        model.addAttribute("notifications", notifications);
    }

    private boolean isApplicationInViewMode(Model model, ApplicationResource application, OrganisationResource userOrganisation) {
        if(!application.isOpen() || userOrganisation == null){
            //Application Not open, so add empty lists
            model.addAttribute("assignableUsers", new ArrayList<ProcessRoleResource>());
            model.addAttribute("pendingAssignableUsers", new ArrayList<InviteResource>());
            model.addAttribute("questionAssignees", new HashMap<Long, QuestionStatusResource>());
            model.addAttribute("notifications", new ArrayList<QuestionStatusResource>());
            return true;
        }
        return false;
    }

    private OrganisationResource getUserOrganisation(Long userId, List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
            .filter(uar -> uar.getUser().equals(userId))
            .map(uar -> s.getOrganisationService().getOrganisationById(uar.getOrganisation()))
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

        Optional<QuestionResource> previousQuestion = s.getQuestionService().getPreviousQuestion(question.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, model);
        Optional<QuestionResource> nextQuestion = s.getQuestionService().getNextQuestion(question.getId());
        addNextQuestionToModel(nextQuestion, applicationId, model);
    }

    private void addPreviousQuestionToModel(Optional<QuestionResource> previousQuestionOptional, Long applicationId, Model model) {
        String previousUrl;
        String previousText;

        if (previousQuestionOptional.isPresent()) {
            QuestionResource previousQuestion = previousQuestionOptional.get();
            SectionResource previousSection = s.getSectionService().getSectionByQuestionId(previousQuestion.getId());
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
            SectionResource nextSection = s.getSectionService().getSectionByQuestionId(nextQuestion.getId());

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

    private List<InviteResource> pendingInvitations(ApplicationResource application) {
        RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = s.getInviteRestService().getInvitesByApplication(application.getId());

        return pendingAssignableUsersResult.handleSuccessOrFailure(
            failure -> new ArrayList<>(0),
            success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                .filter(item -> !InviteStatusConstants.ACCEPTED.equals(item.getStatus()))
                .collect(Collectors.toList()));
    }

    private List<FormInputResponseResource> getFormInputResponses(ApplicationResource application) {
        return s.getFormInputResponseService().getByApplication(application.getId());
    }

    private void addCompletedDetails(Model model, QuestionResource question, Long applicationId) {
        List<QuestionResource> questions = singletonList(question);

        Set<Long> markedAsComplete = questions
            .stream()
            .filter(q -> q.isMarkAsCompletedEnabled() && s.getQuestionStatusRestService().findQuestionStatusesByQuestionAndApplicationId(q.getId(), applicationId).getSuccessObjectOrThrowException()
                .stream()
                .anyMatch(qs ->
                    (!q.hasMultipleStatuses() && isMarkedAsCompleteForSingleStatus(qs).orElse(Boolean.FALSE))))
            .map(QuestionResource::getId).collect(Collectors.toSet());

        model.addAttribute("markedAsComplete", markedAsComplete);
    }

    private Optional<Boolean> isMarkedAsCompleteForSingleStatus(QuestionStatusResource questionStatus) {
        Boolean markedAsComplete = null;
        if (questionStatus != null) {
            markedAsComplete = questionStatus.getMarkedAsComplete();
        }
        return Optional.ofNullable(markedAsComplete);
    }
}
