package com.worth.ifs.service.competitionsetup.sectionupdaters;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;
import com.worth.ifs.controller.form.competitionsetup.EligibilityForm;

@Service
public class ElgibilitySectionSaver implements CompetitionSetupSectionSaver {

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.ELIGIBILITY;
	}

	@Override
	public void saveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm) {
		
	}
	
	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return EligibilityForm.class.equals(clazz);
	}

}
