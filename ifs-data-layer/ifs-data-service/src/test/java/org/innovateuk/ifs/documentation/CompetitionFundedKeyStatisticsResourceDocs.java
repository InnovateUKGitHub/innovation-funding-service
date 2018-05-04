package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionFundedKeyStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.competition.builder.CompetitionFundedKeyStatisticsResourceBuilder.newCompetitionFundedKeyStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionFundedKeyStatisticsResourceDocs {
    public static final FieldDescriptor[] competitionFundedKeyStatisticsResourceFields = {
            fieldWithPath("applicationsSubmitted").description("The number of applications submitted"),
            fieldWithPath("applicationsFunded").description("The number of applications funded"),
            fieldWithPath("applicationsNotFunded").description("The number of applications not funded"),
            fieldWithPath("applicationsOnHold").description("The number of applications on hold"),
            fieldWithPath("applicationsNotifiedOfDecision").description("The number of applications notified of decision"),
            fieldWithPath("applicationsAwaitingDecision").description("The number of applications awaiting decision"),
            fieldWithPath("canManageFundingNotifications").description("True if funding notifications can be managed"),
            fieldWithPath("canReleaseFeedback").description("True if feedback can be relesed")
    };

    public static final CompetitionFundedKeyStatisticsResourceBuilder competitionFundedKeyStatisticsResourceBuilder =
            newCompetitionFundedKeyStatisticsResource()
                    .withApplicationsSubmitted(1)
                    .withApplicationsFunded(2)
                    .withApplicationsNotFunded(3)
                    .withApplicationsOnHold(4)
                    .withApplicationsNotifiedOfDecision(5)
                    .withApplicationsAwaitingDecision(6);
}