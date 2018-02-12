package org.innovateuk.ifs.management.model;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.assessment.review.resource.AssessmentPanelInviteStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyStatisticsRestService;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Invite assessors view.
 */
@Component
abstract class PanelInviteAssessorsModelPopulator<ViewModelType extends InviteAssessorsViewModel> {

    @Autowired
    private CompetitionKeyStatisticsRestService competitionKeyStatisticsRestService;

    public ViewModelType populateModel(CompetitionResource competition) {
        ViewModelType model = populateCompetitionDetails(createModel(), competition);
        populateStatistics(model,competition);
        populateCompetitionInnovationSectorAndArea(model, competition);
        return model;
    }

    protected abstract ViewModelType createModel();

    private ViewModelType populateCompetitionDetails(ViewModelType model, CompetitionResource competition) {
        model.setCompetitionId(competition.getId());
        model.setCompetitionName(competition.getName());
        return model;
    }

    private void populateStatistics(ViewModelType model, CompetitionResource competitionResource) {
        AssessmentPanelInviteStatisticsResource statisticsResource = competitionKeyStatisticsRestService.getAssessmentPanelInviteStatisticsByCompetition(competitionResource.getId()).getSuccess();
        model.setAssessorsInvited(statisticsResource.getInvited());
        model.setAssessorsAccepted(statisticsResource.getAccepted());
        model.setAssessorsDeclined(statisticsResource.getDeclined());
        model.setAssessorsStaged(statisticsResource.getPending());
    }

    private void populateCompetitionInnovationSectorAndArea(ViewModelType model, CompetitionResource competition) {
        model.setInnovationSector(competition.getInnovationSectorName());
        model.setInnovationArea(StringUtils.join(competition.getInnovationAreaNames(),", "));
    }
}
