package org.innovateuk.ifs.competitionsetup.service.sectionupdaters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.form.AdditionalInfoForm;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Competition setup section saver for the additional info section.
 */
@Service
public class AdditionalInfoSectionSaver extends AbstractSectionSaver implements CompetitionSetupSectionSaver {

	private static final Log LOG = LogFactory.getLog(AdditionalInfoSectionSaver.class);

	@Autowired
	private CompetitionSetupRestService competitionSetupRestService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.ADDITIONAL_INFO;
	}

	@Override
	protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		AdditionalInfoForm additionalInfoForm = (AdditionalInfoForm) competitionSetupForm;

		setFieldsDisallowedFromChangeAfterSetupAndLive(competition, additionalInfoForm);
		setFieldsAllowedFromChangeAfterSetupAndLive(competition, additionalInfoForm);

		try {
			competitionSetupRestService.update(competition).getSuccessObjectOrThrowException();
		} catch (RuntimeException e) {
			LOG.error("Competition object not available");
			return serviceFailure(asList(new Error("competition.setup.autosave.should.be.completed", HttpStatus.BAD_REQUEST)));
		}

		return serviceSuccess();
	}


	private void setFieldsDisallowedFromChangeAfterSetupAndLive(CompetitionResource competition, AdditionalInfoForm additionalInfoForm) {
		//All fields set by saver are valid when live. Competition code cannot be set, but is not saved via this class.
	}

	private void setFieldsAllowedFromChangeAfterSetupAndLive(CompetitionResource competition, AdditionalInfoForm additionalInfoForm) {
		competition.setBudgetCode(additionalInfoForm.getBudgetCode());
		competition.setPafCode(additionalInfoForm.getPafNumber());
		competition.setActivityCode(additionalInfoForm.getActivityCode());

		competition.setFunders(new ArrayList());
		additionalInfoForm.getFunders().forEach(funder -> {
			CompetitionFunderResource competitionFunderResource = new CompetitionFunderResource();
			competitionFunderResource.setFunder(funder.getFunder());
			competitionFunderResource.setFunderBudget(funder.getFunderBudget());
			competitionFunderResource.setCoFunder(funder.getCoFunder());
			competition.getFunders().add(competitionFunderResource);
		});
	}

	@Override
	protected ServiceResult<Void> handleIrregularAutosaveCase(CompetitionResource competitionResource, String fieldName, String value, Optional<Long> questionId) {
		if("removeFunder".equals(fieldName)) {
			return removeFunder(competitionResource, fieldName, value);
		} else if (fieldName.contains("funder")) {
			return tryUpdateFunders(competitionResource, fieldName, value);
		} else {
			return super.handleIrregularAutosaveCase(competitionResource, fieldName, value, questionId);
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
			return competitionSetupRestService.update(competitionResource).toServiceResult();
	    } else {
			//Not allowed to remove 0th index.
			return serviceFailure(new Error("competition.setup.autosave.funder.could.not.be.removed", HttpStatus.BAD_REQUEST));
	    }
	}

	private ServiceResult<Void> tryUpdateFunders(CompetitionResource competitionResource, String fieldName, String value) {
		Integer index = getFunderIndex(fieldName);
		CompetitionFunderResource funder;

		if(index >= competitionResource.getFunders().size()) {
			addNotSavedFunders(competitionResource, index);
		}

		funder = competitionResource.getFunders().get(index);

		if (fieldName.endsWith("funder")) {
			funder.setFunder(value);
		} else if(fieldName.endsWith("funderBudget")) {
			funder.setFunderBudget(new BigInteger(value));
		} else {
			return serviceFailure(new Error("Field not found", HttpStatus.BAD_REQUEST));
		}

		competitionResource.getFunders().set(index, funder);

		return competitionSetupRestService.update(competitionResource).toServiceResult();
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
