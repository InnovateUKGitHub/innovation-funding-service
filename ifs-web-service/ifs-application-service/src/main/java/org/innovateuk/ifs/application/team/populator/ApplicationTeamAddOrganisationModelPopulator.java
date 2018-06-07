package org.innovateuk.ifs.application.team.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamAddOrganisationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType.APPLICATION_TEAM;

/**
 * Builds the model for the Add Organisation view.
 */
@Component
public class ApplicationTeamAddOrganisationModelPopulator {

    @Autowired
    private QuestionRestService questionRestService;

    public ApplicationTeamAddOrganisationViewModel populateModel(ApplicationResource applicationResource) {
        return new ApplicationTeamAddOrganisationViewModel(applicationResource.getId(),
                getApplicationTeamQuestion(applicationResource.getCompetition()),
                applicationResource.getName());
    }

    private long getApplicationTeamQuestion(long competitionId) {
        return questionRestService.getQuestionByCompetitionIdAndCompetitionSetupQuestionType(competitionId,
                APPLICATION_TEAM).getSuccess().getId();
    }

}