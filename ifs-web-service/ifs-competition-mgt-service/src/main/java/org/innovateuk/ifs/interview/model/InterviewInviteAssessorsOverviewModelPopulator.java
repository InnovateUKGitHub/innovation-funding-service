package org.innovateuk.ifs.interview.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewInviteAssessorsOverviewViewModel;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewPageResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.management.assessor.viewmodel.OverviewAssessorRowViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.REJECTED;
import static org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController.SELECTION_LIMIT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Interview Panel Invite assessors 'Overview' view.
 */
@Component
public class InterviewInviteAssessorsOverviewModelPopulator extends InterviewInviteAssessorsModelPopulator<InterviewInviteAssessorsOverviewViewModel> {

    @Autowired
    private InterviewInviteRestService interviewInviteRestService;

    @Autowired
    private CompetitionRestService competitionsRestService;

    public InterviewInviteAssessorsOverviewViewModel populateModel(long competitionId,
                                                                int page,
                                                                String originQuery) {
        CompetitionResource competition = competitionsRestService
                .getCompetitionById(competitionId)
                .getSuccess();

        InterviewInviteAssessorsOverviewViewModel model = super.populateModel(competition);

        AssessorInviteOverviewPageResource pageResource = interviewInviteRestService.getInvitationOverview(
                competition.getId(),
                page,
                asList(REJECTED, PENDING)
        ).getSuccess();

        List<OverviewAssessorRowViewModel> assessors = simpleMap(pageResource.getContent(), this::getRowViewModel);

        model.setAssessors(assessors);
        model.setPagination(new Pagination(pageResource, originQuery));
        model.setSelectAllDisabled(pageResource.getTotalElements() > SELECTION_LIMIT);
        model.setOriginQuery(originQuery);

        return model;
    }

    private OverviewAssessorRowViewModel getRowViewModel(AssessorInviteOverviewResource assessorInviteOverviewResource) {
        return new OverviewAssessorRowViewModel(
                assessorInviteOverviewResource.getId(),
                assessorInviteOverviewResource.getName(),
                assessorInviteOverviewResource.getInnovationAreas(),
                assessorInviteOverviewResource.isCompliant(),
                assessorInviteOverviewResource.getBusinessType(),
                assessorInviteOverviewResource.getStatus(),
                assessorInviteOverviewResource.getDetails(),
                assessorInviteOverviewResource.getInviteId());
    }

    @Override
    protected InterviewInviteAssessorsOverviewViewModel createModel() {
        return new InterviewInviteAssessorsOverviewViewModel();
    }
}
