package org.innovateuk.ifs.application;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionApplicationViewModel;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class for populating the model for the "Your Finances" section
 */
@Component
public abstract class BaseOpenFinanceSectionModelPopulator extends BaseSectionModelPopulator {
    public static final String MODEL_ATTRIBUTE_FORM = "form";

    protected void populateSubSectionMenuOptions(OpenFinanceSectionViewModel viewModel, final List<SectionResource> allSections, Long userOrganisationId, Integer organisationGrantClaimPercentage) {

        boolean organisationSizeComplete = false;
        if (viewModel.getSectionsMarkedAsComplete() != null) {
            organisationSizeComplete = viewModel.getSectionsMarkedAsComplete().contains(allSections.stream().filter(filterSection -> SectionType.ORGANISATION_FINANCES.equals(filterSection.getType())).map(SectionResource::getId).findFirst().orElse(-1L));
        }

        boolean yourFundingComplete = false;
        if (viewModel.getSectionsMarkedAsComplete() != null) {
            yourFundingComplete = viewModel.getSectionsMarkedAsComplete().contains(allSections.stream().filter(filterSection -> SectionType.FUNDING_FINANCES.equals(filterSection.getType())).map(SectionResource::getId).findFirst().orElse(-1L));
        }
        viewModel.setNotRequestingFunding(yourFundingComplete && organisationSizeComplete && organisationGrantClaimPercentage != null && organisationGrantClaimPercentage == 0);

    }

    protected Boolean isSubFinanceSection(SectionResource section) {
        return SectionType.FINANCE.equals(section.getType().getParent().orElse(null));
    }

    private void addApplicationDetails(OpenFinanceSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel,
                                       ApplicationForm form, ApplicantSectionResource applicantSection) {

        form = initializeApplicationForm(form);
        form.setApplication(applicantSection.getApplication());

        //Parent finance section has no assignable or question details.
        if (!SectionType.FINANCE.equals(applicantSection.getSection().getType())) {
            addQuestionsDetails(viewModel, applicantSection, form);
        }
        addUserDetails(viewModel, applicantSection);
        addMappedSectionsDetails(viewModel, applicantSection);

        if (!SectionType.FINANCE.equals(applicantSection.getSection().getType())) {
            viewModel.setSectionAssignableViewModel(addAssignableDetails(applicantSection));
        }
        addCompletedDetails(sectionApplicationViewModel, applicantSection);

        sectionApplicationViewModel.setUserOrganisation(applicantSection.getCurrentApplicant().getOrganisation());
    }

    private void addCompletedDetails(SectionApplicationViewModel sectionApplicationViewModel, ApplicantSectionResource applicantSection) {
        Set<Long> markedAsComplete = applicantSection.allCompleteQuestionStatuses()
                .filter(status -> status.getMarkedAsCompleteBy().hasSameOrganisation(applicantSection.getCurrentApplicant())
                        && status.getStatus().getMarkedAsComplete())
                .map(status -> status.getStatus().getQuestion())
                .collect(Collectors.toSet());
        sectionApplicationViewModel.setMarkedAsComplete(markedAsComplete);
    }

    protected void addApplicationAndSections(OpenFinanceSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel,
                                             ApplicationForm form, ApplicantSectionResource applicantSection) {
        addSectionsMarkedAsComplete(viewModel, applicantSection);
        addApplicationDetails(viewModel, sectionApplicationViewModel, form, applicantSection);

        addSectionDetails(viewModel, applicantSection);
    }

    protected void addFundingSection(OpenFinanceSectionViewModel viewModel, ApplicantSectionResource applicantSection) {
        viewModel.setFundingSection(applicantSection.allSections().filter(applicantSectionResource -> applicantSection.getSection().getType().equals(SectionType.FUNDING_FINANCES)).map(ApplicantSectionResource::getSection).findAny().orElse(null));
    }
}