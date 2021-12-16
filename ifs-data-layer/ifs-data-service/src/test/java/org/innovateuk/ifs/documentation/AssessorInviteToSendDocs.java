package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;

public class AssessorInviteToSendDocs {

    public static final AssessorInvitesToSendResourceBuilder  ASSESSOR_INVITES_TO_SEND_RESOURCE_BUILDER = newAssessorInvitesToSendResource()
            .withCompetitionId(1L)
            .withCompetitionName("Connected digital additive manufacturing")
            .withRecipients(singletonList("recipient"))
            .withContent("content");
}
