package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder;

import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;

public class AssessorInviteOverviewPageResourceDocs {

    public static final AssessorInviteOverviewPageResourceBuilder assessorInviteOverviewPageResourceBuilder =
            newAssessorInviteOverviewPageResource()
                    .withContent(newAssessorInviteOverviewResource().build(2))
                    .withSize(20)
                    .withTotalPages(5)
                    .withTotalElements(100L)
                    .withNumber(0);
}
