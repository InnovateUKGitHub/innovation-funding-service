package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionCoFunderResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.AdditionalInfoForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.worth.ifs.commons.error.Error;

/**
 * Competition setup section saver for the additional info section.
 */
@Service
public class AdditionalInfoSectionSaver implements CompetitionSetupSectionSaver {

    @Autowired
    private CompetitionService competitionService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.ADDITIONAL_INFO;
	}

	@Override
	public List<Error> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		AdditionalInfoForm additionalInfoForm = (AdditionalInfoForm) competitionSetupForm;
		competition.setActivityCode(additionalInfoForm.getActivityCode());
		competition.setInnovateBudget(additionalInfoForm.getInnovateBudget());
		competition.setFunder(additionalInfoForm.getFunder());
		competition.setFunderBudget(additionalInfoForm.getFunderBudget());
		competition.setBudgetCode(additionalInfoForm.getBudgetCode());
		competition.setPafCode(additionalInfoForm.getPafNumber());
		additionalInfoForm.setCompetitionCode(competition.getCode());
		competition.setCoFunders(new ArrayList<>());
		additionalInfoForm.getCoFunders().forEach(coFunderForm -> {
			CompetitionCoFunderResource competitionCoFunderResource = new CompetitionCoFunderResource();
			competitionCoFunderResource.setCoFunder(coFunderForm.getCoFunder());
			competitionCoFunderResource.setCoFunderBudget(coFunderForm.getCoFunderBudget());
			competition.getCoFunders().add(competitionCoFunderResource);
		});
        competitionService.update(competition);
		return new ArrayList<>();
	}

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return AdditionalInfoForm.class.equals(clazz);
	}

}
