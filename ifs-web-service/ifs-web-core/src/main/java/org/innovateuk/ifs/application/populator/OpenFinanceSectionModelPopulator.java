package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.view.FinanceOverviewModelManager;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionApplicationViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionAssignableViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;

import static java.util.Collections.singletonList;

/**
 * Class for populating the model for the "Your Finances" section
 */
@Component
public class OpenFinanceSectionModelPopulator extends BaseSectionModelPopulator {
    public static final String MODEL_ATTRIBUTE_FORM = "form";

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
    private FinanceOverviewModelManager financeOverviewModelManager;

    @Autowired
    private FinanceHandler financeHandler;

    @Autowired
    private UserRestService userRestService;

    @Override
    public BaseSectionViewModel populateModel(final ApplicationForm form,
                              final Model model,
                              final ApplicationResource application,
                              final SectionResource section,
                              final UserResource user,
                              final BindingResult bindingResult,
                              final List<SectionResource> allSections){
        return populateModel(form, model, application, section, user, bindingResult, allSections, null, false, false);
    }

    @Override
    public BaseSectionViewModel populateModel(ApplicationForm form,
                                              Model model,
                                              ApplicationResource application,
                                              SectionResource section,
                                              UserResource user,
                                              BindingResult bindingResult,
                                              List<SectionResource> allSections,
                                              final Long organisationId,
                                              final boolean isInternalUser,
                                              boolean showProjectFinance){
        CompetitionResource competition = competitionService.getById(application.getCompetition());
        List<QuestionResource> costsQuestions = questionService.getQuestionsBySectionIdAndType(section.getId(), QuestionType.COST);

        OpenFinanceSectionViewModel openFinanceSectionViewModel = new OpenFinanceSectionViewModel(addNavigation(section, application.getId()),
                section, true, section.getId(), user, isSubFinanceSection(section));
        SectionApplicationViewModel sectionApplicationViewModel = new SectionApplicationViewModel();

        sectionApplicationViewModel.setAllReadOnly(calculateAllReadOnly(competition) || SectionType.FINANCE.equals(section.getType()));
        sectionApplicationViewModel.setCurrentApplication(application);
        sectionApplicationViewModel.setCurrentCompetition(competition);

        addQuestionsDetails(openFinanceSectionViewModel, application, form);
        addApplicationAndSections(openFinanceSectionViewModel, sectionApplicationViewModel, application, competition, user.getId(), section, form, allSections);


        if(isInternalUser && organisationId != null){
            OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
            String organisationType = organisationResource.getOrganisationTypeName();
            addOrganisationAndUserFinanceDetails(application.getCompetition(), application.getId(), costsQuestions, user, model, form, organisationType, organisationId);
        } else {
            ProcessRoleResource userApplicationRole = userRestService.findProcessRole(user.getId(), application.getId()).getSuccessObjectOrThrowException();
            addOrganisationAndUserFinanceDetails(application.getCompetition(), application.getId(), costsQuestions, user, model, form, userApplicationRole.getOrganisation());
        }

        form.setBindingResult(bindingResult);
        form.setObjectErrors(bindingResult.getAllErrors());

        openFinanceSectionViewModel.setSectionApplicationViewModel(sectionApplicationViewModel);
        populateSubSectionMenuOptions(openFinanceSectionViewModel, allSections, openFinanceSectionViewModel.getSectionApplicationViewModel().getUserOrganisation().getId());

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);

