package org.innovateuk.ifs.competition.resource;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompetitionEoiDocumentResource {
    private Long competitionEoiEvidenceConfigId;
    private Long fileTypeId;
}
