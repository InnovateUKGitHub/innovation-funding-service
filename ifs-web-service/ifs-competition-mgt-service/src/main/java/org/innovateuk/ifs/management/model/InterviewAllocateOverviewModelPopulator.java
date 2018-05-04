package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.resource.InterviewAllocateOverviewPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAllocateOverviewResource;
import org.innovateuk.ifs.interview.service.InterviewAllocateRestService;
import org.innovateuk.ifs.management.viewmodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the allocate applications to assessors page
 */
@Component
public class InterviewAllocateOverviewModelPopulator extends BaseManageAssessmentsModelPopulator<AssessorCountSummaryResource, AssessorCountSummaryPageResource, InterviewAllocateOverviewViewModel> {

    private InterviewAllocateRestService interviewAllocateRestService;

    @Autowired
    public InterviewAllocateOverviewModelPopulator(InterviewAllocateRestService interviewAllocateRestService) {
        this.interviewAllocateRestService = interviewAllocateRestService;
    }

    public InterviewAllocateOverviewViewModel populateModel(CompetitionResource competition,
                                                            String origin
    ) {

        InterviewAllocateOverviewPageResource pageResource = interviewAllocateRestService.getAllocateApplicationsOverview(
                competition.getId(),
                0)
                .getSuccess();

        InterviewAllocateOverviewViewModel model = new InterviewAllocateOverviewViewModel(
                competition.getId(),
                competition.getName(),
                simpleMap(pageResource.getContent(), this::getRowViewModel),
                new PaginationViewModel(pageResource, origin)

        );

        return model;
    }

    private InterviewAllocateOverviewRowViewModel getRowViewModel(InterviewAllocateOverviewResource interviewAllocateOverviewResource) {
        return new InterviewAllocateOverviewRowViewModel(interviewAllocateOverviewResource);
    }

}