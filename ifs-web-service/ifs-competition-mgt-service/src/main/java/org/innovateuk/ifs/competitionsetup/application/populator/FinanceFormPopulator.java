package org.innovateuk.ifs.competitionsetup.application.populator;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.service.CompetitionSetupFinanceRestService;
import org.innovateuk.ifs.competitionsetup.application.form.FinanceForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupSubsectionFormPopulator;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Form populator for the Finances sub-section under the Application form of competition setup section.
 */
@Service
public class FinanceFormPopulator implements CompetitionSetupSubsectionFormPopulator {

    private CompetitionSetupFinanceRestService competitionSetupFinanceRestService;
    private SectionService sectionService;
    private QuestionRestService questionRestService;

    public FinanceFormPopulator(CompetitionSetupFinanceRestService competitionSetupFinanceRestService,
                                SectionService sectionService,
                                QuestionRestService questionRestService) {
        this.competitionSetupFinanceRestService = competitionSetupFinanceRestService;
        this.sectionService = sectionService;
        this.questionRestService = questionRestService;
    }

    @Override
    public CompetitionSetupSubsection sectionToFill() {
        return CompetitionSetupSubsection.FINANCES;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId) {
        FinanceForm competitionSetupForm = new FinanceForm();

        CompetitionSetupFinanceResource competitionSetupFinanceResource = competitionSetupFinanceRestService
                .getByCompetitionId(competitionResource.getId()).getSuccess();

        competitionSetupForm.setApplicationFinanceType(competitionSetupFinanceResource.getApplicationFinanceType());
        competitionSetupForm.setIncludeGrowthTable(competitionSetupFinanceResource.getIncludeGrowthTable());

        if (competitionResource.isFinanceType()) {
            competitionSetupForm.setFundingRules(getFundingRulesWithoutHeading(competitionResource.getId()));
        }

        return competitionSetupForm;
    }

    private String getFundingRulesWithoutHeading(Long competitionId) {
        List<QuestionResource> questions = questionRestService.getQuestionsBySectionIdAndType(getOverviewFinancesSectionId(competitionId), QuestionType.GENERAL).getSuccess();
        Optional<QuestionResource> question = questions.stream()
                .filter(questionResource -> questionResource.getName() == null)
                .findFirst();

        return question.map(QuestionResource::getDescription).orElse(null);
    }

    private Long getOverviewFinancesSectionId(Long competitionId) {
        Optional<SectionResource> section = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.OVERVIEW_FINANCES).stream().findFirst();

        return section.map(SectionResource::getId).orElse(null);
    }

}
