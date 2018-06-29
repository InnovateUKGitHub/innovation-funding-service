package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.YourFundingSectionViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Your funding populator section view models.
 */
@Component
public class YourFundingSectionPopulator extends AbstractSectionPopulator<YourFundingSectionViewModel> {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Override
    protected void populateNoReturn(ApplicantSectionResource section, ApplicationForm form, YourFundingSectionViewModel viewModel, Model model, BindingResult bindingResult, Boolean readOnly, Optional<Long> applicantOrganisationId) {
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());
        viewModel.setComplete(completedSectionIds.contains(section.getSection().getId()));

        // Values needed for speedbump page
        long researchCategoryQuestionId = getResearchCategoryQuestionId(section);
        viewModel.setResearchCategoryQuestionId(researchCategoryQuestionId);
        viewModel.setResearchCategoryComplete(isResearchCategoryComplete(section, researchCategoryQuestionId));

        long yourOrganisationSectionId = getYourOrganisationSectionId(section);
        boolean isYourOrganisationSectionComplete = completedSectionIds.contains(yourOrganisationSectionId);
        viewModel.setYourOrganisationSectionId(yourOrganisationSectionId);
        viewModel.setYourOrganisationComplete(isYourOrganisationSectionComplete);
    }

    boolean isResearchCategoryComplete(ApplicantSectionResource section, long questionId) {

        long applicationId = section.getApplication().getId();
        long applicantOrganisationId = section.getCurrentApplicant().getOrganisation().getId();

        return questionIsComplete(applicationId, applicantOrganisationId, questionId);
    }

    private boolean questionIsComplete(long applicationId, long organisationId, long questionId) {
        Map<Long, QuestionStatusResource> questionStatuses = questionService.getQuestionStatusesForApplicationAndOrganisation(applicationId, organisationId);
        QuestionStatusResource questionStatus = questionStatuses.get(questionId);
        return questionStatus != null && questionStatus.getMarkedAsComplete();
    }

    boolean isSectionComplete(long sectionId, List<Long> completedSectionIds) {

        return completedSectionIds.contains(sectionId);
    }

    long getResearchCategoryQuestionId(ApplicantSectionResource section) {

        if (isNewResearchCategoryView(section)) {
            // TODO - get the research category page id
            return 222L;
        } else {
            // If it's an old-style competition, use the application details page
            QuestionResource applicationDetailsQuestion = questionService.getQuestionByCompetitionIdAndFormInputType(section.getCompetition().getId(), FormInputType.APPLICATION_DETAILS).getSuccess();
            return applicationDetailsQuestion.getId();
        }
    }

    long getYourOrganisationSectionId(ApplicantSectionResource section) {
        SectionResource yourOrganisationSection = sectionService.getOrganisationFinanceSection(section.getCompetition().getId());
        return yourOrganisationSection.getId();
    }

    private boolean isNewResearchCategoryView(ApplicantSectionResource section) {
        // TODO: get this info from the competition
        return false;
    }

    @Override
    protected YourFundingSectionViewModel createNew(ApplicantSectionResource section, ApplicationForm form, Boolean readOnly, Optional<Long> applicantOrganisationId, Boolean readOnlyAllApplicantApplicationFinances) {
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());
        return new YourFundingSectionViewModel(
                section,
                formInputViewModelGenerator.fromSection(section, section, form, readOnly),
                getNavigationViewModel(section),
                readOnly || completedSectionIds.contains(section.getSection().getId()),
                applicantOrganisationId,
                readOnlyAllApplicantApplicationFinances);
    }

    @Override
    public SectionType getSectionType() {
        return SectionType.FUNDING_FINANCES;
    }

}
