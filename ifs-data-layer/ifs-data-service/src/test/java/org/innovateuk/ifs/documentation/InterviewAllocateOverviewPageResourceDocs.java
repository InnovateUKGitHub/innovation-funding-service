package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.InterviewAllocateOverviewPageResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.invite.builder.InterviewAllocateOverviewPageResourceBuilder.newInterviewAssessorAllocateApplicationsPageResource;
import static org.innovateuk.ifs.invite.builder.InterviewAllocateOverviewResourceBuilder.newInterviewAssessorAllocateApplicationsResource;

public class InterviewAllocateOverviewPageResourceDocs extends PageResourceDocs {

    public static final FieldDescriptor[] interviewAssessorAllocateApplicationsPageResourceFields = pageResourceFields;

    public static final InterviewAllocateOverviewPageResourceBuilder INTERVIEW_ALLOCATE_OVERVIEW_PAGE_RESOURCE_BUILDER =
            newInterviewAssessorAllocateApplicationsPageResource()
                    .withContent(newInterviewAssessorAllocateApplicationsResource().build(2))
                    .withSize(20)
                    .withTotalPages(5)
                    .withTotalElements(100L)
                    .withNumber(0);
}
