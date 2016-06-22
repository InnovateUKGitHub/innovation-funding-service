package com.worth.ifs.service.competitionsetup.modelpopulator;

import org.springframework.ui.Model;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;

public interface CompetitionSetupSectionModelPopulator {

	CompetitionSetupSection sectionToPopulateModel();
	
	void populateModel(Model model, CompetitionResource competitionResource);
}
