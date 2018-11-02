package org.innovateuk.ifs.competitionsetup.application.sectionupdater;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionSetupFinanceRestService;
import org.innovateuk.ifs.competitionsetup.application.form.FinanceForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.sectionupdater.CompetitionSetupSubsectionUpdater;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.NO_FINANCES;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection.FINANCES;

/**
 * Competition setup section saver for the application -> finance form sub-section.
 */
@Service
public class FinanceSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSubsectionUpdater {

    @Autowired
    private CompetitionSetupFinanceRestService competitionSetupFinanceRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionRestService questionRestService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return APPLICATION_FORM;
    }

    @Override
    public CompetitionSetupSubsection subsectionToSave() {
        return FINANCES;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition,
                                                CompetitionSetupForm competitionSetupForm) {
        FinanceForm form = (FinanceForm) competitionSetupForm;

        if (competition.isFinanceType()) {
            // TODO IFS-4143 this can be moved within the check for form.getApplicationFinanceType() != NO_FINANCES
            // TODO ...any answer to the funding rules question will then be cleared when NO_FINANCES is selected
            updateFundingRulesQuestion(form.getFundingRules(), competition.getId());
        }

        CompetitionSetupFinanceResource competitionSetupFinanceResource = new CompetitionSetupFinanceResource();
        competitionSetupFinanceResource.setCompetitionId(competition.getId());
        competitionSetupFinanceResource.setApplicationFinanceType(form.getApplicationFinanceType());

        if (form.getApplicationFinanceType() != NO_FINANCES) {
            competitionSetupFinanceResource.setIncludeGrowthTable(form.getIncludeGrowthTable());
            competitionSetupFinanceResource.setIncludeYourOrganisationSection(form.getIncludeYourOrganisationSection());
        }

        return competitionSetupFinanceRestService.save(competitionSetupFinanceResource).toServiceResult();
    }

    private void updateFundingRulesQuestion(String fundingRules, Long competitionId) {
        Optional<QuestionResource> question = questionRestService.getQuestionsBySectionIdAndType(
                getOverviewFinancesSectionId(competitionId),
                QuestionType.GENERAL
        ).getSuccess()
                .stream()
                .filter(questionResource -> questionResource.getName() == null)
                .findFirst();

        question.ifPresent(questionResource -> {
            questionResource.setDescription(fundingRules);
            questionRestService.save(questionResource);
        });
    }

    private Long getOverviewFinancesSectionId(Long competitionId) {
        Optional<SectionResource> section = sectionService.getSectionsForCompetitionByType(
                competitionId,
                SectionType.OVERVIEW_FINANCES
        )
                .stream()
                .findFirst();

        return section.map(SectionResource::getId).orElse(null);
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return FinanceForm.class.equals(clazz);
    }
}
