package com.worth.ifs.competitionsetup.service.sectionupdaters;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.AssessorsForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;

/**
 * Competition setup section saver for the assessors section.
 */
@Service
public class AssessorsSectionSaver implements CompetitionSetupSectionSaver {

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.ASSESSORS;
	}

	@Override
	public void saveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm) {
		
	}
	
	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return AssessorsForm.class.equals(clazz);
	}

}
