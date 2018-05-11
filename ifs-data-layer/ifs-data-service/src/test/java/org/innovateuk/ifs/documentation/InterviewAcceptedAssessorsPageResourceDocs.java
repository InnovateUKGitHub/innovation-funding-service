package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsPageResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsPageResourceBuilder.newInterviewAcceptedAssessorsPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewAcceptedAssessorsResourceBuilder.newInterviewAcceptedAssessorsResource;

public class InterviewAcceptedAssessorsPageResourceDocs extends PageResourceDocs {

    public static final FieldDescriptor[] interviewAssessorAllocateApplicationsPageResourceFields = pageResourceFields;

    public static final InterviewAcceptedAssessorsPageResourceBuilder INTERVIEW_ACCEPTED_ASSESSORS_PAGE_RESOURCE_BUILDER =
            newInterviewAcceptedAssessorsPageResource()
                    .withContent(newInterviewAcceptedAssessorsResource().build(2))
                    .withSize(20)
                    .withTotalPages(5)
                    .withTotalElements(100L)
                    .withNumber(0);
}
