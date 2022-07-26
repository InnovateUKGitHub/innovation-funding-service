package org.innovateuk.ifs.competition.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompetitionEoiEvidenceConfigResource {
    private Long id;
    private Long competitionId;
    private boolean evidenceRequired;
    private String evidenceTitle;
    private String evidenceGuidance;
}
