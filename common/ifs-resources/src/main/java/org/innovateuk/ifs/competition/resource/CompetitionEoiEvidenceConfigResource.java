package org.innovateuk.ifs.competition.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.innovateuk.ifs.file.resource.FileTypeResource;

import java.util.List;

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
    private List<FileTypeResource> fileTypes;
}
