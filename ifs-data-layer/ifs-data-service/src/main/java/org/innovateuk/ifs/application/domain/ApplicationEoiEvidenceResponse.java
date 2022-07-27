package org.innovateuk.ifs.application.domain;

import lombok.*;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState;
import org.innovateuk.ifs.application.resource.EoiEvidenceStatus;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessActivity;
import org.innovateuk.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Builder
@Entity
@Getter
@AllArgsConstructor
@Table(name = "application_eoi_evidence_response")
public class ApplicationEoiEvidenceResponse implements ProcessActivity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organisationId", referencedColumnName = "id", nullable = false)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fileEntryId", referencedColumnName = "id")
    private FileEntry fileEntry;

    @OneToOne(mappedBy = "target", cascade = CascadeType.ALL, optional=false, fetch = FetchType.LAZY)
    private ApplicationEoiEvidenceProcess applicationEoiEvidenceProcess;

    public ApplicationEoiEvidenceResponse() {
        this.applicationEoiEvidenceProcess = new ApplicationEoiEvidenceProcess(null, this, ApplicationEoiEvidenceState.NOT_SUBMITTED);
    }

    public ApplicationEoiEvidenceResponse(Application application, Organisation organisation, FileEntry fileEntry) {
        this.application = application;
        this.organisation = organisation;
        this.fileEntry = fileEntry;
        this.applicationEoiEvidenceProcess = new ApplicationEoiEvidenceProcess(null, this, ApplicationEoiEvidenceState.NOT_SUBMITTED);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public void setFileEntry(FileEntry fileEntry) {
        this.fileEntry = fileEntry;
    }
}
