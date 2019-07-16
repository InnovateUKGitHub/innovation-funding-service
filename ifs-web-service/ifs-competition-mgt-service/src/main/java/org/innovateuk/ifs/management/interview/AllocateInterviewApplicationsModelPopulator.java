package org.innovateuk.ifs.management.interview;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;
import org.innovateuk.ifs.interview.service.InterviewAllocationRestService;
import org.innovateuk.ifs.management.interview.viewmodel.InterviewAllocateApplicationsViewModel;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllocateInterviewApplicationsModelPopulator {

    private CompetitionRestService competitionRestService;
    private UserRestService userRestService;
    private InterviewAllocationRestService interviewAllocationRestService;

    @Autowired
    public AllocateInterviewApplicationsModelPopulator(CompetitionRestService competitionRestService,
                                                       UserRestService userRestService,
                                                       InterviewAllocationRestService interviewAllocationRestService) {
        this.competitionRestService = competitionRestService;
        this.userRestService = userRestService;
        this.interviewAllocationRestService = interviewAllocationRestService;
    }

    public InterviewAllocateApplicationsViewModel populateModel(long competitionId, long userId, List<Long> applicationIds) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
        UserResource user = userRestService.retrieveUserById(userId).getSuccess();
        List<InterviewApplicationResource> interviewApplications = interviewAllocationRestService.getUnallocatedApplicationsById(competitionId, applicationIds).getSuccess();
        AssessorInvitesToSendResource invitesToSendResource = interviewAllocationRestService.getInviteToSend(competitionId, userId).getSuccess();

        return new InterviewAllocateApplicationsViewModel(
                competitionResource.getId(),
                competitionResource.getName(),
                user,
                invitesToSendResource.getContent(),
                interviewApplications
        );
    }
}