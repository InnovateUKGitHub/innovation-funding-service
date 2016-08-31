package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionFunderResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.AdditionalInfoForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.codehaus.groovy.runtime.InvokerHelper.asList;

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
		competition.setBudgetCode(additionalInfoForm.getBudgetCode());
		competition.setPafCode(additionalInfoForm.getPafNumber());
		additionalInfoForm.setCompetitionCode(competition.getCode());
		competition.setFunders(new ArrayList());
		additionalInfoForm.getFunders().forEach(funder -> {
            CompetitionFunderResource competitionFunderResource = new CompetitionFunderResource();
            competitionFunderResource.setFunder(funder.getFunder());
            competitionFunderResource.setFunderBudget(funder.getFunderBudget());
            competitionFunderResource.setCoFunder(funder.getCoFunder());
			competition.getFunders().add(competitionFunderResource);
		});
        competitionService.update(competition);
		return Collections.emptyList();
	}

	@Override
	public List<Error> autoSaveSectionField(CompetitionResource competitionResource, String fieldName, String value) {
		List<Error> errors = new ArrayList<>();

		try {
			errors = updateCompetitionResourceWithAutoSave(errors, competitionResource, fieldName, value);
		} catch (ParseException e) {
			errors.add(new Error(e.getMessage(), HttpStatus.BAD_REQUEST));
		}

		if(!errors.isEmpty()) {
			return errors;
		}

		competitionService.update(competitionResource);

		return Collections.emptyList();
	}

	private List<Error> updateCompetitionResourceWithAutoSave(List<Error> errors, CompetitionResource competitionResource, String fieldName, String value) throws ParseException {
	    Boolean notFound = false;

		switch (fieldName) {
		    case "pafNumber":
				competitionResource.setPafCode(value);
				break;
			case "budgetCode":
				competitionResource.setBudgetCode(value);
				break;
			case "activityCode":
				competitionResource.setActivityCode(value);
				break;
			case "competitionCode":
				competitionResource.setCode(value);
				break;
			default:
				notFound = true;
		}

		if(notFound) {
            errors = asList(new Error("Field not found", HttpStatus.BAD_REQUEST));
        } else {
            errors = tryUpdateCofunders(competitionResource, fieldName, value);
        }

        return errors;
	}

    private List<Error> tryUpdateCofunders(CompetitionResource competitionResource, String fieldName, String value) {
        if(fieldName.endsWith("coFunder")) {

        } else if(fieldName.endsWith("coFunderBudget")) {

        } else {
           return asList(new Error("Field not found", HttpStatus.BAD_REQUEST));
        }

        return Collections.emptyList();
    }

    @Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return AdditionalInfoForm.class.equals(clazz);
	}

}
