package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.AvailableAssessorPageResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.invite.builder.AvailableAssessorPageResourceBuilder.newAvailableAssessorPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;

public class AvailableAssessorPageResourceDocs extends PageResourceDocs {

    public static final FieldDescriptor[] availableAssessorPageResourceFields = pageResourceFields;

    public static final AvailableAssessorPageResourceBuilder availableAssessorPageResourceBuilder = newAvailableAssessorPageResource()
            .withContent(newAvailableAssessorResource().build(2))
            .withNumber(0)
            .withSize(20)
            .withTotalElements(2L)
            .withTotalPages(1);
}
