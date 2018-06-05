package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewApplicationPageResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationPageResourceBuilder.newInterviewApplicationPageResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationResourceBuilder.newInterviewApplicationResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InterviewApplicationPageResourceDocs extends PageResourceDocs {

    public static final FieldDescriptor[] InterviewApplicationPageResourceFields;

    static {
        List<FieldDescriptor> fields = new ArrayList<>(asList(pageResourceFields));
        fields.add(fieldWithPath("unallocatedApplications").description("The number of applications not allocated to the assessor."));
        fields.add(fieldWithPath("allocatedApplications").description("The number of applications allocated to the assessor."));
        InterviewApplicationPageResourceFields = fields.toArray(new FieldDescriptor[fields.size()]);
    }

    public static final InterviewApplicationPageResourceBuilder INTERVIEW_APPLICATION_PAGE_RESOURCE_BUILDER =
            newInterviewApplicationPageResource()
                    .withContent(newInterviewApplicationResource().build(2))
                    .withSize(20)
                    .withTotalPages(5)
                    .withTotalElements(100L)
                    .withNumber(0)
                    .withAllocatedApplications(1L)
                    .withUnallocatedApplications(2L);
}
