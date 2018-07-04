package org.innovateuk.ifs.competitionsetup.application.sectionupdater;

import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.application.form.FinanceForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.sectionupdater.CompetitionSetupSubsectionUpdater;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupFinanceService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection.FINANCES;

/**
 * Competition setup section saver for the application -> finance form sub-section.
 */
@Service
public class FinanceSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSubsectionUpdater {

    @Autowired
    private CompetitionSetupFinanceService competitionSetupFinanceService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return APPLICATION_FORM;
    }

    @Override
    public CompetitionSetupSubsection subsectionToSave() {
        return FINANCES;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(
            CompetitionResource competition,
            CompetitionSetupForm competitionSetupForm
    ) {
        if (competition.isNonFinanceType()) {
            return serviceSuccess();
        } else {
            FinanceForm form = (FinanceForm) competitionSetupForm;
            CompetitionSetupFinanceResource compSetupFinanceRes = new CompetitionSetupFinanceResource();
            // INFUND-6773 - Not allowed to at this moment
            compSetupFinanceRes.setFullApplicationFinance(true);
            compSetupFinanceRes.setIncludeGrowthTable(form.isIncludeGrowthTable());
            compSetupFinanceRes.setCompetitionId(competition.getId());

            updateFundingRulesQuestion(form.getFundingRules(), competition.getId());
            return competitionSetupFinanceService.updateFinance(compSetupFinanceRes);
        }
    }

    private void updateFundingRulesQuestion(String fundingRules, Long competitionId) {
        Optional<QuestionResource> question = questionService.getQuestionsBySectionIdAndType(
                getOverviewFinancesSectionId(competitionId),
                QuestionType.GENERAL
        )
                .stream()
                .filter(questionResource -> questionResource.getName() == null)
                .findFirst();

        question.ifPresent(questionResource -> {
            questionResource.setDescription(fundingRules);
            questionService.save(questionResource);
        });
    }

    private Long getOverviewFinancesSectionId(Long competitionId) {
        Optional<SectionResource> section = sectionService.getSectionsForCompetitionByType(
                competitionId,
                SectionType.OVERVIEW_FINANCES
        )
                .stream()
                .findFirst();

        if (section.isPresent()) {
            return section.get().getId();
        }

        return null;
    }


    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return FinanceForm.class.equals(clazz);
    }

}
