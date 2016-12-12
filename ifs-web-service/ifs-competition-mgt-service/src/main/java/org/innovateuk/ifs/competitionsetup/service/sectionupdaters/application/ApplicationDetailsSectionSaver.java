package org.innovateuk.ifs.competitionsetup.service.sectionupdaters.application;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.innovateuk.ifs.competitionsetup.service.sectionupdaters.AbstractSectionSaver;
import org.innovateuk.ifs.competitionsetup.service.sectionupdaters.CompetitionSetupSubsectionSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Competition setup section saver for the application -> application details form sub-section.
 */
@Service
public class ApplicationDetailsSectionSaver extends AbstractSectionSaver implements CompetitionSetupSubsectionSaver {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupQuestionService competitionSetupQuestionService;

	@Override
	public CompetitionSetupSubsection sectionToSave() {
		return CompetitionSetupSubsection.APPLICATION_DETAILS;
	}

	@Override
	public ServiceResult<Void> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		ApplicationDetailsForm form = (ApplicationDetailsForm) competitionSetupForm;
		competition.setUseResubmissionQuestion(form.isUseResubmissionQuestion());
		return competitionService.update(competition);
	}

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return ApplicationDetailsForm.class.equals(clazz);
	}
}
