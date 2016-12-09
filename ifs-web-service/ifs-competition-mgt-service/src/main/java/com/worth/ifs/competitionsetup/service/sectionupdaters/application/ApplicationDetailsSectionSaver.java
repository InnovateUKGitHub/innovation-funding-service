package com.worth.ifs.competitionsetup.service.sectionupdaters.application;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import com.worth.ifs.competitionsetup.service.sectionupdaters.AbstractSectionSaver;
import com.worth.ifs.competitionsetup.service.sectionupdaters.CompetitionSetupSubsectionSaver;
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