        return openFinanceSectionViewModel;
    }

    private void populateSubSectionMenuOptions(OpenFinanceSectionViewModel viewModel, final List<SectionResource> allSections, Long userOrganisationId) {
        QuestionResource applicationDetailsQuestion = questionService.getQuestionByCompetitionIdAndFormInputType(viewModel.getApplication().getCurrentApplication().getCompetition(), FormInputType.APPLICATION_DETAILS).getSuccessObjectOrThrowException();
        Map<Long, QuestionStatusResource>  questionStatuses = questionService.getQuestionStatusesForApplicationAndOrganisation(viewModel.getApplication().getCurrentApplication().getId(), userOrganisationId);
        QuestionStatusResource applicationDetailsStatus = questionStatuses.get(applicationDetailsQuestion.getId());

        boolean organisationSizeComplete = false;
        if (viewModel.getSectionsMarkedAsComplete() != null) {
            organisationSizeComplete = viewModel.getSectionsMarkedAsComplete().contains(allSections.stream().filter(filterSection -> SectionType.ORGANISATION_FINANCES.equals(filterSection.getType())).map(SectionResource::getId).findFirst().orElse(-1L));
        }
        boolean applicationDetailsComplete = applicationDetailsStatus != null && applicationDetailsStatus.getMarkedAsComplete();

        viewModel.setFundingSectionLocked(!(organisationSizeComplete && applicationDetailsComplete));
        viewModel.setApplicationDetailsQuestionId(applicationDetailsQuestion.getId());
        viewModel.setYourOrganisationSectionId(allSections.stream().filter(filterSection -> SectionType.ORGANISATION_FINANCES.equals(filterSection.getType())).findFirst().map(SectionResource::getId).orElse(null));
    }


    private Boolean isSubFinanceSection(SectionResource section) {
        return SectionType.FINANCE.equals(section.getType().getParent().orElse(null));
    }

    private void addApplicationDetails(OpenFinanceSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel, ApplicationResource application,
                                       CompetitionResource competition, Long userId, SectionResource section,
                                       ApplicationForm form, List<ProcessRoleResource> userApplicationRoles,
                                       List<SectionResource> allSections, List<FormInputResource> inputs,
                                       Optional<OrganisationResource> userOrganisation) {

        form = initializeApplicationForm(form);
        form.setApplication(application);

        //Parent finance section has no assignable or question details.
        if (!SectionType.FINANCE.equals(section.getType())) {
            addQuestionsDetails(viewModel, application, form);
        }
        addUserDetails(viewModel, application, userId);
        if(null != competition) {
            addMappedSectionsDetails(viewModel, application, competition, section, userOrganisation, allSections, inputs, singletonList(section));
        }

        if (!SectionType.FINANCE.equals(section.getType())) {
            viewModel.setSectionAssignableViewModel(addAssignableDetails(application, userOrganisation, userId, section));
        }
        addCompletedDetails(sectionApplicationViewModel, application, userOrganisation);

        sectionApplicationViewModel.setUserOrganisation(userOrganisation.orElse(null));
    }

    private void addQuestionsDetails(OpenFinanceSectionViewModel viewModel, ApplicationResource application, Form form) {
        List<FormInputResponseResource> responses = getFormInputResponses(application);
        Map<Long, FormInputResponseResource> mappedResponses = formInputResponseService.mapFormInputResponsesToFormInput(responses);

        viewModel.setResponses(mappedResponses);

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

        return new SectionAssignableViewModel(questionAssignees, notifications);
    }

    private void addCompletedDetails(SectionApplicationViewModel sectionApplicationViewModel, ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Future<Set<Long>> markedAsComplete = getMarkedAsCompleteDetails(application, userOrganisation); // List of question ids
        sectionApplicationViewModel.setMarkedAsComplete(markedAsComplete);
    }

    private void addApplicationAndSections(OpenFinanceSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel,
                                           ApplicationResource application,
                                            CompetitionResource competition,
                                            Long userId,
                                            SectionResource section,
                                            ApplicationForm form,
                                            List<SectionResource> allSections) {
        List<FormInputResource> inputs = formInputService.findApplicationInputsByCompetition(application.getCompetition());
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> userOrganisation = organisationService.getOrganisationForUser(userId, userApplicationRoles);

        addSectionsMarkedAsComplete(viewModel, userApplicationRoles, userId, application, userOrganisation);
        addApplicationDetails(viewModel, sectionApplicationViewModel, application, competition, userId, section, form, userApplicationRoles, allSections, inputs, userOrganisation);

        addSectionDetails(viewModel, section, inputs);
    }

    private void addSectionsMarkedAsComplete(OpenFinanceSectionViewModel viewModel, List<ProcessRoleResource> userApplicationRoles, Long userId, ApplicationResource application, Optional<OrganisationResource> userOrganisation) {
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(application.getId());
        Set<Long> sectionsMarkedAsComplete = completedSectionsByOrganisation.get(userOrganisation.map(OrganisationResource::getId)
                .orElse(completedSectionsByOrganisation.keySet().stream().findFirst().orElse(-1L)));

        viewModel.setSectionsMarkedAsComplete(sectionsMarkedAsComplete);
    }

    //TODO - INFUND-7482 - remove usages of Model model
    private void addOrganisationAndUserFinanceDetails(Long competitionId, Long applicationId, List<QuestionResource> costsQuestions, UserResource user,
        Model model, ApplicationForm form, Long organisationId) {
        String organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
        financeHandler.getFinanceModelManager(organisationType).addOrganisationFinanceDetails(model, applicationId, costsQuestions, user.getId(), form, organisationId);
        addOrganisationAndUserFinanceDetails(competitionId, applicationId, costsQuestions, user, model, form, organisationType, organisationId);
    }

    private void addOrganisationAndUserFinanceDetails(Long competitionId, Long applicationId, List<QuestionResource> costsQuestions, UserResource user,
                                                      Model model, ApplicationForm form, String organisationType, Long organisationId) {
        financeOverviewModelManager.addFinanceDetails(model, competitionId, applicationId);
        financeHandler.getFinanceModelManager(organisationType).addOrganisationFinanceDetails(model, applicationId, costsQuestions, user.getId(), form, organisationId);
    }
}
