package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionApplicationViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionAssignableViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.application.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * Class for creating the model for the open section page.
 * These are rendered in the ApplicationFormController.applicationFormWithOpenSection method
 */
@Component
public class OpenSectionModelPopulator extends BaseSectionModelPopulator {

    @Autowired
    private FormInputResponseService formInputResponseService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private FormInputService formInputService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;

    @Autowired
    private FinanceHandler financeHandler;

    @Override
    public BaseSectionViewModel populateModel(ApplicationForm form, Model model, ApplicationResource application, SectionResource section, UserResource user, BindingResult bindingResult, List<SectionResource> allSections, Long organisationId){
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        OpenSectionViewModel openSectionViewModel = new OpenSectionViewModel();
        SectionApplicationViewModel sectionApplicationViewModel = new SectionApplicationViewModel();

        if(null != competition) {
            addApplicationAndSections(openSectionViewModel, sectionApplicationViewModel, application, competition, user.getId(), section, form, allSections);
            addOrganisationAndUserFinanceDetails(openSectionViewModel, competition.getId(), application.getId(), user, model, form, allSections, organisationId);
        }

        form.setBindingResult(bindingResult);
        form.setObjectErrors(bindingResult.getAllErrors());

        openSectionViewModel.setNavigationViewModel(addNavigation(section, application.getId()));
        openSectionViewModel.setSectionApplicationViewModel(sectionApplicationViewModel);

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);

