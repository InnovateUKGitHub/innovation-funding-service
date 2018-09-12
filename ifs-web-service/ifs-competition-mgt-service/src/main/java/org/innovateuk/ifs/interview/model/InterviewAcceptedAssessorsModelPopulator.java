package org.innovateuk.ifs.interview.model;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsPageResource;
import org.innovateuk.ifs.interview.resource.InterviewAcceptedAssessorsResource;
import org.innovateuk.ifs.interview.service.InterviewAllocationRestService;
import org.innovateuk.ifs.management.assessment.populator.BaseManageAssessmentsModelPopulator;
import org.innovateuk.ifs.management.assessor.viewmodel.InterviewAcceptedAssessorsRowViewModel;
import org.innovateuk.ifs.management.assessor.viewmodel.InterviewAcceptedAssessorsViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the allocate applications to assessors page
 */
@Component
public class InterviewAcceptedAssessorsModelPopulator extends BaseManageAssessmentsModelPopulator {

    private InterviewAllocationRestService interviewAllocationRestService;

    @Autowired
    public InterviewAcceptedAssessorsModelPopulator(InterviewAllocationRestService interviewAllocationRestService) {
        this.interviewAllocationRestService = interviewAllocationRestService;
    }

    public InterviewAcceptedAssessorsViewModel populateModel(CompetitionResource competition, String origin) {

        InterviewAcceptedAssessorsPageResource pageResource = interviewAllocationRestService.getInterviewAcceptedAssessors(
                competition.getId(),
                0)
                .getSuccess();

        InterviewAcceptedAssessorsViewModel model = new InterviewAcceptedAssessorsViewModel(
                competition.getId(),
                competition.getName(),
                simpleMap(pageResource.getContent(), this::getRowViewModel),
                new Pagination(pageResource, origin)

        );

        return model;
    }

    private InterviewAcceptedAssessorsRowViewModel getRowViewModel(InterviewAcceptedAssessorsResource interviewAcceptedAssessorsResource) {
        return new InterviewAcceptedAssessorsRowViewModel(interviewAcceptedAssessorsResource);
    }

}