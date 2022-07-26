package org.innovateuk.ifs.competition.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.innovateuk.ifs.file.resource.FileTypeResource;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompetitionEoiDocumentResource {
    private Long competitionEoiEvidenceConfigId;
    private Long fileTypeId;
}
