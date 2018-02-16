package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.InterviewPanelStagedApplicationPageResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.invite.builder.InterviewPanelCreatedInviteResourceBuilder.newInterviewPanelStagedApplicationResource;
import static org.innovateuk.ifs.invite.builder.InterviewPanelStagedApplicationPageResourceBuilder.newInterviewPanelStagedApplicationPageResource;

public class InterviewPanelCreatedInvitePageResourceDocs extends PageResourceDocs {
        public static final FieldDescriptor[] interviewPanelCreatedInvitePageResourceFields = pageResourceFields;

        public static final InterviewPanelStagedApplicationPageResourceBuilder interviewPanelCreatedInvitePageResourceBuilder =
                newInterviewPanelStagedApplicationPageResource()
                        .withContent(newInterviewPanelStagedApplicationResource().build(2))
                        .withSize(20)
                        .withTotalPages(5)
                        .withTotalElements(100L)
                        .withNumber(0);
    }