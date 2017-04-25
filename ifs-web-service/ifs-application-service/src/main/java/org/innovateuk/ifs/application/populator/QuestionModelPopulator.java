package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.*;
import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    public QuestionViewModel populateModel(ApplicantQuestionResource question, final Model model, final ApplicationForm form, final QuestionOrganisationDetailsViewModel organisationDetailsViewModel) {
        QuestionViewModel viewModel = addFormAttributes(question, model, form);
        addOrganisationDetailsViewModel(viewModel, organisationDetailsViewModel);

        return viewModel;
    }

    private QuestionViewModel addFormAttributes(ApplicantQuestionResource question, final Model model, ApplicationForm form) {

        form = initializeApplicationForm(form);

        QuestionApplicationViewModel questionApplicationViewModel = addApplicationDetails(question, form);
        NavigationViewModel navigationViewModel = applicationNavigationPopulator.addNavigation(question.getQuestion(), question.getApplication().getId());
        QuestionAssignableViewModel questionAssignableViewModel = addAssignableDetails(question);

        Map<Long, List<FormInputResource>> questionFormInputs = new HashMap<>();
        questionFormInputs.put(question.getQuestion().getId(), question.getFormInputs().stream().map(ApplicantFormInputResource::getFormInput).collect(Collectors.toList()));

        QuestionViewModel questionViewModel = new QuestionViewModel(question.getCurrentApplicant().getUser(), questionFormInputs, question.getQuestion().getShortName(), question.getQuestion(),
                questionApplicationViewModel, navigationViewModel, questionAssignableViewModel);

        addQuestionsDetails(question, questionViewModel, form);
        addUserDetails(questionViewModel, question);

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);

        return questionViewModel;
    }

    private QuestionApplicationViewModel addApplicationDetails(ApplicantQuestionResource question, ApplicationForm form) {
        form.setApplication(question.getApplication());

        Set<Long> completedDetails = getCompletedDetails(question.getQuestion(), question.getApplication().getId(), question.getQuestionStatuses().stream().map(ApplicantQuestionStatusResource::getStatus).collect(Collectors.toList()));
        Boolean allReadOnly = calculateAllReadOnly(question, completedDetails);

        QuestionApplicationViewModel questionApplicationViewModel = new QuestionApplicationViewModel(completedDetails, allReadOnly
                , question.getApplication(), question.getCompetition(), question.getCurrentApplicant().getOrganisation());

        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(question);
        leadOrganisation.ifPresent(questionApplicationViewModel::setLeadOrganisation);

        addApplicationFormDetailInputs(question.getApplication(), form);
        addSelectedInnovationAreaName(question.getApplication(), questionApplicationViewModel);
        addSelectedResearchCategoryName(question.getApplication(), questionApplicationViewModel);

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

    private Boolean calculateAllReadOnly(ApplicantQuestionResource question, Set<Long> completedDetails) {
        if(null != question.getCompetition().getCompetitionStatus() && question.getCompetition().getCompetitionStatus().equals(CompetitionStatus.OPEN)) {
            Set<Long> assignedQuestions = getAssigneeQuestions(question.getQuestion(), question.getQuestionStatuses().stream().map(ApplicantQuestionStatusResource::getStatus).collect(Collectors.toList()), question.getCurrentApplicant().getUser().getId());
            return question.getQuestionStatuses().size() > 0 &&
                    (completedDetails.contains(question.getQuestion().getId()) || !assignedQuestions.contains(question.getQuestion().getId()));
        } else {
            return true;
        }
    }

    private void addQuestionsDetails(ApplicantQuestionResource questionResource, QuestionViewModel questionViewModel, ApplicationForm form) {
        List<FormInputResponseResource> responses = questionResource.getFormInputs().stream()
                .map(ApplicantFormInputResource::getResponse)
                .map(ApplicantFormInputResponseResource::getResponse)
                .collect(Collectors.toList());
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);
        questionViewModel.setResponses(mappedResponses);
        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((k, v) ->
                values.put(k.toString(), v.getValue())
        );
        form.setFormInput(values);
    }

    private void addUserDetails(QuestionViewModel questionViewModel, ApplicantQuestionResource question) {
        Boolean userIsLeadApplicant = question.getCurrentApplicant().getProcessRole().getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName());
        UserResource leadApplicant = question.getApplicants().stream()
                .filter(applicantResource -> applicantResource.getProcessRole().getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(ApplicantResource::getUser)
                .findAny().orElse(null);
        questionViewModel.setUserIsLeadApplicant(userIsLeadApplicant);
        questionViewModel.setLeadApplicant(leadApplicant);
    }

    private QuestionAssignableViewModel addAssignableDetails(ApplicantQuestionResource question) {

        if (isApplicationInViewMode(question.getApplication(), Optional.of(question.getCurrentApplicant().getOrganisation()))) {
            return new QuestionAssignableViewModel();
        }

        Map<Long, QuestionStatusResource> questionAssignees;

        QuestionStatusResource questionStatusResource = question.getQuestionStatuses().stream()
                .filter(status -> status.getAssignee().getOrganisation().getId().equals(question.getCurrentApplicant().getOrganisation().getId()))
                .findAny()
                .map(ApplicantQuestionStatusResource::getStatus)
                .orElse(null);
        questionAssignees = new HashMap<>();
        if(questionStatusResource != null) {
            questionAssignees.put(question.getQuestion().getId(), questionStatusResource);
        }
        QuestionStatusResource questionAssignee = questionAssignees.get(question.getQuestion().getId());

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), question.getCurrentApplicant().getUser().getId());
        questionService.removeNotifications(notifications);

        List<ApplicationInviteResource> pendingAssignableUsers = pendingInvitations(question.getApplication());

        return new QuestionAssignableViewModel(questionAssignee, question.getAssignableProcessRoles(), pendingAssignableUsers, questionAssignees, notifications);
    }

    private List<ApplicationInviteResource> pendingInvitations(ApplicationResource application) {
        RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(application.getId());

        return pendingAssignableUsersResult.handleSuccessOrFailure(
                failure -> new ArrayList<>(0),
                success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                        .filter(item -> !InviteStatus.OPENED.equals(item.getStatus()))
                        .collect(Collectors.toList()));
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

    private Optional<OrganisationResource> getApplicationLeadOrganisation(ApplicantQuestionResource question) {

        return question.getApplicants().stream()
                .filter(applicant -> applicant.getProcessRole().getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(ApplicantResource::getOrganisation)
                .findFirst();
    }

    private void addOrganisationDetailsViewModel(QuestionViewModel viewModel, QuestionOrganisationDetailsViewModel organisationDetailsViewModel) {
        viewModel.setAcademicOrganisations(organisationDetailsViewModel.getAcademicOrganisations());
        viewModel.setApplicationOrganisations(organisationDetailsViewModel.getApplicationOrganisations());
        viewModel.setLeadOrganisation(organisationDetailsViewModel.getLeadOrganisation());
        viewModel.setPendingOrganisationNames(organisationDetailsViewModel.getPendingOrganisationNames());
    }
}
