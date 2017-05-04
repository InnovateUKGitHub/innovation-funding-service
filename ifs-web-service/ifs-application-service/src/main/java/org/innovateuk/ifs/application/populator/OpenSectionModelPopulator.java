package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.AbstractSectionViewModel;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

/**
 * Class for creating the model for the open section page.
 * These are rendered in the ApplicationFormController.applicationFormWithOpenSection method
 */
@Component
public class OpenSectionModelPopulator extends BaseSectionModelPopulator {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private FinanceOverviewPopulator financeOverviewPopulator;

    @Override
    public AbstractSectionViewModel populateModel(ApplicationForm form, Model model, BindingResult bindingResult, ApplicantSectionResource applicantSection) {
//        OpenSectionViewModel openSectionViewModel = new OpenSectionViewModel();
//        SectionApplicationViewModel sectionApplicationViewModel = new SectionApplicationViewModel();
//
//        addApplicationAndSections(openSectionViewModel, sectionApplicationViewModel, form, applicantSection);
//        if (applicantSection.getSection().getType().equals(OVERVIEW_FINANCES)) {
//            financeOverviewPopulator.addOverviewDetails(openSectionViewModel, model, form, applicantSection);
//        }
//
//        form.setBindingResult(bindingResult);
//        form.setObjectErrors(bindingResult.getAllErrors());
//
//        openSectionViewModel.setNavigationViewModel(addNavigation(applicantSection.getSection(),applicantSection.getApplication().getId()));
//        openSectionViewModel.setSectionApplicationViewModel(sectionApplicationViewModel);
//
//        model.addAttribute(MODEL_ATTRIBUTE_FORM, form);
//
//        return openSectionViewModel;
//    }
//
//    private void addApplicationDetails(OpenSectionViewModel openSectionViewModel, SectionApplicationViewModel sectionApplicationViewModel, ApplicationForm form,
//                                       ApplicantSectionResource applicantSection) {
//
//        form = initializeApplicationForm(form);
//        form.setApplication(applicantSection.getApplication());
//
//        addQuestionsDetails(openSectionViewModel, applicantSection, form);
//        addUserDetails(openSectionViewModel, applicantSection);
//        addApplicationFormDetailInputs(applicantSection.getApplication(), form);
//
//        addMappedSectionsDetails(openSectionViewModel, applicantSection);
//
//        addCompletedDetails(openSectionViewModel, applicantSection);
//
//        openSectionViewModel.setSectionAssignableViewModel(addAssignableDetails(applicantSection));
//        sectionApplicationViewModel.setAllReadOnly(calculateAllReadOnly(openSectionViewModel, applicantSection));
//        sectionApplicationViewModel.setCurrentApplication(applicantSection.getApplication());
//        sectionApplicationViewModel.setCurrentCompetition(applicantSection.getCompetition());
//        sectionApplicationViewModel.setUserOrganisation(applicantSection.getCurrentApplicant().getOrganisation());
//    }
//
//    private void addOrganisationDetails(OpenSectionViewModel viewModel, ApplicantSectionResource applicantSection) {
//        viewModel.setAcademicOrganisations(applicantSection.allOrganisations()
//                .filter(organisation -> organisation.getOrganisationType().equals(OrganisationTypeEnum.RESEARCH.getId()))
//                .collect(Collectors.toSet()));
//        viewModel.setApplicationOrganisations(applicantSection.allOrganisations().collect(Collectors.toSet()));
//
//        List<String> activeApplicationOrganisationNames = applicantSection.allOrganisations().map(OrganisationResource::getName).collect(Collectors.toList());
//
//        List<String> pendingOrganisationNames = pendingInvitations(applicantSection.getApplication()).stream()
//            .map(ApplicationInviteResource::getInviteOrganisationName)
//            .distinct()
//            .filter(orgName -> StringUtils.hasText(orgName)
//                && activeApplicationOrganisationNames.stream().noneMatch(organisationName -> organisationName.equals(orgName))).collect(Collectors.toList());
//
//        viewModel.setPendingOrganisationNames(pendingOrganisationNames);
//
//        viewModel.setLeadOrganisation(applicantSection.getApplicants().stream()
//                .filter(ApplicantResource::isLead)
//                .map(ApplicantResource::getOrganisation)
//                .findAny().orElse(null));
//    }
//
//
//    private List<ApplicationInviteResource> pendingInvitations(ApplicationResource application) {
//        RestResult<List<InviteOrganisationResource>> pendingAssignableUsersResult = inviteRestService.getInvitesByApplication(application.getId());
//
//        return pendingAssignableUsersResult.handleSuccessOrFailure(
//            failure -> new ArrayList<>(0),
//            success -> success.stream().flatMap(item -> item.getInviteResources().stream())
//                .filter(item -> !InviteStatus.OPENED.equals(item.getStatus()))
//                .collect(Collectors.toList()));
//    }
//
//    private void addCompletedDetails(OpenSectionViewModel openSectionViewModel, ApplicantSectionResource applicantSection) {
//        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(applicantSection.getApplication().getId());
//        Set<Long> sectionsMarkedAsComplete = convertToCombinedMarkedAsCompleteSections(completedSectionsByOrganisation);
//
//        Optional<ApplicantSectionResource> optionalFinanceSection = applicantSection.allSections().filter(section -> section.getSection().getType().equals(FINANCE)).findAny();
//        Optional<Long> optionalFinanceSectionId = optionalFinanceSection.map(applicantSectionResource -> applicantSectionResource.getSection().getId());
//
//
//        addSectionsMarkedAsComplete(openSectionViewModel, applicantSection);
//        openSectionViewModel.setCompletedSectionsByOrganisation(completedSectionsByOrganisation);
//        openSectionViewModel.setSectionsMarkedAsComplete(sectionsMarkedAsComplete);
//        openSectionViewModel.setHasFinanceSection(optionalFinanceSection.isPresent());
//        openSectionViewModel.setFinanceSectionId(optionalFinanceSectionId.orElse(null));
//        openSectionViewModel.setEachCollaboratorFinanceSectionId(optionalFinanceSectionId.orElse(null));
//    }
//
//    private Set<Long> convertToCombinedMarkedAsCompleteSections(Map<Long, Set<Long>> completedSectionsByOrganisation) {
//        Set<Long> combinedMarkedAsComplete = new HashSet<>();
//
//        completedSectionsByOrganisation.forEach((organisationId, completedSections) -> combinedMarkedAsComplete.addAll(completedSections));
//        completedSectionsByOrganisation.forEach((key, values) -> combinedMarkedAsComplete.retainAll(values));
//
//        return combinedMarkedAsComplete;
//    }
//
//
//    private void addApplicationAndSections(OpenSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel,
//                                           ApplicationForm form, ApplicantSectionResource applicantSection) {
//
//        addOrganisationDetails(viewModel, applicantSection);
//        addApplicationDetails(viewModel, sectionApplicationViewModel, form, applicantSection);
//        addSectionDetails(viewModel, applicantSection);
//
//        viewModel.setCompletedQuestionsPercentage(applicantSection.getApplication().getCompletion() == null ? 0 : applicantSection.getApplication().getCompletion().intValue());
        return null;
    }

}
