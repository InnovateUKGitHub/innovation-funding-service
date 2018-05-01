package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.resource.InterviewAssessorAllocateApplicationsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAssessorAllocateApplicationsResource;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.management.viewmodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the allocate applications to assessors page
 */
@Component
public class InterviewAllocateApplicationsModelPopulator extends BaseManageAssessmentsModelPopulator<AssessorCountSummaryResource, AssessorCountSummaryPageResource, InterviewAllocateApplicationsViewModel> {

    private InterviewInviteRestService interviewInviteRestService;

    @Autowired
    public InterviewAllocateApplicationsModelPopulator(InterviewInviteRestService interviewInviteRestService) {
        this.interviewInviteRestService = interviewInviteRestService;
    }

    public InterviewAllocateApplicationsViewModel populateModel(CompetitionResource competition,
                                                                String origin
    ) {

        InterviewAssessorAllocateApplicationsPageResource pageResource = interviewInviteRestService.getAllocateApplicationsOverview(
                competition.getId(),
                0)
                .getSuccess();

        InterviewAllocateApplicationsViewModel model = new InterviewAllocateApplicationsViewModel(
                competition.getId(),
                competition.getName(),
                simpleMap(pageResource.getContent(), this::getRowViewModel),
                new PaginationViewModel(pageResource, origin)

        );

        return model;
    }

    private InterviewAllocateApplicationsRowViewModel getRowViewModel(InterviewAssessorAllocateApplicationsResource interviewAssessorAllocateApplicationsResource) {
        return new InterviewAllocateApplicationsRowViewModel(interviewAssessorAllocateApplicationsResource);
    }

}