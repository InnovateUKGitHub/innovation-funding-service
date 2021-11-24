package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.interview.builder.InterviewInviteResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.interview.builder.InterviewInviteResourceBuilder.newInterviewInviteResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InterviewInviteDocs {

    public static final InterviewInviteResourceBuilder INTERVIEW_INVITE_RESOURCE_BUILDER = newInterviewInviteResource()
            .withCompetitionId(1L)
            .withCompetitionName("Connected digital additive manufacturing")
            .withInviteHash("0519d73a-f062-4784-ae86-7a933a7de4c3")
            .withEmail("paul.plum@gmail.com")
            .withUserId(2L)
            .withStatus(CREATED);
}
