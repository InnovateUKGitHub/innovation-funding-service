package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.YourFinancesSectionViewModel;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * Your finances populator section view models.
 */
@Component
public class YourFinancesSectionPopulator extends AbstractSectionPopulator<YourFinancesSectionViewModel> {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Override
    protected YourFinancesSectionViewModel createNew(ApplicantSectionResource applicantSection, ApplicationForm form, Boolean readOnly, Optional<Long> applicantOrganisationId, Boolean readOnlyAllApplicantApplicationFinances) {
        return new YourFinancesSectionViewModel(applicantSection, Collections.emptyList(), getNavigationViewModel(applicantSection), readOnly, applicantOrganisationId, readOnlyAllApplicantApplicationFinances);
    }

    @Override
    public void populateNoReturn(ApplicantSectionResource section,
                                 ApplicationForm form,
                                 YourFinancesSectionViewModel viewModel,
                                 Model model,
                                 BindingResult bindingResult,
                                 Boolean readOnly,
                                 Optional<Long> applicantOrganisationId) {
        ApplicantSectionResource yourFunding = findChildSectionByType(section, SectionType.FUNDING_FINANCES);
        ApplicantSectionResource yourOrganisation = findChildSectionByType(section, SectionType.ORGANISATION_FINANCES);
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());

        boolean yourFundingComplete = completedSectionIds.contains(yourFunding.getSection().getId());
        boolean yourOrganisationComplete = completedSectionIds.contains(yourOrganisation.getSection().getId());

        initializeApplicantFinances(section);
        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(financeService, fileEntryRestService, section.getApplication().getId());
        BaseFinanceResource organisationFinances = organisationFinanceOverview.getFinancesByOrganisation().get(section.getCurrentApplicant().getOrganisation().getId());

        viewModel.setNotRequestingFunding(yourFundingComplete && yourOrganisationComplete && organisationFinances.getGrantClaimPercentage() != null && organisationFinances.getGrantClaimPercentage() == 0);
        viewModel.setCompletedSectionIds(completedSectionIds);
        viewModel.setYourOrganisationSectionId(yourOrganisation.getSection().getId());
        viewModel.setOrganisationFinance(organisationFinances);
        determineYourFundingUnlocked(viewModel, applicantOrganisationId, yourOrganisationComplete);
    }

    /**
     * TODO: IFS-3753 remove all related to applicationDetails
     * @param viewModel
     * @param applicantOrganisationId
     * @param yourOrganisationComplete
     */
    private void determineYourFundingUnlocked(YourFinancesSectionViewModel viewModel,
                                              Optional<Long> applicantOrganisationId,
                                              boolean yourOrganisationComplete) {
        QuestionResource yourFundingDependencyQuestion;
        if (viewModel.getCompetition().getUseNewApplicantMenu()) {
            yourFundingDependencyQuestion = questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(viewModel.getCompetition().getId(), RESEARCH_CATEGORY).getSuccess();
            viewModel.setResearchCategoryQuestionId(yourFundingDependencyQuestion.getId());
        } else {
            yourFundingDependencyQuestion = questionRestService.getQuestionByCompetitionIdAndFormInputType(viewModel.getCompetition().getId(), FormInputType.APPLICATION_DETAILS).getSuccess();
            viewModel.setApplicationDetailsQuestionId(yourFundingDependencyQuestion.getId());
        }

        Long userOrganisationId = determineUserOrganisationId(viewModel, applicantOrganisationId);
        boolean yourFundingDependencyQuestionComplete = questionIsComplete(viewModel.getApplication().getId(), userOrganisationId, yourFundingDependencyQuestion);
        viewModel.setFundingSectionLocked(!(yourOrganisationComplete && yourFundingDependencyQuestionComplete));
    }

    private void initializeApplicantFinances(ApplicantSectionResource section) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(section.getCurrentUser().getId(), section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());
        if (applicationFinanceResource == null) {
            financeService.addApplicationFinance(section.getCurrentUser().getId(), section.getApplication().getId());
        }
    }

    private boolean questionIsComplete(Long applicationId, Long userOrganisationId, QuestionResource question) {
        Map<Long, QuestionStatusResource> questionStatuses = questionService.getQuestionStatusesForApplicationAndOrganisation(applicationId, userOrganisationId);
        QuestionStatusResource questionStatus = questionStatuses.get(question.getId());
        return questionStatus != null && questionStatus.getMarkedAsComplete();
    }

    private Long determineUserOrganisationId(YourFinancesSectionViewModel viewModel,
                                             Optional<Long> applicantOrganisationId) {
        if (applicantOrganisationId.isPresent()) {
            return applicantOrganisationId.get();
        }
        return viewModel.getApplicantResource().getCurrentApplicant().getOrganisation().getId();
    }

    private ApplicantSectionResource findChildSectionByType(ApplicantSectionResource section, SectionType sectionType) {
        return section.getApplicantChildrenSections().stream().filter(child -> child.getSection().getType().equals(sectionType)).findAny().get();
    }

    @Override
    public SectionType getSectionType() {
        return SectionType.FINANCE;
    }
}

