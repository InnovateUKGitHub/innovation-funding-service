package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionInAssessmentKeyStatisticsResourceDocs {
    public static final FieldDescriptor[] competitionInAssessmentKeyStatisticsResourceFields = {
            fieldWithPath("assignmentCount").description("The number of assignments"),
            fieldWithPath("assignmentsWaiting").description("The number of assignments waiting response"),
            fieldWithPath("assignmentsAccepted").description("The number of assignments accepted"),
            fieldWithPath("assessmentsStarted").description("The number of assignments started"),
            fieldWithPath("assessmentsSubmitted").description("The number of assignments submitted")
    };
}
