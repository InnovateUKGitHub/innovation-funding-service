package org.innovateuk.ifs.competitionsetup.service.formpopulator.application;

import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.GuidanceRowForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.innovateuk.ifs.competitionsetup.service.formpopulator.CompetitionSetupSubsectionFormPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Form populator for the application form competition setup section.
 */
@Service
public class ApplicationQuestionFormPopulator implements CompetitionSetupSubsectionFormPopulator {

	@Autowired
	private CompetitionSetupQuestionService competitionSetupQuestionService;

	@Override
	public CompetitionSetupSubsection sectionToFill() {
		return CompetitionSetupSubsection.QUESTIONS;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId) {

		ApplicationQuestionForm competitionSetupForm = new ApplicationQuestionForm();

		if(objectId.isPresent()) {
			CompetitionSetupQuestionResource questionResource = competitionSetupQuestionService.getQuestion(objectId.get()).getSuccessObjectOrThrowException();
			competitionSetupForm.setQuestion(questionResource);

			competitionSetupForm.getQuestion().getGuidanceRows().forEach(guidanceRowResource ->  {
				GuidanceRowForm grvm = new GuidanceRowForm(guidanceRowResource);
				competitionSetupForm.getGuidanceRows().add(grvm);
			});

        } else {
            throw new ObjectNotFoundException();
        }

		return competitionSetupForm;
	}

}
