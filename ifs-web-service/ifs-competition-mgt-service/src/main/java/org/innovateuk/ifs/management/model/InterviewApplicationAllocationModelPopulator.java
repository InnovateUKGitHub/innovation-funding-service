package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.resource.AssessorInterviewAllocationPageResource;
import org.innovateuk.ifs.interview.resource.AssessorInterviewAllocationResource;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewAvailableAssessorRowViewModel;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewPageResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorPageResource;
import org.innovateuk.ifs.invite.resource.InterviewParticipantResource;
import org.innovateuk.ifs.management.viewmodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the allocate applications to assessors page
 */
@Component
public class InterviewApplicationAllocationModelPopulator extends BaseManageAssessmentsModelPopulator<AssessorCountSummaryResource, AssessorCountSummaryPageResource, InterviewApplicationAllocationViewModel> {

    private InterviewInviteRestService interviewInviteRestService;

    @Autowired
    public InterviewApplicationAllocationModelPopulator(InterviewInviteRestService interviewInviteRestService) {
        this.interviewInviteRestService = interviewInviteRestService;
    }


    public InterviewApplicationAllocationViewModel populateModel(CompetitionResource competition,
                                                                 String origin
    ) {

        AssessorInterviewAllocationPageResource pageResource = interviewInviteRestService.getAllocateApplicationsOverview(
                competition.getId(),
                0)
                .getSuccess();
        /*
        * loop through resource content
        * use user id to get number of allocated applications
        * add the number to resource
        * */
        InterviewApplicationAllocationViewModel model = new InterviewApplicationAllocationViewModel(
                competition.getId(),
                competition.getName(),
                simpleMap(pageResource.getContent(), this::getRowViewModel),
                new PaginationViewModel(pageResource, origin)

        );

        return model;
    }

    private InterviewApplicationAllocationRowViewModel getRowViewModel(AssessorInterviewAllocationResource assessorInterviewAllocationResource) {
        return new InterviewApplicationAllocationRowViewModel(assessorInterviewAllocationResource);
    }

}
