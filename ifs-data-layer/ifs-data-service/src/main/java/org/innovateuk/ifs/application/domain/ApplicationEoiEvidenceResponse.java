package org.innovateuk.ifs.application.domain;

import lombok.*;
import org.innovateuk.ifs.application.resource.EoiEvidenceStatus;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "application_eoi_evidence_response")
public class ApplicationEoiEvidenceResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="application_id", referencedColumnName="id")
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organisation_id", referencedColumnName = "id", nullable = false)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_entry_id", referencedColumnName = "id")
    private FileEntry fileEntry;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EoiEvidenceStatus eoiEvidenceStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "process_role_id", referencedColumnName = "id")
    private ProcessRole processRole;

    private ZonedDateTime uploadedOn;
}
