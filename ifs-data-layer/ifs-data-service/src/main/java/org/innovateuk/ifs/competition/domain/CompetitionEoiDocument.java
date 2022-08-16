package org.innovateuk.ifs.competition.domain;

import lombok.*;
import org.innovateuk.ifs.file.domain.FileType;

import javax.persistence.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "eoi_evidence_config_file_type")
public class CompetitionEoiDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competitionEoiEvidenceConfigId", referencedColumnName = "id")
    public CompetitionEoiEvidenceConfig competitionEoiEvidenceConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fileTypeId", referencedColumnName = "id")
    public FileType fileType;
}
