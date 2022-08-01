package org.innovateuk.ifs.competition.domain;

import lombok.*;

import javax.persistence.*;

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

    @OneToOne(mappedBy = "competitionEoiEvidenceConfig",fetch = FetchType.LAZY)
    private Competition competition;

    @Builder.Default
    private boolean evidenceRequired = false;

    private String evidenceTitle;

    @Column(length=5000, columnDefinition = "LONGTEXT")
    private String evidenceGuidance;
}
