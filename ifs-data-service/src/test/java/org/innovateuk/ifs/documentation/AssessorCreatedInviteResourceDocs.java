package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.AssessorCreatedInvitePageResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.documentation.PageResourceDocs.pageResourceFields;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInvitePageResourceBuilder.newAssessorCreatedInvitePageResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;

public class AssessorCreatedInviteResourceDocs {

    public static final FieldDescriptor[] assessorCreatedInvitePageResourceFields = pageResourceFields;

    public static final AssessorCreatedInvitePageResourceBuilder assessorCreatedInvitePageResourceBuilder =
            newAssessorCreatedInvitePageResource()
                    .withContent(newAssessorCreatedInviteResource().build(2))
                    .withSize(20)
                    .withTotalPages(5)
                    .withTotalElements(100L)
                    .withNumber(0);
}