package org.innovateuk.ifs.competition.domain;

import lombok.*;
import org.innovateuk.ifs.file.domain.FileType;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "competition_eoi_evidence_config")
public class CompetitionEoiEvidenceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "competitionApplicationConfig",fetch = FetchType.LAZY)
    private Competition competition;

    @Builder.Default
    private boolean evidenceRequired = false;

    private String evidenceTitle;

    @Column(length=5000)
    private String evidenceGuidance;

    @ManyToMany
    @JoinTable(name = "eoi_evidence_config_file_type",
            joinColumns = @JoinColumn(name = "competition_eoi_evidence_config_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "file_type_id", referencedColumnName = "id"))
    private List<FileType> fileTypes;
}
