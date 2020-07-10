package org.innovateuk.ifs.management.competition.setup.application.populator;

import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.management.competition.setup.application.form.GuidanceRowForm;
import org.innovateuk.ifs.management.competition.setup.application.form.QuestionForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSubsectionFormPopulator;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Form populator for the application form competition setup section.
 */
@Service
public class QuestionFormPopulator implements CompetitionSetupSubsectionFormPopulator {

    @Autowired
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    @Autowired
    private SectionService sectionService;

    @Override
    public CompetitionSetupSubsection sectionToFill() {
        return CompetitionSetupSubsection.QUESTIONS;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId) {

        QuestionForm competitionSetupForm = new QuestionForm();

        if (objectId.isPresent()) {
            CompetitionSetupQuestionResource questionResource = questionSetupCompetitionRestService.getByQuestionId(
                    (objectId.get())).getSuccess();
            competitionSetupForm.setQuestion(questionResource);

            if (sectionContainsMoreThanOneQuestion(objectId.get())) {
                competitionSetupForm.setRemovable(true);
            }

            if (questionResource.getNumberOfUploads() == 0) {
                competitionSetupForm.setNumberOfUploads(0);
                questionResource.setAppendix(false);
            } else {
                competitionSetupForm.setNumberOfUploads(questionResource.getNumberOfUploads());
                questionResource.setAppendix(true);
            }

            competitionSetupForm.getQuestion().getGuidanceRows().forEach(guidanceRowResource -> {
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
