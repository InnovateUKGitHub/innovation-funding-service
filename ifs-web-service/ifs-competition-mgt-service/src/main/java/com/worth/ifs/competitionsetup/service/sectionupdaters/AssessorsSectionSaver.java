package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.AssessorsForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.codehaus.groovy.runtime.InvokerHelper.asList;

/**
 * Competition setup section saver for the assessor section.
 */
@Service
public class AssessorsSectionSaver extends AbstractSectionSaver implements CompetitionSetupSectionSaver {

	@Autowired
	private CompetitionService competitionService;
	
	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.ASSESSORS;
	}

	@Override
	public List<Error> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		
		AssessorsForm assessorsForm = (AssessorsForm) competitionSetupForm;
		
		competition.setAssessorCount(assessorsForm.getAssessorCount());
		competition.setAssessorPay(assessorsForm.getAssessorPay());

		competitionService.update(competition);
		
        return Collections.emptyList();
	}

	@Override
	public List<Error> autoSaveSectionField(CompetitionResource competitionResource, String fieldName, String value, Optional<Long> objectId) {
		return performAutoSaveField(competitionResource, fieldName, value);
	}

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return AssessorsForm.class.equals(clazz);
	}

	@Override
	public List<Error> updateCompetitionResourceWithAutoSave(List<Error> errors, CompetitionResource competitionResource, String fieldName, String value) throws ParseException {
		switch (fieldName) {
			case "assessorCount":
				competitionResource.setAssessorCount(Integer.parseInt(value));
				break;
			case "assessorPay":
				if (value == null || StringUtils.isEmpty(value)) {
					return asList(new Error("validation.assessorsform.assessorPay.required", HttpStatus.BAD_REQUEST));
				} else if (!NumberUtils.isNumber(value)) {
					return asList(new Error("validation.assessorsform.assessorPay.only.numbers", HttpStatus.BAD_REQUEST));
				} else if (!assessorPayInRange(value)) {
					return asList(new Error("validation.assessorsform.assessorPay.max.amount.invalid", HttpStatus.BAD_REQUEST));
				} else {
					competitionResource.setAssessorPay(new BigDecimal(value));
				}
				break;
			default:
				return asList(new Error("Field not found", HttpStatus.BAD_REQUEST));
		}
		return errors;
	}


	private boolean assessorPayInRange(String value) {
		BigDecimal pay = new BigDecimal(value);
		return (new BigDecimal("99999999.99").compareTo(pay) > 0 && pay.scale() <= 2) ? true : false;
	}

}