package org.innovateuk.ifs.competitionsetup.application.populator;

import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupQuestionService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.application.form.GuidanceRowForm;
import org.innovateuk.ifs.competitionsetup.application.form.QuestionForm;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupSubsectionFormPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Form populator for the application form competition setup section.
 */
@Service
public class QuestionFormPopulator implements CompetitionSetupSubsectionFormPopulator {

	@Autowired
	private CompetitionSetupQuestionService competitionSetupQuestionService;

	@Autowired
	private SectionService sectionService;

	@Override
	public CompetitionSetupSubsection sectionToFill() {
		return CompetitionSetupSubsection.QUESTIONS;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId) {

		QuestionForm competitionSetupForm = new QuestionForm();

		if(objectId.isPresent()) {
			CompetitionSetupQuestionResource questionResource = competitionSetupQuestionService.getQuestion(objectId.get()).getSuccess();
			competitionSetupForm.setQuestion(questionResource);

			if(sectionContainsMoreThanOneQuestion(objectId.get())) {
				competitionSetupForm.setRemovable(true);
			}

			competitionSetupForm.getQuestion().getGuidanceRows().forEach(guidanceRowResource ->  {
				GuidanceRowForm grvm = new GuidanceRowForm(guidanceRowResource);
				competitionSetupForm.getGuidanceRows().add(grvm);
			});

        } else {
            throw new ObjectNotFoundException();
        }

		return competitionSetupForm;
	}

	private boolean sectionContainsMoreThanOneQuestion(Long questionId) {
		SectionResource sectionServiceResult = sectionService.getSectionByQuestionId(questionId);
		return sectionServiceResult.getQuestions().size() > 1;
	}

}
