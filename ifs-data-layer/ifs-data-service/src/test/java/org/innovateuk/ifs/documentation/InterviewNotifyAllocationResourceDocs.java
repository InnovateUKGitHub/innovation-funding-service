package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewNotifyAllocationResourceBuilder;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.interview.builder.InterviewNotifyAllocationResourceBuilder.newInterviewNotifyAllocationResource;

public class InterviewNotifyAllocationResourceDocs {

    public static final InterviewNotifyAllocationResourceBuilder INTERVIEW_NOTIFY_ALLOCATION_RESOURCE_BUILDER = newInterviewNotifyAllocationResource()
            .withCompetitionId(1L)
            .withAssessorId(2L)
            .withApplicationIds(asList(3L, 5L))
            .withSubject("subject")
            .withContent("content");
}
