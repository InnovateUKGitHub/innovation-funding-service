package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class for populating the model for the "Your Finances" section
 */
@Component
public abstract class BaseOpenFinanceSectionModelPopulator extends BaseSectionModelPopulator {
    public static final String MODEL_ATTRIBUTE_FORM = "form";

    @Autowired
    private QuestionService questionService;

//    protected void populateSubSectionMenuOptions(OpenFinanceSectionViewModel viewModel, final List<SectionResource> allSections, Long userOrganisationId, Integer organisationGrantClaimPercentage) {
//        QuestionResource applicationDetailsQuestion = questionService.getQuestionByCompetitionIdAndFormInputType(viewModel.getApplication().getCurrentApplication().getCompetition(), FormInputType.APPLICATION_DETAILS).getSuccessObjectOrThrowException();
//        Map<Long, QuestionStatusResource> questionStatuses = questionService.getQuestionStatusesForApplicationAndOrganisation(viewModel.getApplication().getCurrentApplication().getId(), userOrganisationId);
//        QuestionStatusResource applicationDetailsStatus = questionStatuses.get(applicationDetailsQuestion.getId());
//
//        boolean organisationSizeComplete = false;
//        if (viewModel.getSectionsMarkedAsComplete() != null) {
//            organisationSizeComplete = viewModel.getSectionsMarkedAsComplete().contains(allSections.stream().filter(filterSection -> SectionType.ORGANISATION_FINANCES.equals(filterSection.getType())).map(SectionResource::getId).findFirst().orElse(-1L));
//        }
//        boolean applicationDetailsComplete = applicationDetailsStatus != null && applicationDetailsStatus.getMarkedAsComplete();
//
//        viewModel.setFundingSectionLocked(!(organisationSizeComplete && applicationDetailsComplete));
//        viewModel.setApplicationDetailsQuestionId(applicationDetailsQuestion.getId());
//        viewModel.setYourOrganisationSectionId(allSections.stream().filter(filterSection -> SectionType.ORGANISATION_FINANCES.equals(filterSection.getType())).findFirst().map(SectionResource::getId).orElse(null));
//
//
//        boolean yourFundingComplete = false;
//        if (viewModel.getSectionsMarkedAsComplete() != null) {
//            yourFundingComplete = viewModel.getSectionsMarkedAsComplete().contains(allSections.stream().filter(filterSection -> SectionType.FUNDING_FINANCES.equals(filterSection.getType())).map(SectionResource::getId).findFirst().orElse(-1L));
//        }
//        viewModel.setNotRequestingFunding(yourFundingComplete && organisationSizeComplete && organisationGrantClaimPercentage != null && organisationGrantClaimPercentage == 0);
//    }
//
//
//    protected Boolean isSubFinanceSection(SectionResource section) {
//        return SectionType.FINANCE.equals(section.getType().getParent().orElse(null));
//    }
//
//    private void addApplicationDetails(OpenFinanceSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel,
//                                       ApplicationForm form, ApplicantSectionResource applicantSection) {
//
//        form = initializeApplicationForm(form);
//        form.setApplication(applicantSection.getApplication());
//
//        //Parent finance section has no assignable or question details.
//        if (!SectionType.FINANCE.equals(applicantSection.getSection().getType())) {
//            addQuestionsDetails(viewModel, applicantSection, form);
//        }
//        addUserDetails(viewModel, applicantSection);
//        addMappedSectionsDetails(viewModel, applicantSection);
//
//        if (!SectionType.FINANCE.equals(applicantSection.getSection().getType())) {
//            viewModel.setSectionAssignableViewModel(addAssignableDetails(applicantSection));
//        }
//        addCompletedDetails(sectionApplicationViewModel, applicantSection);
//
//        sectionApplicationViewModel.setUserOrganisation(applicantSection.getCurrentApplicant().getOrganisation());
//    }
//
//    private void addCompletedDetails(SectionApplicationViewModel sectionApplicationViewModel, ApplicantSectionResource applicantSection) {
//        Set<Long> markedAsComplete = applicantSection.allCompleteQuestionStatuses()
//                .filter(status -> status.getMarkedAsCompleteBy().hasSameOrganisation(applicantSection.getCurrentApplicant())
//                 && status.getStatus().getMarkedAsComplete())
//                .map(status -> status.getStatus().getQuestion())
//                .collect(Collectors.toSet());
//        sectionApplicationViewModel.setMarkedAsComplete(markedAsComplete);
//    }
//
//    protected void addApplicationAndSections(OpenFinanceSectionViewModel viewModel, SectionApplicationViewModel sectionApplicationViewModel,
//                                             ApplicationForm form, ApplicantSectionResource applicantSection) {
//        addSectionsMarkedAsComplete(viewModel, applicantSection);
//        addApplicationDetails(viewModel, sectionApplicationViewModel, form, applicantSection);
//
//        addSectionDetails(viewModel, applicantSection);
//    }
//
//    protected void addFundingSection(OpenFinanceSectionViewModel viewModel, ApplicantSectionResource applicantSection) {
//        viewModel.setFundingSection(applicantSection.allSections().filter(applicantSectionResource -> applicantSection.getSection().getType().equals(SectionType.FUNDING_FINANCES)).map(ApplicantSectionResource::getSection).findAny().orElse(null));
//    }
}