package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.interview.resource.InterviewApplicationPageResource;
import org.springframework.stereotype.Component;

@Component
public class UnallocatedInterviewApplicationsModelPopulator extends AbstractInterviewApplicationsModelPopulator {

    @Override
    protected InterviewApplicationPageResource getPageResource(long competitionId, long userId, int page) {
        return interviewAllocateRestService.getUnallocatedApplications(competitionId, userId, page).getSuccess();
    }
}
