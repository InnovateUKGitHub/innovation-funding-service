package com.worth.ifs.service.competitionsetup.sectionupdaters;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.competitionsetup.AdditionalInfoForm;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;

/**
 * Competition setup section saver for the additional info section.
 */
@Service
public class AdditionalInfoSectionSaver implements CompetitionSetupSectionSaver {

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.ADDITIONAL_INFO;
	}

	@Override
	public void saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		
	}

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return AdditionalInfoForm.class.equals(clazz);
	}

}
