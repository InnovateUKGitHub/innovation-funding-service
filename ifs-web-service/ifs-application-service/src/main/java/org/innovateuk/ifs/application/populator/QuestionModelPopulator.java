package org.innovateuk.ifs.application.populator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.viewmodel.QuestionApplicationViewModel;
import org.innovateuk.ifs.application.viewmodel.QuestionNavigationViewModel;
import org.innovateuk.ifs.application.viewmodel.QuestionViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.application.ApplicationFormController.*;

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

        form = initializeApplicationForm(form);

        QuestionApplicationViewModel questionApplicationViewModel = addApplicationDetails(application, competition, user.getId(), question, form, userApplicationRoles);
        QuestionNavigationViewModel questionNavigationViewModel = addNavigation(question, application.getId());

        Map<Long, List<FormInputResource>> questionFormInputs = new HashMap<>();
        questionFormInputs.put(question.getId(), formInputs);

        QuestionViewModel questionViewModel = new QuestionViewModel(user, questionFormInputs, question.getShortName(), question,
                questionApplicationViewModel, questionNavigationViewModel);

        model.addAttribute(MODEL_ATTRIBUTE_MODEL, questionViewModel);
        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);
    }

    private ApplicationForm initializeApplicationForm(ApplicationForm form) {
        if(null == form){
            form = new ApplicationForm();
        }

        return form;
    }

    private QuestionApplicationViewModel addApplicationDetails(ApplicationResource application,
                                                               CompetitionResource competition,
                                                               Long userId,
                                                               QuestionResource questionResource,
                                                               ApplicationForm form,
                                                               List<ProcessRoleResource> userApplicationRoles) {

        form.setApplication(application);

        OrganisationResource userOrganisation = getUserOrganisation(userId, userApplicationRoles);
        List<QuestionStatusResource> questionStatuses = getQuestionStatuses(questionResource.getId(), application.getId());
        Set<Long> completedDetails = getCompletedDetails(questionResource, application.getId(), questionStatuses);
        Boolean allReadOnly = calculateAllReadOnly(competition, questionResource, questionStatuses, userId, completedDetails);

        QuestionApplicationViewModel questionApplicationViewModel = new QuestionApplicationViewModel(completedDetails, allReadOnly
                , application, competition, userOrganisation);

        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);
        leadOrganisation.ifPresent(org ->
                questionApplicationViewModel.setLeadOrganisation(org)
        );

        addQuestionsDetails(questionApplicationViewModel, application, form);
        addUserDetails(questionApplicationViewModel, application, userId);
        addApplicationFormDetailInputs(application, form);
        addAssignableDetails(questionApplicationViewModel, application, userOrganisation, userId, questionResource.getId());

        return questionApplicationViewModel;
    }

    private Boolean calculateAllReadOnly(CompetitionResource competition, QuestionResource questionResource, List<QuestionStatusResource> questionStatuses, Long userId, Set<Long> completedDetails) {
        if(null != competition.getCompetitionStatus() && competition.getCompetitionStatus().equals(CompetitionStatus.OPEN)) {
            Set<Long> assignedQuestions = getAssigneeQuestions(questionResource, questionStatuses, userId);
            return questionStatuses.size() > 0 &&
                    (completedDetails.contains(questionResource.getId()) || !assignedQuestions.contains(questionResource.getId()));
        } else {
            return true;
        }
    }

    private void addQuestionsDetails(QuestionApplicationViewModel questionApplicationViewModel, ApplicationResource application, ApplicationForm form) {
        List<FormInputResponseResource> responses = getFormInputResponses(application);
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);
        questionApplicationViewModel.setResponses(mappedResponses);
        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((k, v) ->
                values.put(k.toString(), v.getValue())
        );
        form.setFormInput(values);
    }

    private void addUserDetails(QuestionApplicationViewModel questionApplicationViewModel, ApplicationResource application, Long userId) {
        Boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());

        questionApplicationViewModel.setUserIsLeadApplicant(userIsLeadApplicant);
        questionApplicationViewModel.setLeadApplicant(leadApplicant);
    }

    private void addAssignableDetails(QuestionApplicationViewModel questionApplicationViewModel, ApplicationResource application, OrganisationResource userOrganisation,
                                      Long userId, Long currentQuestionId) {

        if (isApplicationInViewMode(questionApplicationViewModel, application, userOrganisation))
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

        questionApplicationViewModel.setQuestionAssignee(questionAssignee);
        questionApplicationViewModel.setAssignableUsers(processRoleService.findAssignableProcessRoles(application.getId()));
        questionApplicationViewModel.setPendingAssignableUsers(pendingAssignableUsers);
        questionApplicationViewModel.setQuestionAssignees(questionAssignees);
        questionApplicationViewModel.setNotifications(notifications);
    }

    private boolean isApplicationInViewMode(QuestionApplicationViewModel questionApplicationViewModel, ApplicationResource application, OrganisationResource userOrganisation) {
        if(!application.isOpen() || userOrganisation == null){
            //Application Not open, so add empty lists
            questionApplicationViewModel.setAssignableUsers(CompletableFuture.completedFuture(new ArrayList<ProcessRoleResource>()));
            questionApplicationViewModel.setPendingAssignableUsers(new ArrayList<ApplicationInviteResource>());
            questionApplicationViewModel.setQuestionAssignees(new HashMap<Long, QuestionStatusResource>());
            questionApplicationViewModel.setNotifications(new ArrayList<QuestionStatusResource>());
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

    private QuestionNavigationViewModel addNavigation(QuestionResource question, Long applicationId) {
        if (question == null) {
            return null;
        }

        QuestionNavigationViewModel questionNavigationViewModel = new QuestionNavigationViewModel();

        Optional<QuestionResource> previousQuestion = questionService.getPreviousQuestion(question.getId());
        addPreviousQuestionToModel(previousQuestion, applicationId, questionNavigationViewModel);
        Optional<QuestionResource> nextQuestion = questionService.getNextQuestion(question.getId());
        addNextQuestionToModel(nextQuestion, applicationId, questionNavigationViewModel);

        return questionNavigationViewModel;
    }

    private void addPreviousQuestionToModel(Optional<QuestionResource> previousQuestionOptional, Long applicationId, QuestionNavigationViewModel questionNavigationViewModel) {
        if (previousQuestionOptional.isPresent()) {
            String previousUrl;
            String previousText;

            QuestionResource previousQuestion = previousQuestionOptional.get();
            SectionResource previousSection = sectionService.getSectionByQuestionId(previousQuestion.getId());
            if (previousSection.isQuestionGroup()) {
                previousUrl = APPLICATION_BASE_URL + applicationId + "/form" + SECTION_URL + previousSection.getId();
                previousText = previousSection.getName();
            } else {
                previousUrl = APPLICATION_BASE_URL + applicationId + "/form" + QUESTION_URL + previousQuestion.getId();
                previousText = previousQuestion.getShortName();
            }

            questionNavigationViewModel.setPreviousUrl(previousUrl);
            questionNavigationViewModel.setPreviousText(previousText);
        }
    }

    private void addNextQuestionToModel(Optional<QuestionResource> nextQuestionOptional, Long applicationId, QuestionNavigationViewModel questionNavigationViewModel) {
        if (nextQuestionOptional.isPresent()) {
            String nextUrl;
            String nextText;

            QuestionResource nextQuestion = nextQuestionOptional.get();
            SectionResource nextSection = sectionService.getSectionByQuestionId(nextQuestion.getId());

            if (nextSection.isQuestionGroup()) {
                nextUrl = APPLICATION_BASE_URL + applicationId + "/form" + SECTION_URL + nextSection.getId();
                nextText = nextSection.getName();
            } else {
                nextUrl = APPLICATION_BASE_URL + applicationId + "/form" + QUESTION_URL + nextQuestion.getId();
                nextText = nextQuestion.getShortName();
            }

            questionNavigationViewModel.setNextUrl(nextUrl);
            questionNavigationViewModel.setNextText(nextText);
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
