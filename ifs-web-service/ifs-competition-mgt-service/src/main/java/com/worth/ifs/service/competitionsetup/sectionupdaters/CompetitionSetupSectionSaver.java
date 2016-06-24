package com.worth.ifs.service.competitionsetup.sectionupdaters;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;

public interface CompetitionSetupSectionSaver {

	CompetitionSetupSection sectionToSave();
	
	boolean supportsForm(Class<? extends CompetitionSetupForm> clazz);
	
	void saveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm);
}
