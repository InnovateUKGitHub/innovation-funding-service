package org.innovateuk.ifs.application.populator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.application.viewmodel.*;
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
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.*;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.application.ApplicationFormController.MODEL_ATTRIBUTE_FORM;

/**
 * View model for the single question pages
 */
@Component
public class QuestionModelPopulator extends BaseModelPopulator {
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

    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MessageSource messageSource;


    public QuestionViewModel populateModel(final Long questionId, final Long applicationId, final UserResource user, final Model model,
                                           final ApplicationForm form, final QuestionOrganisationDetailsViewModel organisationDetailsViewModel) {
        QuestionResource question = questionService.getById(questionId);
        List<FormInputResource> formInputs = formInputService.findApplicationInputsByQuestion(questionId);
        ApplicationResource application = applicationService.getById(applicationId);
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());

        QuestionViewModel viewModel = addFormAttributes(application, competition, user, model, form,
                question, formInputs, userApplicationRoles);
        addOrganisationDetailsViewModel(viewModel, organisationDetailsViewModel);

        return viewModel;
    }

    private QuestionViewModel addFormAttributes(ApplicationResource application,
                                                CompetitionResource competition,
                                                UserResource user, Model model,
                                                ApplicationForm form, QuestionResource question,
                                                List<FormInputResource> formInputs,
                                                List<ProcessRoleResource> userApplicationRoles){

        form = initializeApplicationForm(form);
        Optional<OrganisationResource> userOrganisation = getUserOrganisation(user.getId(), userApplicationRoles);

        QuestionApplicationViewModel questionApplicationViewModel = addApplicationDetails(application, competition, user.getId(), question, userOrganisation, form, userApplicationRoles);
        NavigationViewModel navigationViewModel = applicationNavigationPopulator.addNavigation(question, application.getId());
        QuestionAssignableViewModel questionAssignableViewModel = addAssignableDetails(application, userOrganisation, user.getId(), question.getId());

        Map<Long, List<FormInputResource>> questionFormInputs = new HashMap<>();
        questionFormInputs.put(question.getId(), formInputs);

        QuestionViewModel questionViewModel = new QuestionViewModel(user, questionFormInputs, question.getShortName(), question,
                questionApplicationViewModel, navigationViewModel, questionAssignableViewModel);

        addQuestionsDetails(questionViewModel, application, form);
        addUserDetails(questionViewModel, application, user.getId());

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);

        return questionViewModel;
    }
    private QuestionApplicationViewModel addApplicationDetails(ApplicationResource application,
                                                               CompetitionResource competition,
                                                               Long userId,
                                                               QuestionResource questionResource,
                                                               Optional<OrganisationResource> userOrganisation,
                                                               ApplicationForm form,
                                                               List<ProcessRoleResource> userApplicationRoles) {
        form.setApplication(application);

        List<QuestionStatusResource> questionStatuses = getQuestionStatuses(questionResource.getId(), application.getId());
        Set<Long> completedDetails = getCompletedDetails(questionResource, application.getId(), questionStatuses);
        Boolean allReadOnly = calculateAllReadOnly(competition, questionResource, questionStatuses, userId, completedDetails);

        QuestionApplicationViewModel questionApplicationViewModel = new QuestionApplicationViewModel(completedDetails, allReadOnly
                , application, competition, userOrganisation.orElse(null));

        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);
        leadOrganisation.ifPresent(org ->
                questionApplicationViewModel.setLeadOrganisation(org)
        );

        addApplicationFormDetailInputs(application, form);
        addSelectedInnovationAreaName(application, questionApplicationViewModel);
        addSelectedResearchCategoryName(application, questionApplicationViewModel);

        return questionApplicationViewModel;
    }

    private void addSelectedInnovationAreaName(ApplicationResource applicationResource, QuestionApplicationViewModel questionApplicationViewModel) {
        if(applicationResource.getNoInnovationAreaApplicable()) {
            questionApplicationViewModel.setNoInnovationAreaApplicable(true);
        }
        else if(applicationResource.getInnovationArea() != null) {
            questionApplicationViewModel.setSelectedInnovationAreaName(applicationResource.getInnovationArea().getName());
        }
    }

    private void addSelectedResearchCategoryName(ApplicationResource applicationResource, QuestionApplicationViewModel questionApplicationViewModel) {
        if(applicationResource.getResearchCategory() != null) {
            questionApplicationViewModel.setSelectedResearchCategoryName(applicationResource.getResearchCategory().getName());
        }
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

    private void addQuestionsDetails(QuestionViewModel questionViewModel, ApplicationResource application, ApplicationForm form) {
        List<FormInputResponseResource> responses = getFormInputResponses(application);
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);
        questionViewModel.setResponses(mappedResponses);
        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((k, v) ->
                values.put(k.toString(), v.getValue())
        );
        form.setFormInput(values);
    }

    private void addUserDetails(QuestionViewModel questionViewModel, ApplicationResource application, Long userId) {
        Boolean userIsLeadApplicant = userService.isLeadApplicant(userId, application);
        ProcessRoleResource leadApplicantProcessRole = userService.getLeadApplicantProcessRoleOrNull(application);
        UserResource leadApplicant = userService.findById(leadApplicantProcessRole.getUser());

        questionViewModel.setUserIsLeadApplicant(userIsLeadApplicant);
        questionViewModel.setLeadApplicant(leadApplicant);
    }

    private QuestionAssignableViewModel addAssignableDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation,
                                      Long userId, Long currentQuestionId) {

        if (isApplicationInViewMode(application, userOrganisation)) {
            return new QuestionAssignableViewModel();
        }

        Map<Long, QuestionStatusResource> questionAssignees;

        QuestionStatusResource questionStatusResource = questionService.getByQuestionIdAndApplicationIdAndOrganisationId(currentQuestionId, application.getId(), getUserOrganisationId(userOrganisation));
        questionAssignees = new HashMap<>();
        if(questionStatusResource != null) {
            questionAssignees.put(currentQuestionId, questionStatusResource);
        }
        QuestionStatusResource questionAssignee = questionAssignees.get(currentQuestionId);

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        questionService.removeNotifications(notifications);

        List<ApplicationInviteResource> pendingAssignableUsers = pendingInvitations(application);

        return new QuestionAssignableViewModel(questionAssignee, processRoleService.findAssignableProcessRoles(application.getId()), pendingAssignableUsers, questionAssignees, notifications);
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
                .map(uar -> organisationService.getOrganisationById(uar.getOrganisationId()))
                .findFirst();
    }

    private void addOrganisationDetailsViewModel(QuestionViewModel viewModel, QuestionOrganisationDetailsViewModel organisationDetailsViewModel) {
        viewModel.setAcademicOrganisations(organisationDetailsViewModel.getAcademicOrganisations());
        viewModel.setApplicationOrganisations(organisationDetailsViewModel.getApplicationOrganisations());
        viewModel.setLeadOrganisation(organisationDetailsViewModel.getLeadOrganisation());
        viewModel.setPendingOrganisationNames(organisationDetailsViewModel.getPendingOrganisationNames());
    }
}
