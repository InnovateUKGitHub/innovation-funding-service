package org.innovateuk.ifs.interview.model;

import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.springframework.stereotype.Component;

@Component
public class AllocatedInterviewApplicationsModelPopulator extends AbstractInterviewApplicationsModelPopulator {

    @Override
    protected InterviewApplicationPageResource getPageResource(long competitionId, long userId, int page) {
        return interviewAllocateRestService.getAllocatedApplications(competitionId, userId, page).getSuccess();
    }
}
