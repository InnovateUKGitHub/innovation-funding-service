package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.InterviewPanelCreatedInvitePageResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.invite.builder.InterviewPanelCreatedInvitePageResourceBuilder.newInterviewPanelCreatedInvitePageResource;
import static org.innovateuk.ifs.invite.builder.InterviewPanelCreatedInviteResourceBuilder.newInterviewPanelCreatedInviteResource;

public class InterviewPanelCreatedInvitePageResourceDocs extends PageResourceDocs {
        public static final FieldDescriptor[] interviewPanelCreatedInvitePageResourceFields = pageResourceFields;

        public static final InterviewPanelCreatedInvitePageResourceBuilder interviewPanelCreatedInvitePageResourceBuilder =
                newInterviewPanelCreatedInvitePageResource()
                        .withContent(newInterviewPanelCreatedInviteResource().build(2))
                        .withSize(20)
                        .withTotalPages(5)
                        .withTotalElements(100L)
                        .withNumber(0);
    }