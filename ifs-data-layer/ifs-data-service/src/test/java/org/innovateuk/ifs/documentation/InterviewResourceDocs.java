package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.resource.InterviewState;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDate;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InterviewResourceDocs {
    public static final FieldDescriptor[] interviewResourceFields = {
            fieldWithPath("id").description("Id of the interview"),
            fieldWithPath("event").description("Event of the interview"),
            fieldWithPath("interviewState").description("Interview state of the interview"),
            fieldWithPath("startDate").description("Start date of the interview"),
            fieldWithPath("endDate").description("End date of the interview"),
            fieldWithPath("processRole").description("Process role of the interview"),
            fieldWithPath("internalParticipant").description("Internal participant of the interview"),
            fieldWithPath("application").description("Application of the interview"),
            fieldWithPath("applicationName").description("Application name of the interview"),
            fieldWithPath("competition").description("Competition of the interview"),
    };
}