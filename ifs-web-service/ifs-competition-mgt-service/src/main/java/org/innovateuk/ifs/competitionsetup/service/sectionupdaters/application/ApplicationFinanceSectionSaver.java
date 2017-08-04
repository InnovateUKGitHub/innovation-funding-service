package org.innovateuk.ifs.competitionsetup.service.sectionupdaters.application;

import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupFinanceService;
import org.innovateuk.ifs.competitionsetup.service.sectionupdaters.AbstractSectionSaver;
import org.innovateuk.ifs.competitionsetup.service.sectionupdaters.CompetitionSetupSubsectionSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection.FINANCES;

/**
 * Competition setup section saver for the application -> finance form sub-section.
 */
@Service
public class ApplicationFinanceSectionSaver extends AbstractSectionSaver implements CompetitionSetupSubsectionSaver {

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
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
        ApplicationFinanceForm form = (ApplicationFinanceForm) competitionSetupForm;
        CompetitionSetupFinanceResource compSetupFinanceRes = new CompetitionSetupFinanceResource();
        // INFUND-6773 - Not allowed to at this moment
        compSetupFinanceRes.setFullApplicationFinance(true);
        compSetupFinanceRes.setIncludeGrowthTable(form.isIncludeGrowthTable());
        compSetupFinanceRes.setCompetitionId(competition.getId());

        updateFundingRulesQuestion(form.getFundingRules(), competition.getId());
        return competitionSetupFinanceService.updateFinance(compSetupFinanceRes);
    }

    private void updateFundingRulesQuestion(String fundingRules, Long competitionId) {
        Optional<QuestionResource> question = questionService.getQuestionsBySectionIdAndType(getOverviewFinancesSectionId(competitionId), QuestionType.GENERAL).stream()
                .filter(questionResource -> questionResource.getName() == null)
                .findFirst();

        question.ifPresent(questionResource -> {
                    questionResource.setDescription(fundingRules);
                    questionService.save(questionResource);
                }
        );
    }

    private Long getOverviewFinancesSectionId(Long competitionId) {
        Optional<SectionResource> section = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.OVERVIEW_FINANCES).stream().findFirst();
        if (section.isPresent()) {
            return section.get().getId();
        }

        return null;
    }


    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return ApplicationFinanceForm.class.equals(clazz);
    }

}
