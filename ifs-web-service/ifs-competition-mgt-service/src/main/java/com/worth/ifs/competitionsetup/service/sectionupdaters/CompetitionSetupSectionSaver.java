package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.competition.resource.CompetitionSetupSection;

public interface CompetitionSetupSectionSaver extends CompetitionSetupSaver {

	CompetitionSetupSection sectionToSave();
	
}
