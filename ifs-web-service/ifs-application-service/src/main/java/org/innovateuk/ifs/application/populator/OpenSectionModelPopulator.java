package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
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
import org.innovateuk.ifs.form.service.FormInputRestService;
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
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
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
    private FormInputRestService formInputRestService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;

    @Autowired
    private FinanceHandler financeHandler;

    @Override
    public BaseSectionViewModel populateModel(ApplicationForm form, Model model, BindingResult bindingResult, ApplicantSectionResource applicantSection) {
        OpenSectionViewModel openSectionViewModel = new OpenSectionViewModel();
        SectionApplicationViewModel sectionApplicationViewModel = new SectionApplicationViewModel();

        addApplicationAndSections(openSectionViewModel, sectionApplicationViewModel, form, applicantSection);
//        addOrganisationAndUserFinanceDetails(openSectionViewModel, model, form, applicantSection);

        form.setBindingResult(bindingResult);
        form.setObjectErrors(bindingResult.getAllErrors());

        openSectionViewModel.setNavigationViewModel(addNavigation(applicantSection.getSection(),applicantSection.getApplication().getId()));
        openSectionViewModel.setSectionApplicationViewModel(sectionApplicationViewModel);

        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);

        return openSectionViewModel;
    }

    private void addApplicationDetails(OpenSectionViewModel openSectionViewModel, SectionApplicationViewModel sectionApplicationViewModel, ApplicationForm form,
                                       ApplicantSectionResource applicantSection) {

        form = initializeApplicationForm(form);
        form.setApplication(applicantSection.getApplication());

        addQuestionsDetails(openSectionViewModel, applicantSection, form);
        addUserDetails(openSectionViewModel, applicantSection);
        addApplicationFormDetailInputs(applicantSection.getApplication(), form);

        addMappedSectionsDetails(openSectionViewModel, applicantSection);

        addCompletedDetails(openSectionViewModel, sectionApplicationViewModel, applicantSection);

        openSectionViewModel.setSectionAssignableViewModel(addAssignableDetails(applicantSection));
        sectionApplicationViewModel.setAllReadOnly(calculateAllReadOnly(openSectionViewModel, applicantSection));
        sectionApplicationViewModel.setCurrentApplication(applicantSection.getApplication());
        sectionApplicationViewModel.setCurrentCompetition(applicantSection.getCompetition());
        sectionApplicationViewModel.setUserOrganisation(applicantSection.getCurrentApplicant().getOrganisation());
    }

    private void addOrganisationDetails(OpenSectionViewModel viewModel, ApplicantSectionResource applicantSection) {
        viewModel.setAcademicOrganisations(applicantSection.allOrganisations()
                .filter(organisation -> organisation.getOrganisationType().equals(OrganisationTypeEnum.RESEARCH.getId()))
                .collect(Collectors.toSet()));
        viewModel.setApplicationOrganisations(applicantSection.allOrganisations().collect(Collectors.toSet()));

        List<String> activeApplicationOrganisationNames = applicantSection.allOrganisations().map(OrganisationResource::getName).collect(Collectors.toList());

        List<String> pendingOrganisationNames = pendingInvitations(applicantSection.getApplication()).stream()
            .map(ApplicationInviteResource::getInviteOrganisationName)
            .distinct()
            .filter(orgName -> StringUtils.hasText(orgName)
                && activeApplicationOrganisationNames.stream().noneMatch(organisationName -> organisationName.equals(orgName))).collect(Collectors.toList());

        viewModel.setPendingOrganisationNames(pendingOrganisationNames);

        viewModel.setLeadOrganisation(applicantSection.getApplicants().stream()
                .filter(ApplicantResource::isLead)
                .map(ApplicantResource::getOrganisation)
                .findAny().orElse(null));
    }


    private List<ApplicationInviteResource> pendingInvitations(ApplicationResource application) {
        RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(application.getId());

        return pendingAssignableUsersResult.handleSuccessOrFailure(
            failure -> new ArrayList<>(0),
            success -> success.stream().flatMap(item -> item.getInviteResources().stream())
                .filter(item -> !InviteStatus.OPENED.equals(item.getStatus()))
                .collect(Collectors.toList()));
    }

    private void addCompletedDetails(OpenSectionViewModel openSectionViewModel, SectionApplicationViewModel sectionApplicationViewModel, ApplicantSectionResource applicantSection) {
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(applicantSection.getApplication().getId());
        Set<Long> sectionsMarkedAsComplete = convertToCombinedMarkedAsCompleteSections(completedSectionsByOrganisation);

        Optional<ApplicantSectionResource> optionalFinanceSection = applicantSection.allSections().filter(section -> section.getSection().getType().equals(FINANCE)).findAny();
        Optional<Long> optionalFinanceSectionId = optionalFinanceSection.map(applicantSectionResource -> applicantSectionResource.getSection().getId());


        addSectionsMarkedAsComplete(openSectionViewModel, applicantSection);
        openSectionViewModel.setCompletedSectionsByOrganisation(completedSectionsByOrganisation);
        openSectionViewModel.setSectionsMarkedAsComplete(sectionsMarkedAsComplete);
        openSectionViewModel.setHasFinanceSection(optionalFinanceSection.isPresent());
        openSectionViewModel.setFinanceSectionId(optionalFinanceSectionId.orElse(null));
        openSectionViewModel.setEachCollaboratorFinanceSectionId(optionalFinanceSectionId.orElse(null));
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

    private void addApplicationAndSections(OpenSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel,
                                           ApplicationForm form, ApplicantSectionResource applicantSection) {

        addOrganisationDetails(viewModel, applicantSection);
        addApplicationDetails(viewModel, sectionApplicationViewModel, form, applicantSection);
        addSectionDetails(viewModel, applicantSection);

        viewModel.setCompletedQuestionsPercentage(applicantSection.getApplication().getCompletion() == null ? 0 : applicantSection.getApplication().getCompletion().intValue());
    }
//
//    //TODO - INFUND-7482 - remove usages of Model model
//    private void addOrganisationAndUserFinanceDetails(OpenSectionViewModel openSectionViewModel, Model model, ApplicationForm form, ApplicantSectionResource applicantSection) {
//        List<SectionResource> financeSections = getSectionsByType(allSections, FINANCE);
//
//        boolean hasFinanceSection = !financeSections.isEmpty();
//
//        if(hasFinanceSection) {
//            Long organisationType = organisationService.getOrganisationType(user.getId(), applicationId);
//            List<QuestionResource> costsQuestions = questionService.getQuestionsBySectionIdAndType(financeSections.get(0).getId(), QuestionType.COST);
//
//            applicationFinanceOverviewModelManager.addFinanceDetails(model, competitionId, applicationId, Optional.of(organisationId));
//            if(!form.isAdminMode()){
//
//                if(competitionResource.isOpen()) {
//                    openSectionViewModel.setFinanceViewModel(financeHandler.getFinanceModelManager(organisationType).getFinanceViewModel(applicationId, costsQuestions, user.getId(), form, organisationId));
//                }
//            }
//        }
//    }


    private Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
            .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
            .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccessObjectOrThrowException())
            .findFirst();
    }
}
