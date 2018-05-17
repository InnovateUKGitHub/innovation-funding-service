package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewAssignmentApplicationPageResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.interview.builder.InterviewAssignmentApplicationPageResourceBuilder.newInterviewAssignmentApplicationPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentInvitedResourceBuilder.newInterviewAssignmentApplicationResource;

public class InterviewAssignmentAssignedPageResourceDocs extends PageResourceDocs {
    public static final FieldDescriptor[] interviewAssignmentAssignedPageResourceFields = pageResourceFields;

    public static final InterviewAssignmentApplicationPageResourceBuilder interviewAssignmentAssignedPageResourceBuilder =
            newInterviewAssignmentApplicationPageResource()
                    .withContent(newInterviewAssignmentApplicationResource().build(2))
                    .withSize(20)
                    .withTotalPages(5)
                    .withTotalElements(100L)
                    .withNumber(0);
}