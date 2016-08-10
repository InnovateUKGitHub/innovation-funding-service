package com.worth.ifs.competitionsetup.service.sectionupdaters;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.ApplicationFormForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;

/**
 * Competition setup section saver for the application form section.
 */
@Service
public class ApplicationFormSectionSaver implements CompetitionSetupSectionSaver {

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.APPLICATION_FORM;
	}

	@Override
	public void saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {

	}

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return ApplicationFormForm.class.equals(clazz);
	}
}
