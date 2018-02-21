package org.innovateuk.ifs.management.model;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.assessment.service.InterviewPanelRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.AvailableApplicationResource;
import org.innovateuk.ifs.management.viewmodel.InterviewPanelApplicationRowViewModel;
import org.innovateuk.ifs.management.viewmodel.InterviewPanelApplicationsFindViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.management.controller.CompetitionManagementCookieController.SELECTION_LIMIT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Invite assessors for Assessment Interview Panel Find view.
 */
@Component
public class InterviewPanelApplicationsFindModelPopulator {

    @Autowired
    private InterviewPanelRestService interviewPanelRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public InterviewPanelApplicationsFindViewModel populateModel(long competitionId,
                                                                 int page,
                                                                 String originQuery) {
        CompetitionResource competition = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        AvailableApplicationPageResource pageResource = interviewPanelRestService.getAvailableApplications(
                competition.getId(),
                page)
                .getSuccess();

        List<InterviewPanelApplicationRowViewModel> applications = simpleMap(pageResource.getContent(), this::getRowViewModel);

        return new InterviewPanelApplicationsFindViewModel(
                competitionId,
                competition.getName(),
                competition.getInnovationSectorName(),
                StringUtils.join(competition.getInnovationAreaNames(), ", "),
                applications,
                0,
                0,
                new PaginationViewModel(pageResource, originQuery), originQuery, pageResource.getTotalElements() > SELECTION_LIMIT);
    }

    private InterviewPanelApplicationRowViewModel getRowViewModel(AvailableApplicationResource availableApplicationResource) {
        return new InterviewPanelApplicationRowViewModel(
                availableApplicationResource.getId(),
                availableApplicationResource.getName(),
                availableApplicationResource.getLeadOrganisation()
        );
    }
}