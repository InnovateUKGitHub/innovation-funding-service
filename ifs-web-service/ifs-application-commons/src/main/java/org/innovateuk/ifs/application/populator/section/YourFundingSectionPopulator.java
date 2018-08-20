package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.YourFundingSectionViewModel;
import org.innovateuk.ifs.form.ApplicationForm;
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

import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

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
    private QuestionRestService questionRestService;

    @Autowired
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Override
    protected void populateNoReturn(ApplicantSectionResource section, ApplicationForm form, YourFundingSectionViewModel viewModel, Model model, BindingResult bindingResult, Boolean readOnly, Optional<Long> applicantOrganisationId) {
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());
        viewModel.setComplete(completedSectionIds.contains(section.getSection().getId()));

        Long researchCategoryQuestionId = getResearchCategoryQuestionId(section);
        boolean researchCategoryRequired = researchCategoryQuestionId != null && !isResearchCategoryComplete
                (section, researchCategoryQuestionId);
        viewModel.setResearchCategoryQuestionId(researchCategoryQuestionId);
        viewModel.setResearchCategoryRequired(researchCategoryRequired);

        long yourOrganisationSectionId = getYourOrganisationSectionId(section);
        boolean yourOrganisationRequired = !completedSectionIds.contains(yourOrganisationSectionId);
        viewModel.setYourOrganisationSectionId(yourOrganisationSectionId);
        viewModel.setYourOrganisationRequired(yourOrganisationRequired);
    }

    private boolean isResearchCategoryComplete(ApplicantSectionResource section, long questionId) {
        long applicationId = section.getApplication().getId();
        long applicantOrganisationId = section.getCurrentApplicant().getOrganisation().getId();

        return questionIsComplete(applicationId, applicantOrganisationId, questionId);
    }

    private boolean questionIsComplete(long applicationId, long organisationId, long questionId) {
        Map<Long, QuestionStatusResource> questionStatuses = questionService.getQuestionStatusesForApplicationAndOrganisation(applicationId, organisationId);
        QuestionStatusResource questionStatus = questionStatuses.get(questionId);
        return questionStatus != null && questionStatus.getMarkedAsComplete();
    }

    private Long getResearchCategoryQuestionId(ApplicantSectionResource section) {
        return questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(section.getCompetition().getId(),
                RESEARCH_CATEGORY).handleSuccessOrFailure(failure -> null, QuestionResource::getId);
    }

    private long getYourOrganisationSectionId(ApplicantSectionResource section) {
        SectionResource yourOrganisationSection = sectionService.getOrganisationFinanceSection(section.getCompetition().getId());
        return yourOrganisationSection.getId();
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
