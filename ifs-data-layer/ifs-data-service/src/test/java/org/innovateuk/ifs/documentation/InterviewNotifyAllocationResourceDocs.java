package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewNotifyAllocationResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.interview.builder.InterviewNotifyAllocationResourceBuilder.newInterviewNotifyAllocationResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InterviewNotifyAllocationResourceDocs {

    public static final FieldDescriptor[] INTERVIEW_ALLOCATION_RESOURCE_FIELDS = {
            fieldWithPath("competitionId").description("Id of the competition"),
            fieldWithPath("assessorId").description("Id of the assessor"),
            fieldWithPath("applicationIds").description("Ids of the allocated applications"),
            fieldWithPath("subject").description("Subject of the notification"),
            fieldWithPath("content").description("content of the notification"),
    };

    public static final InterviewNotifyAllocationResourceBuilder INTERVIEW_NOTIFY_ALLOCATION_RESOURCE_BUILDER = newInterviewNotifyAllocationResource()
            .withCompetitionId(1L)
            .withAssessorId(2L)
            .withApplicationIds(asList(3L, 5L))
            .withSubject("subject")
            .withContent("content");
}
