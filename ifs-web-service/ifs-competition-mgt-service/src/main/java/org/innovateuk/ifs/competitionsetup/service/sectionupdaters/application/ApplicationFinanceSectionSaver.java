package org.innovateuk.ifs.competitionsetup.service.sectionupdaters.application;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupFinanceService;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.innovateuk.ifs.competitionsetup.service.sectionupdaters.AbstractSectionSaver;
import org.innovateuk.ifs.competitionsetup.service.sectionupdaters.CompetitionSetupSubsectionSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection.FINANCES;

/**
 * Competition setup section saver for the application -> finance form sub-section.
 */
@Service
public class ApplicationFinanceSectionSaver extends AbstractSectionSaver implements CompetitionSetupSubsectionSaver {

    @Autowired
    private CompetitionService competitionService;

	@Autowired
	private CompetitionSetupFinanceService competitionSetupFinanceService;

	@Override
	public CompetitionSetupSection sectionToSave() { return APPLICATION_FORM; }

	@Override
	public CompetitionSetupSubsection subsectionToSave() { return FINANCES; }

	@Override
	protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
        ApplicationFinanceForm form = (ApplicationFinanceForm) competitionSetupForm;
		competition.setIncludeGrowthTable(form.isIncludeGrowthTable());
		competition.setFullApplicationFinance(form.isFullApplicationFinance());
		CompetitionSetupFinanceResource csfr = new CompetitionSetupFinanceResource();
		// TODO at the moment we save the finance setup information to the competition itself as a flag.
		// TODO We also change the active status on a relevant form inputs.
		// TODO Going forward we need to remove the flag and key of the activity of the form inputs only.
		csfr.setFullApplicationFinance(form.isFullApplicationFinance());
		csfr.setIncludeGrowthTable(form.isIncludeGrowthTable());
		csfr.setCompetitionId(competition.getId());
		competitionSetupFinanceService.updateFinance(csfr);
		return competitionService.update(competition);
	}

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return ApplicationFinanceForm.class.equals(clazz);
	}

}
