package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewApplicationPageResourceBuilder;

import static org.innovateuk.ifs.interview.builder.InterviewApplicationPageResourceBuilder.newInterviewApplicationPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationResourceBuilder.newInterviewApplicationResource;

public class InterviewApplicationPageResourceDocs {

    public static final InterviewApplicationPageResourceBuilder INTERVIEW_APPLICATION_PAGE_RESOURCE_BUILDER =
            newInterviewApplicationPageResource()
                    .withContent(newInterviewApplicationResource().build(2))
                    .withSize(20)
                    .withTotalPages(5)
                    .withTotalElements(100L)
                    .withNumber(0)
                    .withAllocatedApplications(1L)
                    .withUnallocatedApplications(2L);
}
