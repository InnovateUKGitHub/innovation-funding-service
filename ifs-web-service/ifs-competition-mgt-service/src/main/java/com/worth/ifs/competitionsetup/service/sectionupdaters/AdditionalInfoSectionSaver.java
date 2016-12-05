package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionFunderResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.AdditionalInfoForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;

/**
 * Competition setup section saver for the additional info section.
 */
@Service
public class AdditionalInfoSectionSaver extends AbstractSectionSaver implements CompetitionSetupSectionSaver {

    private static final Log LOG = LogFactory.getLog(AdditionalInfoSectionSaver.class);

    @Autowired
    private CompetitionService competitionService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.ADDITIONAL_INFO;
	}

	@Override
	public ServiceResult<Void> saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm, boolean allowInvalidData) {
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

		try {
			competitionService.update(competition).getSuccessObjectOrThrowException();
		} catch (RuntimeException e) {
			LOG.error("Competition object not available");
			return serviceFailure(asList(new Error("competition.setup.autosave.should.be.completed", HttpStatus.BAD_REQUEST)));
		}

		return serviceSuccess();
	}

	@Override
	protected ServiceResult<Void> handleIrregularAutosaveCase(CompetitionResource competitionResource, String fieldName, String value) {
		if("removeFunder".equals(fieldName)) {
			return removeFunder(competitionResource, fieldName, value);
		} else {
			return tryUpdateFunders(competitionResource, fieldName, value);
		}
	}

	private ServiceResult<Void> removeFunder(CompetitionResource competitionResource, String fieldName, String value) {
		int index = Integer.valueOf(value);
		//If the index is out of range then ignore it, The UI will add rows without them being persisted yet.
		if (competitionResource.getFunders().size() <= index) {
			return serviceSuccess();
		}
		if (index > 0) {
			competitionResource.getFunders().remove(index);
			return competitionService.update(competitionResource);
	    } else {
			//Not allowed to remove 0th index.
			return serviceFailure(new Error("competition.setup.autosave.funder.could.not.be.removed", HttpStatus.BAD_REQUEST));
	    }
	}

	private ServiceResult<Void> tryUpdateFunders(CompetitionResource competitionResource, String fieldName, String value) {
		Integer index;
		CompetitionFunderResource funder;

		try {
			index = getFunderIndex(fieldName);
			if(index >= competitionResource.getFunders().size()) {
				addNotSavedFunders(competitionResource, index);
			}

			funder = competitionResource.getFunders().get(index);

			if (fieldName.endsWith("funder")) {
				funder.setFunder(value);
			} else if(fieldName.endsWith("funderBudget")) {
				BigDecimal funderBudget = new BigDecimal(value);
				if (funderBudget.compareTo(BigDecimal.ZERO) < 0) {
					return serviceFailure(new Error("validation.additionalinfoform.funderbudget.min", HttpStatus.BAD_REQUEST));
				}
				if (new BigDecimal("99999999.99").compareTo(funderBudget) > 0 && funderBudget.scale() <= 2) {
					funder.setFunderBudget(funderBudget);
				} else {
					return serviceFailure(new Error("validation.additionalinfoform.funderbudget.invalid", HttpStatus.BAD_REQUEST));
				}
			} else {
				return serviceFailure(new Error("Field not found", HttpStatus.BAD_REQUEST));
			}
		} catch (NumberFormatException e) {
			return serviceFailure(new Error("Field not found", HttpStatus.BAD_REQUEST));
		}

		competitionResource.getFunders().set(index, funder);

		return competitionService.update(competitionResource);
	}

	private Integer getFunderIndex(String fieldName) {
		return Integer.parseInt(fieldName.substring(fieldName.indexOf("[") + 1, fieldName.indexOf("]")));
	}

	private void addNotSavedFunders(CompetitionResource competitionResource, Integer index) {
		Integer currentIndexNotUsed = competitionResource.getFunders().size();

		for(Integer i = currentIndexNotUsed; i <= index; i++) {
			CompetitionFunderResource funder = new CompetitionFunderResource();
			funder.setCoFunder(true);
			competitionResource.getFunders().add(i, funder);
		}
	}



    @Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return AdditionalInfoForm.class.equals(clazz);
	}

}
