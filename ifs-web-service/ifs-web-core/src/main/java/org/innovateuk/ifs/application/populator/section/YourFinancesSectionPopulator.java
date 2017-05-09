package org.innovateuk.ifs.application.populator.section;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.YourFinancesSectionViewModel;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestServiceImpl;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class YourFinancesSectionPopulator extends AbstractSectionPopulator<YourFinancesSectionViewModel> {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ApplicationFinanceRestServiceImpl applicationFinanceRestService;

    @Override
    protected YourFinancesSectionViewModel createNew(ApplicantSectionResource applicantSection, ApplicationForm form) {
        return new YourFinancesSectionViewModel(applicantSection, Collections.emptyList(), getNavigationViewModel(applicantSection), true);
    }

    @Override
    public void populate(ApplicantSectionResource section, ApplicationForm form, YourFinancesSectionViewModel viewModel, Model model, BindingResult bindingResult) {
        QuestionResource applicationDetailsQuestion = questionService.getQuestionByCompetitionIdAndFormInputType(viewModel.getCompetition().getId(), FormInputType.APPLICATION_DETAILS).getSuccessObjectOrThrowException();
        ApplicantSectionResource yourOrganisation = findChildSectionByType(section, SectionType.ORGANISATION_FINANCES);
        ApplicantSectionResource yourFunding = findChildSectionByType(section, SectionType.FUNDING_FINANCES);
        List<Long> completedSectionIds = sectionService.getCompleted(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId());

        boolean yourOrganisationComplete = completedSectionIds.contains(yourOrganisation.getSection().getId());
        boolean yourFundingComplete = completedSectionIds.contains(yourFunding.getSection().getId());
        boolean applicationDetailsComplete = applicationDetailsIsComplete(viewModel, applicationDetailsQuestion);

        ApplicationFinanceResource applicantFinances = applicationFinanceRestService.getFinanceDetails(section.getApplication().getId(), section.getCurrentApplicant().getOrganisation().getId()).getSuccessObjectOrThrowException();
        viewModel.setNotRequestingFunding(yourFundingComplete && yourOrganisationComplete && applicantFinances.getGrantClaimPercentage() != null && applicantFinances.getGrantClaimPercentage() == 0);
        viewModel.setFundingSectionLocked(!(yourOrganisationComplete && applicationDetailsComplete));
        viewModel.setCompletedSectionIds(completedSectionIds);
        viewModel.setYourOrganisationSectionId(yourOrganisation.getSection().getId());
        viewModel.setApplicationDetailsQuestionId(applicationDetailsQuestion.getId());
        viewModel.setOrganisationFinance(applicantFinances);
    }

    private boolean applicationDetailsIsComplete(YourFinancesSectionViewModel viewModel, QuestionResource applicationDetailsQuestion) {
        Map<Long, QuestionStatusResource> questionStatuses = questionService.getQuestionStatusesForApplicationAndOrganisation(viewModel.getApplication().getId(), viewModel.getApplicantResource().getCurrentApplicant().getOrganisation().getId());
        QuestionStatusResource applicationDetailsStatus = questionStatuses.get(applicationDetailsQuestion.getId());
        return applicationDetailsStatus != null && applicationDetailsStatus.getMarkedAsComplete();
    }

    private ApplicantSectionResource findChildSectionByType(ApplicantSectionResource section, SectionType sectionType) {
        return section.getApplicantChildrenSections().stream().filter(child -> child.getSection().getType().equals(sectionType)).findAny().orElse(null);
    }

    @Override
    public SectionType getSectionType() {
        return SectionType.FINANCE;
    }
}

