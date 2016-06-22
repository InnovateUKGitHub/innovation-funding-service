package com.worth.ifs.service.competitionsetup.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    CompetitionService competitionService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.ADDITIONAL_INFO;
	}

	@Override
	public void saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		AdditionalInfoForm additionalInfoForm = (AdditionalInfoForm) competitionSetupForm;
		competition.setActivityCode(additionalInfoForm.getActivityCode());
		competition.setInnovateBudget(additionalInfoForm.getInnovateBudget());
		competition.setCoFunders(additionalInfoForm.getCoFunders());
		competition.setCoFundersBudget(additionalInfoForm.getCoFundersBudget());

        competitionService.update(competition);
	}

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return AdditionalInfoForm.class.equals(clazz);
	}

}
