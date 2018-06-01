package org.innovateuk.ifs.review.model;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyApplicationStatisticsRestService;
import org.innovateuk.ifs.management.viewmodel.InviteAssessorsViewModel;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Invite assessors view.
 */
@Component
abstract class ReviewInviteAssessorsModelPopulator<ViewModelType extends InviteAssessorsViewModel> {

    @Autowired
    private CompetitionKeyApplicationStatisticsRestService competitionKeyApplicationStatisticsRestService;

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
        ReviewInviteStatisticsResource statisticsResource =
                competitionKeyApplicationStatisticsRestService.getReviewInviteStatisticsByCompetition(competitionResource.getId()).getSuccess();
        model.setAssessorsInvited(statisticsResource.getInvited());
        model.setAssessorsAccepted(statisticsResource.getAccepted());
        model.setAssessorsDeclined(statisticsResource.getDeclined());
    }

    private void populateCompetitionInnovationSectorAndArea(ViewModelType model, CompetitionResource competition) {
        model.setInnovationSector(competition.getInnovationSectorName());
        model.setInnovationArea(StringUtils.join(competition.getInnovationAreaNames(),", "));
    }
}
