package org.innovateuk.ifs.application;

import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionApplicationViewModel;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.Form;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Class for creating the model for the open section page.
 * These are rendered in the ApplicationFormController.applicationFormWithOpenSection method
 */
@Component
public class OpenSectionModelPopulator extends BaseSectionModelPopulator {

    private SectionService sectionService;
    private FinanceOverviewPopulator financeOverviewPopulator;
    private InviteService inviteService;

    public OpenSectionModelPopulator(SectionService sectionService,
                                     FinanceOverviewPopulator financeOverviewPopulator,
                                     InviteService inviteService) {
        this.sectionService = sectionService;
        this.financeOverviewPopulator = financeOverviewPopulator;
        this.inviteService = inviteService;
    }

    @Override
    public BaseSectionViewModel populateModel(ApplicationForm form, Model model, BindingResult bindingResult, ApplicantSectionResource applicantSection) {
        OpenSectionViewModel openSectionViewModel = new OpenSectionViewModel();
        SectionApplicationViewModel sectionApplicationViewModel = new SectionApplicationViewModel();

        addApplicationAndSections(openSectionViewModel, sectionApplicationViewModel, form, applicantSection);
        if (applicantSection.getSection().getType().equals(SectionType.OVERVIEW_FINANCES)) {
            financeOverviewPopulator.addOverviewDetails(openSectionViewModel, model, form, applicantSection);
        }

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

        addCompletedDetails(openSectionViewModel, applicantSection);

        openSectionViewModel.setSectionAssignableViewModel(addAssignableDetails(applicantSection));
        sectionApplicationViewModel.setAllReadOnly(calculateAllReadOnly(openSectionViewModel, applicantSection));
        sectionApplicationViewModel.setCurrentApplication(applicantSection.getApplication());
        sectionApplicationViewModel.setCurrentCompetition(applicantSection.getCompetition());
        sectionApplicationViewModel.setUserOrganisation(applicantSection.getCurrentApplicant().getOrganisation());
    }

    private void addApplicationFormDetailInputs(ApplicationResource application, Form form) {
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

    private void addOrganisationDetails(OpenSectionViewModel viewModel, ApplicantSectionResource applicantSection) {

        OrganisationResource leadOrganisation = applicantSection.getApplicants().stream()
                .filter(ApplicantResource::isLead)
                .map(ApplicantResource::getOrganisation)
                .findAny().orElse(null);

        final Comparator<OrganisationResource> comparator;

        if (leadOrganisation != null) {
            comparator = Comparator.comparing(organisationResource -> leadOrganisation.getId().equals(organisationResource.getId()), Comparator.reverseOrder());
        } else {
            comparator = Comparator.comparingLong(OrganisationResource::getId);
        }

        final Supplier<TreeSet<OrganisationResource>> supplier = () -> new TreeSet<>(comparator);

        viewModel.setAcademicOrganisations(applicantSection.allOrganisations()
                .filter(organisation -> organisation.getOrganisationType().equals(OrganisationTypeEnum.RESEARCH.getId()))
                .collect(Collectors.toCollection(supplier)));
        viewModel.setApplicationOrganisations(applicantSection.allOrganisations()
                .collect(Collectors.toCollection(supplier)));

        List<String> activeApplicationOrganisationNames = applicantSection.allOrganisations().map(OrganisationResource::getName).collect(Collectors.toList());

        List<String> pendingOrganisationNames = inviteService.getPendingInvitationsByApplicationId(applicantSection.getApplication().getId()).stream()
            .map(ApplicationInviteResource::getInviteOrganisationName)
            .distinct()
            .filter(orgName -> StringUtils.hasText(orgName)
                && activeApplicationOrganisationNames.stream().noneMatch(organisationName -> organisationName.equals(orgName))).collect(Collectors.toList());

        viewModel.setPendingOrganisationNames(pendingOrganisationNames);

        viewModel.setLeadOrganisation(leadOrganisation);
    }

    private void addCompletedDetails(OpenSectionViewModel openSectionViewModel, ApplicantSectionResource applicantSection) {
        Map<Long, Set<Long>> completedSectionsByOrganisation = sectionService.getCompletedSectionsByOrganisation(applicantSection.getApplication().getId());
        Set<Long> sectionsMarkedAsComplete = convertToCombinedMarkedAsCompleteSections(completedSectionsByOrganisation);

        List<SectionResource> financeSections = sectionService.getSectionsForCompetitionByType(applicantSection.getCompetition().getId(), SectionType.FINANCE);
        Optional<SectionResource> optionalFinanceSection = financeSections.stream().findAny();
        Optional<Long> optionalFinanceSectionId = optionalFinanceSection.map(SectionResource::getId);

        addSectionsMarkedAsComplete(openSectionViewModel, applicantSection);
        openSectionViewModel.setCompletedSectionsByOrganisation(completedSectionsByOrganisation);
        openSectionViewModel.setSectionsMarkedAsComplete(sectionsMarkedAsComplete);
        openSectionViewModel.setHasFinanceSection(optionalFinanceSection.isPresent());
        openSectionViewModel.setFinanceSectionId(optionalFinanceSectionId.orElse(null));
        openSectionViewModel.setEachCollaboratorFinanceSectionId(optionalFinanceSectionId.orElse(null));
        if (optionalFinanceSection.isPresent()) {
            boolean yourFinancesCompleteForAllOrganisations = getYourFinancesCompleteForAllOrganisations(
                    completedSectionsByOrganisation, optionalFinanceSectionId.get());
            openSectionViewModel.setYourFinancesCompleteForAllOrganisations(yourFinancesCompleteForAllOrganisations);
        }
    }

    private boolean getYourFinancesCompleteForAllOrganisations(Map<Long, Set<Long>> completedSectionsByOrganisation,
                                                               Long financeSectionId) {
        return completedSectionsByOrganisation.keySet()
                .stream()
                .noneMatch(id -> !completedSectionsByOrganisation.get(id).contains(financeSectionId));
    }

    private Set<Long> convertToCombinedMarkedAsCompleteSections(Map<Long, Set<Long>> completedSectionsByOrganisation) {
        Set<Long> combinedMarkedAsComplete = new HashSet<>();

        completedSectionsByOrganisation.forEach((organisationId, completedSections) -> combinedMarkedAsComplete.addAll(completedSections));
        completedSectionsByOrganisation.forEach((key, values) -> combinedMarkedAsComplete.retainAll(values));

        return combinedMarkedAsComplete;
    }


    private void addApplicationAndSections(OpenSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel,
                                           ApplicationForm form, ApplicantSectionResource applicantSection) {

        addOrganisationDetails(viewModel, applicantSection);
        addApplicationDetails(viewModel, sectionApplicationViewModel, form, applicantSection);
        addSectionDetails(viewModel, applicantSection);

        viewModel.setCompletedQuestionsPercentage(applicantSection.getApplication().getCompletion() == null ? 0 : applicantSection.getApplication().getCompletion().intValue());
    }

}
