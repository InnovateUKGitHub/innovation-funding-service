package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.competition.builder.CompetitionReadyToOpenKeyStatisticsResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.competition.builder.CompetitionReadyToOpenKeyStatisticsResourceBuilder.newCompetitionReadyToOpenKeyStatisticsResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionReadyToOpenKeyStatisticsResourceDocs {
    public static final FieldDescriptor[] competitionReadyToOpenKeyStatisticsResourceFields = {
            fieldWithPath("assessorsInvited").description("The number of assessors invited"),
            fieldWithPath("assessorsAccepted").description("The number of assessors who accepted the invite")
    };

    public static final CompetitionReadyToOpenKeyStatisticsResourceBuilder competitionReadyToOpenKeyStatisticsResourceBuilder =
            newCompetitionReadyToOpenKeyStatisticsResource()
                    .withAssessorsAccepted(1)
                    .withAssessorsInvited(2);

}
