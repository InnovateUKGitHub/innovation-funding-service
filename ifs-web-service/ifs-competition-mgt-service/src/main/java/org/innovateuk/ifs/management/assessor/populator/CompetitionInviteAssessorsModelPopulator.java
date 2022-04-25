package org.innovateuk.ifs.management.assessor.populator;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.CompetitionInviteStatisticsResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.assessor.viewmodel.InviteAssessorsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Build the model for the Invite assessors view.
 */
@Component
abstract class CompetitionInviteAssessorsModelPopulator<ViewModelType extends InviteAssessorsViewModel> {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private InviteUserRestService inviteUserRestService;

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
        model.setFundingType(competition.getFundingType());
        return model;
    }

    private void populateStatistics(ViewModelType model, CompetitionResource competitionResource) {
        CompetitionInviteStatisticsResource competitionInviteStatisticsResource = competitionInviteRestService.getInviteStatistics(competitionResource.getId()).getSuccess();
        model.setAssessorsInvited(competitionInviteStatisticsResource.getInvited());
        model.setAssessorsAccepted(competitionInviteStatisticsResource.getAccepted());
        model.setAssessorsDeclined(competitionInviteStatisticsResource.getDeclined());
    }

    private void populateCompetitionInnovationSectorAndArea(ViewModelType model, CompetitionResource competition) {
        model.setInnovationSector(competition.getInnovationSectorName());
        model.setInnovationArea(StringUtils.join(competition.getInnovationAreaNames(),", "));
    }

    public List<InnovationAreaResource> getExternalAssessorsInnovationAreas(String email) {
        List<RoleInviteResource> roleInviteResources = inviteUserRestService.findExternalInvitesByEmail(email).getSuccess();
        return roleInviteResources.stream().map(RoleInviteResource::getInnovationArea).collect(Collectors.toList());
    }
}
