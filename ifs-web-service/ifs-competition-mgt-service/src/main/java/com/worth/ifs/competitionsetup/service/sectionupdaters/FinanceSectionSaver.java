package com.worth.ifs.competitionsetup.service.sectionupdaters;

import org.springframework.stereotype.Service;

import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.FinanceForm;

/**
 * Competition setup section saver for the finance section.
 */
@Service
public class FinanceSectionSaver implements CompetitionSetupSectionSaver {

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.FINANCE;
	}

	@Override
	public ValidationMessages saveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm) {

        return null;
	}
	
	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return FinanceForm.class.equals(clazz);
	}

}
