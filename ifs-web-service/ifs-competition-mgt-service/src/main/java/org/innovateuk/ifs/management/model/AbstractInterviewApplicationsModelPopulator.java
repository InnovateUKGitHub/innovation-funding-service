package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationResource;
import org.innovateuk.ifs.interview.service.InterviewAllocationRestService;
import org.innovateuk.ifs.management.viewmodel.InnovationSectorViewModel;
import org.innovateuk.ifs.management.viewmodel.InterviewAllocatedApplicationRowViewModel;
import org.innovateuk.ifs.management.viewmodel.InterviewAssessorApplicationsViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.management.controller.CompetitionManagementCookieController.SELECTION_LIMIT;

public abstract class AbstractInterviewApplicationsModelPopulator {

    @Autowired
    private CompetitionRestService competitionService;

    @Autowired
    private UserRestService userService;

    @Autowired
    private AssessorRestService assessorRestService;

    @Autowired
    protected InterviewAllocationRestService interviewAllocateRestService;

    public InterviewAssessorApplicationsViewModel populateModel(long competitionId, long userId, int page) {

        UserResource user = userService.retrieveUserById(userId).getSuccess();
        CompetitionResource competition = competitionService.getCompetitionById(competitionId).getSuccess();
        AssessorProfileResource assessorProfile = assessorRestService.getAssessorProfile(userId).getSuccess();

        InterviewApplicationPageResource interviewApplicationPageResource = getPageResource(competitionId, userId, page);

        InterviewAssessorApplicationsViewModel model = new InterviewAssessorApplicationsViewModel(
                competition.getId(),
                competition.getName(),
                user,
                assessorProfile.getProfile(),
                innovationSectorViewModel(assessorProfile.getProfile().getInnovationAreas()),
                toViewModel(interviewApplicationPageResource),
                new PaginationViewModel(interviewApplicationPageResource, ""),
                interviewApplicationPageResource.getUnallocatedApplications(),
                interviewApplicationPageResource.getAllocatedApplications(),
                interviewApplicationPageResource.getTotalElements() > SELECTION_LIMIT
        );

        return model;
    }

    private List<InterviewAllocatedApplicationRowViewModel> toViewModel(InterviewApplicationPageResource interviewApplicationPageResource) {
        return interviewApplicationPageResource.getContent().stream().map(this::toViewModel).collect(toList());
    }

    private InterviewAllocatedApplicationRowViewModel toViewModel(InterviewApplicationResource resource) {
        return new InterviewAllocatedApplicationRowViewModel(resource.getId(), resource.getName(), resource.getLeadOrganisation(), resource.getNumberOfAssessors());
    }

    private List<InnovationSectorViewModel> innovationSectorViewModel(List<InnovationAreaResource> innovationAreas) {
        List<InnovationSectorViewModel> sectors = new ArrayList<>();

        innovationAreas
                .stream()
                .collect(groupingBy(InnovationAreaResource::getSector))
                .forEach(
                        (id, innovationAreaResources) ->
                                sectors.add(
                                        new InnovationSectorViewModel(innovationAreaResources.get(0).getSectorName(), innovationAreaResources)
                                )
                );

        return sectors;
    }

    protected abstract InterviewApplicationPageResource getPageResource(long competitionId, long userId, int page);
}
