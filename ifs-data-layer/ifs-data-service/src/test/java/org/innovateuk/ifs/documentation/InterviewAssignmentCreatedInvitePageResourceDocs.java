package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewAssignmentStagedApplicationPageResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.interview.builder.InterviewAssignmentCreatedInviteResourceBuilder.newInterviewAssignmentStagedApplicationResource;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentStagedApplicationPageResourceBuilder.newInterviewAssignmentStagedApplicationPageResource;

public class InterviewAssignmentCreatedInvitePageResourceDocs extends PageResourceDocs {
        public static final FieldDescriptor[] interviewAssignmentCreatedInvitePageResourceFields = pageResourceFields;

        public static final InterviewAssignmentStagedApplicationPageResourceBuilder interviewAssignmentCreatedInvitePageResourceBuilder =
                newInterviewAssignmentStagedApplicationPageResource()
                        .withContent(newInterviewAssignmentStagedApplicationResource().build(2))
                        .withSize(20)
                        .withTotalPages(5)
                        .withTotalElements(100L)
                        .withNumber(0);
    }