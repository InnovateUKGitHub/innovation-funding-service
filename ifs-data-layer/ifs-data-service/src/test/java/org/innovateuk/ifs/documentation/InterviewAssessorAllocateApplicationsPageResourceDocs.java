package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.InterviewAssessorAllocateApplicationsPageResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.invite.builder.InterviewAssessorAllocateApplicationsPageResourceBuilder.newInterviewAssessorAllocateApplicationsPageResource;
import static org.innovateuk.ifs.invite.builder.InterviewAssessorAllocateApplicationsResourceBuilder.newInterviewAssessorAllocateApplicationsResource;

public class InterviewAssessorAllocateApplicationsPageResourceDocs extends PageResourceDocs {

    public static final FieldDescriptor[] interviewAssessorAllocateApplicationsPageResourceFields = pageResourceFields;

    public static final InterviewAssessorAllocateApplicationsPageResourceBuilder interviewAssessorAllocateApplicationsPageResourceBuilder =
            newInterviewAssessorAllocateApplicationsPageResource()
                    .withContent(newInterviewAssessorAllocateApplicationsResource().build(2))
                    .withSize(20)
                    .withTotalPages(5)
                    .withTotalElements(100L)
                    .withNumber(0);
}
