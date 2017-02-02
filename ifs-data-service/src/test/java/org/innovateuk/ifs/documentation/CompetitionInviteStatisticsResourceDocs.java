package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionInviteStatisticsResourceDocs {
    public static final FieldDescriptor[] competitionInviteStatisticsResourceFields = {
            fieldWithPath("invited").description("The number of assessors invited"),
            fieldWithPath("accepted").description("The number of assessors accepted"),
            fieldWithPath("declined").description("The number of assessors declined"),
            fieldWithPath("inviteList").description("The number of assessors on the invite list")
    };
}
