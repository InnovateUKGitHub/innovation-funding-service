package org.innovateuk.ifs.assessment.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentAggregateScoreDocs {
    public static final FieldDescriptor[] applicationAssessmentAggregateResourceFields = {
            fieldWithPath("scopeAssessed").description("Flag to signify if the scope question requires assessment feedback"),
            fieldWithPath("totalScope").description("The total number of assessments for scope"),
            fieldWithPath("inScope").description("The total number of in-scope assessments"),
            fieldWithPath("scores").description("The average scores for each question"),
            fieldWithPath("averagePercentage").description("The total average percentage")
    };
}
