package org.innovateuk.ifs.competitionsetup.service.formpopulator.application;

import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupFinanceService;
import org.innovateuk.ifs.competitionsetup.service.formpopulator.CompetitionSetupSubsectionFormPopulator;
import org.innovateuk.ifs.setup.resource.ApplicationFinanceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Form populator for the Finances sub-section under the Application form of competition setup section.
 */
@Service
public class ApplicationFinanceFormPopulator implements CompetitionSetupSubsectionFormPopulator {

	@Autowired
	private CompetitionSetupFinanceService competitionSetupFinanceService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private QuestionService questionService;

	@Override
	public CompetitionSetupSubsection sectionToFill() {
		return CompetitionSetupSubsection.FINANCES;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId) {
        ApplicationFinanceForm competitionSetupForm = new ApplicationFinanceForm();

        if (competitionResource.isFinanceType()) {
            CompetitionSetupFinanceResource competitionSetupFinanceResource = competitionSetupFinanceService.getByCompetitionId(competitionResource.getId());

            competitionSetupForm.setApplicationFinanceType(getFinanceType(competitionSetupFinanceResource.isFullApplicationFinance()));
            competitionSetupForm.setIncludeGrowthTable(competitionSetupFinanceResource.isIncludeGrowthTable());
            competitionSetupForm.setFundingRules(getFundingRulesWithoutHeading(competitionResource.getId()));
        } else {
            competitionSetupForm.setApplicationFinanceType(ApplicationFinanceType.NONE);
        }

		return competitionSetupForm;
	}

    private ApplicationFinanceType getFinanceType(boolean fullApplicationFinance) {
        return fullApplicationFinance ? ApplicationFinanceType.FULL : ApplicationFinanceType.LIGHT;
    }

    private String getFundingRulesWithoutHeading(Long competitionId) {
        Optional<QuestionResource> question = questionService.getQuestionsBySectionIdAndType(getOverviewFinancesSectionId(competitionId), QuestionType.GENERAL).stream()
                .filter(questionResource -> questionResource.getName() == null)
                .findFirst();

        if(question.isPresent()) {
           return question.get().getDescription();
        }

        return null;
    }

    private Long getOverviewFinancesSectionId(Long competitionId) {
        Optional<SectionResource> section = sectionService.getSectionsForCompetitionByType(competitionId, SectionType.OVERVIEW_FINANCES).stream().findFirst();
        if(section.isPresent()) {
            return section.get().getId();
        }

        return null;
    }


}
