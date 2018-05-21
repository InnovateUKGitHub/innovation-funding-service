package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessorInviteToSendDocs {

    public static final FieldDescriptor[] ASSESSOR_INVITES_TO_SEND_FIELDS = {
            fieldWithPath("competitionId").description("Id of the competition"),
            fieldWithPath("competitionName").description("Name of the competition"),
            fieldWithPath("recipients").description("Email of the assessor"),
            fieldWithPath("content").description("Email content"),
    };

    public static final AssessorInvitesToSendResourceBuilder  ASSESSOR_INVITES_TO_SEND_RESOURCE_BUILDER = newAssessorInvitesToSendResource()
            .withCompetitionId(1L)
            .withCompetitionName("Connected digital additive manufacturing")
            .withRecipients(singletonList("recipient"))
            .withContent("content");
}
