package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.SectionApplicationViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * Class for populating the model for the "Your Finances" section
 */
@Component
public abstract class BaseOpenFinanceSectionModelPopulator extends BaseSectionModelPopulator {
    public static final String MODEL_ATTRIBUTE_FORM = "form";

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRestService questionRestService;

    protected void populateSubSectionMenuOptions(OpenFinanceSectionViewModel viewModel,
                                                 final List<SectionResource> allSections,
                                                 Long userOrganisationId,
                                                 Integer organisationGrantClaimPercentage) {
        boolean organisationSizeComplete = isOrganisationSizeComplete(viewModel.getSectionsMarkedAsComplete(), allSections);

        viewModel.setYourOrganisationSectionId(allSections.stream()
                .filter(filterSection -> SectionType.ORGANISATION_FINANCES.equals(filterSection.getType()))
                .findFirst()
                .map(SectionResource::getId)
                .orElse(null));


        boolean yourFundingComplete = false;
        if (viewModel.getSectionsMarkedAsComplete() != null) {
            yourFundingComplete = viewModel.getSectionsMarkedAsComplete().contains(allSections.stream().filter(filterSection -> SectionType.FUNDING_FINANCES.equals(filterSection.getType())).map(SectionResource::getId).findFirst().orElse(-1L));
        }
        viewModel.setNotRequestingFunding(yourFundingComplete && organisationSizeComplete && organisationGrantClaimPercentage != null && organisationGrantClaimPercentage == 0);
        determineYourFundingUnlocked(viewModel, userOrganisationId, organisationSizeComplete);
    }

    /**
     * TODO: IFS-3753 remove all related to applicationDetails
     * @param viewModel
     * @param userOrganisationId
     * @param organisationSizeComplete
     */
    private void determineYourFundingUnlocked(OpenFinanceSectionViewModel viewModel,
                                              Long userOrganisationId,
                                              boolean organisationSizeComplete) {
        boolean useNewApplicantMenu = viewModel.getApplication().getCurrentApplication().isUseNewApplicantMenu();
        Long competitionId = viewModel.getApplication().getCurrentApplication().getCompetition();

        QuestionResource yourFundingDependencyQuestion;
        if (useNewApplicantMenu) {
            yourFundingDependencyQuestion = questionRestService.getQuestionByCompetitionIdAndFormInputType(competitionId, FormInputType.APPLICATION_DETAILS).getSuccess();
            viewModel.setResearchCategoryQuestionId(yourFundingDependencyQuestion.getId());
        } else {
            yourFundingDependencyQuestion = questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, RESEARCH_CATEGORY).getSuccess();
            viewModel.setApplicationDetailsQuestionId(yourFundingDependencyQuestion.getId());
        }

        boolean yourFundingDependencyQuestionComplete = isQuestionComplete(yourFundingDependencyQuestion,
                viewModel.getApplication().getCurrentApplication().getId(), userOrganisationId);

        viewModel.setFundingSectionLocked(!(organisationSizeComplete && yourFundingDependencyQuestionComplete));
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

    private boolean isQuestionComplete(QuestionResource question,
                                       Long applicationId,
                                       Long userOrganisationId) {
        Map<Long, QuestionStatusResource> questionStatuses = questionService.getQuestionStatusesForApplicationAndOrganisation(applicationId, userOrganisationId);
        QuestionStatusResource applicationDetailsStatus = questionStatuses.get(question.getId());
        return applicationDetailsStatus != null && applicationDetailsStatus.getMarkedAsComplete();
    }

    private boolean isOrganisationSizeComplete(Set<Long> sectionsMarkedAsComplete, List<SectionResource> allSections) {
        if (sectionsMarkedAsComplete != null) {
            return sectionsMarkedAsComplete.contains(
                    allSections.stream()
                            .filter(filterSection -> SectionType.ORGANISATION_FINANCES.equals(filterSection.getType()))
                            .map(SectionResource::getId).findFirst().orElse(-1L));
        }
        return false;
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