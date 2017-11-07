package org.innovateuk.ifs.management.model;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.CompetitionInviteStatisticsResource;
import org.innovateuk.ifs.management.viewmodel.CompetitionInviteAssessorsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Invite assessors view.
 */
@Component
abstract class InviteAssessorsModelPopulator<ViewModelType extends CompetitionInviteAssessorsViewModel> {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    public ViewModelType populateModel(CompetitionResource competition) {
        ViewModelType model = populateCompetitionDetails(createModel(), competition);
        populateStatistics(model, competition);
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
        CompetitionInviteStatisticsResource competitionInviteStatisticsResource = competitionInviteRestService.getInviteStatistics(competitionResource.getId()).getSuccessObject();
        model.setAssessorsInvited(competitionInviteStatisticsResource.getInvited());
        model.setAssessorsAccepted(competitionInviteStatisticsResource.getAccepted());
        model.setAssessorsDeclined(competitionInviteStatisticsResource.getDeclined());
        model.setAssessorsStaged(competitionInviteStatisticsResource.getInviteList());
    }

    private void populateCompetitionInnovationSectorAndArea(ViewModelType model, CompetitionResource competition) {
        model.setInnovationSector(competition.getInnovationSectorName());
        model.setInnovationArea(StringUtils.join(competition.getInnovationAreaNames(),", "));
    }
}
