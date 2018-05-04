package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;
import org.innovateuk.ifs.interview.service.InterviewAllocateRestService;
import org.innovateuk.ifs.management.viewmodel.InterviewAllocatedApplicationRowViewModel;
import org.innovateuk.ifs.management.viewmodel.InterviewAssessorApplicationsViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.stream.Collectors.toList;

public abstract class AbstractInterviewApplicationsModelPopulator {

    @Autowired
    private CompetitionRestService competitionService;

    @Autowired
    private UserRestService userService;

    @Autowired
    private AssessorRestService assessorRestService;

    @Autowired
    protected InterviewAllocateRestService interviewAllocateRestService;

    public InterviewAssessorApplicationsViewModel populateModel(long competitionId, long userId) {

        UserResource user = userService.retrieveUserById(userId).getSuccess();
        CompetitionResource competition = competitionService.getCompetitionById(competitionId).getSuccess();
        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(userId).getSuccess();

        competition.getInnovationAreaNames();

        InterviewApplicationPageResource interviewApplicationPageResource = getPageResource(competitionId, userId, 0);

        InterviewAssessorApplicationsViewModel model = new InterviewAssessorApplicationsViewModel(
                competition.getId(),
                competition.getName(),
                user,
                assessorProfile.getProfile(),
                competition.getInnovationAreaNames(),
                toViewModel(interviewApplicationPageResource),
                new PaginationViewModel(interviewApplicationPageResource, ""),
                interviewApplicationPageResource.getUnallocatedApplications(),
                interviewApplicationPageResource.getAllocatedApplications()
        );

        return model;
    }

    private List<InterviewAllocatedApplicationRowViewModel> toViewModel(InterviewApplicationPageResource interviewApplicationPageResource) {
        return interviewApplicationPageResource.getContent().stream().map(this::toViewModel).collect(toList());
    }

    private InterviewAllocatedApplicationRowViewModel toViewModel(InterviewApplicationResource resource) {
        return new InterviewAllocatedApplicationRowViewModel(resource.getId(), resource.getName(), resource.getLeadOrganisation(), resource.getNumberOfAssessors());
    }

    protected abstract InterviewApplicationPageResource getPageResource(long competitionId, long userId, int page);
}