        return openSectionViewModel;
    }

    private void addApplicationDetails(OpenSectionViewModel openSectionViewModel, SectionApplicationViewModel sectionApplicationViewModel, ApplicationResource application,
                                       CompetitionResource competition, Long userId, SectionResource section,
                                       ApplicationForm form, List<ProcessRoleResource> userApplicationRoles, List<SectionResource> allSections) {
        Optional<OrganisationResource> userOrganisation = getUserOrganisation(userId, userApplicationRoles);

        form = initializeApplicationForm(form);
        form.setApplication(application);

        addQuestionsDetails(openSectionViewModel, application, form);
        addUserDetails(openSectionViewModel, application, userId);
        addApplicationFormDetailInputs(application, form);

        if(null != competition) {
            List<FormInputResource> formInputResources = formInputService.findApplicationInputsByCompetition(competition.getId());
            addMappedSectionsDetails(openSectionViewModel, application, competition, section, userOrganisation, allSections, formInputResources, sectionService.filterParentSections(allSections));
        }

        addCompletedDetails(openSectionViewModel, sectionApplicationViewModel, application, userOrganisation, allSections);

        openSectionViewModel.setSectionAssignableViewModel(addAssignableDetails(application, userOrganisation, userId, section));
        sectionApplicationViewModel.setAllReadOnly(calculateAllReadOnly(competition, section.getId(), openSectionViewModel.getSectionsMarkedAsComplete()));
        sectionApplicationViewModel.setCurrentApplication(application);
        sectionApplicationViewModel.setCurrentCompetition(competition);
        sectionApplicationViewModel.setUserOrganisation(userOrganisation.orElse(null));
    }

    private void addOrganisationDetails(OpenSectionViewModel viewModel, ApplicationResource application, List<ProcessRoleResource> userApplicationRoles) {
        SortedSet<OrganisationResource> organisations = getApplicationOrganisations(userApplicationRoles);
        viewModel.setAcademicOrganisations(getAcademicOrganisations(organisations));
        viewModel.setApplicationOrganisations(organisations);

        List<String> activeApplicationOrganisationNames = organisations.stream().map(OrganisationResource::getName).collect(Collectors.toList());

        List<String> pendingOrganisationNames = pendingInvitations(application).stream()
            .map(ApplicationInviteResource::getInviteOrganisationName)
            .distinct()
            .filter(orgName -> StringUtils.hasText(orgName)
                && activeApplicationOrganisationNames.stream().noneMatch(organisationName -> organisationName.equals(orgName))).collect(Collectors.toList());

        viewModel.setPendingOrganisationNames(pendingOrganisationNames);

        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);
        leadOrganisation.ifPresent(organisationResource ->
            viewModel.setLeadOrganisation(organisationResource)
        );
    }

    private void addQuestionsDetails(OpenSectionViewModel openSectionViewModel, ApplicationResource application, Form form) {
        List<FormInputResponseResource> responses = getFormInputResponses(application);
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);
        openSectionViewModel.setResponses(mappedResponses);

        if(form == null){
            form = new Form();
        }
        Map<String, String> values = form.getFormInput();
        mappedResponses.forEach((k, v) ->
            values.put(k.toString(), v.getValue())
        );
        form.setFormInput(values);
    }

    private SectionAssignableViewModel addAssignableDetails(ApplicationResource application, Optional<OrganisationResource> userOrganisation,
        Long userId, SectionResource currentSection) {

        if (isApplicationInViewMode(application, userOrganisation)) {
            return new SectionAssignableViewModel();
        }

        Map<Long, QuestionStatusResource> questionAssignees;

        questionAssignees = questionService.getQuestionStatusesByQuestionIdsAndApplicationIdAndOrganisationId(currentSection.getQuestions(), application.getId(), getUserOrganisationId(userOrganisation));

        List<QuestionStatusResource> notifications = questionService.getNotificationsForUser(questionAssignees.values(), userId);
        questionService.removeNotifications(notifications);

        List<ApplicationInviteResource> pendingAssignableUsers = pendingInvitations(application);

        return new SectionAssignableViewModel(processRoleService.findAssignableProcessRoles(application.getId()), pendingAssignableUsers, questionAssignees, notifications);
    }

    private List<ApplicationInviteResource> pendingInvitations(ApplicationResource application) {
        RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(application.getId());

        return pendingAssignableUsersResult.handleSuccessOrFailure(
            failure -> new ArrayList<>(0),
            success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                .filter(item -> !InviteStatus.OPENED.equals(item.getStatus()))
                .collect(Collectors.toList()));
    }

    private void addCompletedDetails(OpenSectionViewModel openSectionViewModel, SectionApplicationViewModel sectionApplicationViewModel, ApplicationResource application, Optional<OrganisationResource> userOrganisation, List<SectionResource> allSections) {
        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids

        List<SectionResource> financeSections = getSectionsByType(allSections, FINANCE);

        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = convertToCombinedMarkedAsCompleteSections(completedSectionsByOrganisation);

        boolean hasFinanceSection = false;
        Long financeSectionId = null;
        if (!financeSections.isEmpty()) {
            hasFinanceSection = true;
            financeSectionId = financeSections.get(0).getId();
        }

        List<SectionResource> eachOrganisationFinanceSections = getSectionsByType(allSections, FINANCE);
        Long eachCollaboratorFinanceSectionId;
        if(eachOrganisationFinanceSections.isEmpty()) {
            eachCollaboratorFinanceSectionId = null;
        } else {
            eachCollaboratorFinanceSectionId = eachOrganisationFinanceSections.get(0).getId();
        }

        sectionApplicationViewModel.setMarkedAsComplete(markedAsComplete);
        openSectionViewModel.setCompletedSectionsByOrganisation(completedSectionsByOrganisation);
        openSectionViewModel.setSectionsMarkedAsComplete(sectionsMarkedAsComplete);
        openSectionViewModel.setAllQuestionsCompleted(sectionService.allSectionsMarkedAsComplete(application.getId()));
        openSectionViewModel.setHasFinanceSection(hasFinanceSection);
        openSectionViewModel.setFinanceSectionId(financeSectionId);
        openSectionViewModel.setEachCollaboratorFinanceSectionId(eachCollaboratorFinanceSectionId);
    }

    private Set<Long> convertToCombinedMarkedAsCompleteSections(Map<Long, Set<Long>> completedSectionsByOrganisation) {
        Set<Long> combinedMarkedAsComplete = new HashSet<>();

        completedSectionsByOrganisation.forEach((organisationId, completedSections) -> combinedMarkedAsComplete.addAll(completedSections));
        completedSectionsByOrganisation.forEach((key, values) -> combinedMarkedAsComplete.retainAll(values));

        return combinedMarkedAsComplete;
    }
    
    private List<SectionResource> getSectionsByType(List<SectionResource> list, SectionType type){
        return simpleFilter(list, s -> type.equals(s.getType()));
    }

    private void addApplicationAndSections(OpenSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel, ApplicationResource application,
                                           CompetitionResource competition,
                                           Long userId,
                                           SectionResource section,
                                           ApplicationForm form,
                                           List<SectionResource> allSections) {

        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        addOrganisationDetails(viewModel, application, userApplicationRoles);
        addApplicationDetails(viewModel, sectionApplicationViewModel, application, competition, userId, section, form, userApplicationRoles, allSections);
        addSectionDetails(viewModel, section, formInputService.findApplicationInputsByCompetition(competition.getId()));

        viewModel.setCompletedQuestionsPercentage(application.getCompletion() == null ? 0 : application.getCompletion().intValue());
    }

    //TODO - INFUND-7482 - remove usages of Model model
    private void addOrganisationAndUserFinanceDetails(OpenSectionViewModel openSectionViewModel, Long competitionId, Long applicationId, UserResource user,
                                                      Model model, ApplicationForm form, List<SectionResource> allSections,
                                                      Long organisationId) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        List<SectionResource> financeSections = getSectionsByType(allSections, FINANCE);

        boolean hasFinanceSection = !financeSections.isEmpty();

        if(hasFinanceSection) {
            Long organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
            List<QuestionResource> costsQuestions = questionService.getQuestionsBySectionIdAndType(financeSections.get(0).getId(), QuestionType.COST);

            applicationFinanceOverviewModelManager.addFinanceDetails(model, competitionId, applicationId, Optional.of(organisationId));
            if(!form.isAdminMode()){

                if(competitionResource.isOpen()) {
                    openSectionViewModel.setFinanceViewModel(financeHandler.getFinanceModelManager(organisationType).getFinanceViewModel(applicationId, costsQuestions, user.getId(), form, organisationId));
                }
            }
        }
    }

    private SortedSet<OrganisationResource> getApplicationOrganisations(List<ProcessRoleResource> userApplicationRoles) {
        Comparator<OrganisationResource> compareById =
            Comparator.comparingLong(OrganisationResource::getId);
        Supplier<SortedSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);

        return userApplicationRoles.stream()
            .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName())
                || uar.getRoleName().equals(UserApplicationRole.COLLABORATOR.getRoleName()))
            .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccessObjectOrThrowException())
            .collect(Collectors.toCollection(supplier));
    }

    private SortedSet<OrganisationResource> getAcademicOrganisations(SortedSet<OrganisationResource> organisations) {
        Comparator<OrganisationResource> compareById =
            Comparator.comparingLong(OrganisationResource::getId);
        Supplier<TreeSet<OrganisationResource>> supplier = () -> new TreeSet<>(compareById);
        ArrayList<OrganisationResource> organisationList = new ArrayList<>(organisations);

        return organisationList.stream()
            .filter(o -> OrganisationTypeEnum.RESEARCH.getId().equals(o.getOrganisationType()))
            .collect(Collectors.toCollection(supplier));
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
            .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
            .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccessObjectOrThrowException())
            .findFirst();
    }
}
