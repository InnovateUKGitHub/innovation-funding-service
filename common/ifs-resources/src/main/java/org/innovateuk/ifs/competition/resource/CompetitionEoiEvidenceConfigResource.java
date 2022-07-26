package org.innovateuk.ifs.competition.resource;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompetitionEoiEvidenceConfigResource {
    private Long id;
    private Long competitionId;
    private boolean evidenceRequired;
    private String evidenceTitle;
    private String evidenceGuidance;
}
