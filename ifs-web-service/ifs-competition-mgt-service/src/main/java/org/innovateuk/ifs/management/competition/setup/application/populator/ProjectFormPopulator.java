package org.innovateuk.ifs.management.competition.setup.application.populator;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.management.competition.setup.application.form.ProjectForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSubsectionFormPopulator;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Form populator for the application form competition setup section.
 */
@Service
public class ProjectFormPopulator extends AbstractFormInputQuestionFormPopulator implements CompetitionSetupSubsectionFormPopulator {

    @Autowired
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    @Override
    public CompetitionSetupSubsection sectionToFill() {
        return CompetitionSetupSubsection.PROJECT_DETAILS;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource, Optional<Long> objectId) {

        ProjectForm competitionSetupForm = new ProjectForm();

        if (objectId.isPresent()) {
            CompetitionSetupQuestionResource questionResource = questionSetupCompetitionRestService
                    .getByQuestionId(objectId.get()).getSuccess();
            competitionSetupForm.setQuestion(questionResource);
            competitionSetupForm.setRemovable(true);
            populateCommon(questionResource, competitionSetupForm);
        } else {
            throw new ObjectNotFoundException();
        }

        return competitionSetupForm;
    }
}
