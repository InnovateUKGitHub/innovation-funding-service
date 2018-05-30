package org.innovateuk.ifs.interview.model;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyStatisticsRestService;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
import org.innovateuk.ifs.management.assessor.viewmodel.InviteAssessorsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Invite assessors view.
 */
@Component
abstract class InterviewInviteAssessorsModelPopulator<ViewModelType extends InviteAssessorsViewModel> {

    @Autowired
    private CompetitionKeyStatisticsRestService competitionKeyStatisticsRestService;

    public ViewModelType populateModel(CompetitionResource competition) {
        ViewModelType model = populateCompetitionDetails(createModel(), competition);
        populateCompetitionInnovationSectorAndArea(model, competition);
        populateStatistics(model, competition);
        return model;
    }

    protected abstract ViewModelType createModel();

    private ViewModelType populateCompetitionDetails(ViewModelType model, CompetitionResource competition) {
        model.setCompetitionId(competition.getId());
        model.setCompetitionName(competition.getName());
        return model;
    }

    private void populateCompetitionInnovationSectorAndArea(ViewModelType model, CompetitionResource competition) {
        model.setInnovationSector(competition.getInnovationSectorName());
        model.setInnovationArea(StringUtils.join(competition.getInnovationAreaNames(),", "));
    }

    private void populateStatistics(ViewModelType model, CompetitionResource competitionResource) {
        InterviewInviteStatisticsResource statisticsResource =
                competitionKeyStatisticsRestService.getInterviewInviteStatisticsByCompetition(competitionResource.getId()).getSuccess();
        model.setAssessorsInvited(statisticsResource.getAssessorsInvited());
        model.setAssessorsAccepted(statisticsResource.getAssessorsAccepted());
        model.setAssessorsDeclined(statisticsResource.getAssessorsRejected());
    }
}