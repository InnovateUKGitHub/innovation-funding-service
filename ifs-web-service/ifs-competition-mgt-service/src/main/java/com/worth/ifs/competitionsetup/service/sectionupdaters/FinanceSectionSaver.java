package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.FinanceForm;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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
	public List<Error> saveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm) {

        return Collections.emptyList();
	}

	@Override
	public List<Error> autoSaveSectionField(CompetitionResource competitionResource, String fieldName, String value) {
		return Collections.emptyList();
	}

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return FinanceForm.class.equals(clazz);
	}

}
