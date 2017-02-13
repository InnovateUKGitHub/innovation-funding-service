package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder.newCompetitionInviteStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionInviteStatisticsResourceDocs {
    public static final FieldDescriptor[] competitionInviteStatisticsResourceFields = {
            fieldWithPath("invited").description("The number of assessors invited"),
            fieldWithPath("accepted").description("The number of assessors accepted"),
            fieldWithPath("declined").description("The number of assessors declined"),
            fieldWithPath("inviteList").description("The number of assessors on the invite list")
    };

    public static final CompetitionInviteStatisticsResourceBuilder competitionInviteStatisticsResourceBuilder =
            newCompetitionInviteStatisticsResource()
                    .withInviteList(1)
                    .withInvited(2)
                    .withAccepted(3)
                    .withDeclined(4);
}
