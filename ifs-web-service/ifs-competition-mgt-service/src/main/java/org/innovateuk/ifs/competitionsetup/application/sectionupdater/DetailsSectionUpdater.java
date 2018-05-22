package org.innovateuk.ifs.competitionsetup.application.sectionupdater;

import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.application.form.DetailsForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.sectionupdater.CompetitionSetupSubsectionUpdater;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection.APPLICATION_DETAILS;

/**
 * Competition setup section saver for the application -> application details form sub-section.
 */
@Service
public class DetailsSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSubsectionUpdater {

    @Autowired
	private Validator validator;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private CompetitionSetupQuestionService competitionSetupQuestionService;

	@Override
	public CompetitionSetupSubsection subsectionToSave() {
		return APPLICATION_DETAILS;
	}

	@Override
	public CompetitionSetupSection sectionToSave() {
		return APPLICATION_FORM;
	}

	@Override
	protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {
		DetailsForm form = (DetailsForm) competitionSetupForm;
		Set<ConstraintViolation<CompetitionSetupForm>> violations = validator.validate(competitionSetupForm);

		if(!violations.isEmpty()) {
			return ServiceResult.serviceFailure(new ValidationMessages(violations).getErrors());
		}
		else {
			competition.setUseResubmissionQuestion(form.getUseResubmissionQuestion());
			competition.setMaxProjectDuration(Integer.valueOf(form.getMaxProjectDuration().intValue()));
			competition.setMinProjectDuration(Integer.valueOf(form.getMinProjectDuration().intValue()));
			return competitionSetupRestService.update(competition).toServiceResult();
		}
	}

	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return DetailsForm.class.equals(clazz);
	}
}
