package org.innovateuk.ifs.docusign.domain;

import org.innovateuk.ifs.commons.util.AuditableEntity;
import org.innovateuk.ifs.docusign.resource.DocusignType;
import org.innovateuk.ifs.project.core.domain.Project;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
public class DocusignDocument extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DocusignType type;
    private long recipientId;
    private String envelopeId;

    private ZonedDateTime signedDocumentImported;

    @OneToOne(mappedBy = "signedGolDocusignDocument", fetch = FetchType.LAZY)
    private Project project;

    DocusignDocument() {}

    public DocusignDocument(long recipientId, DocusignType type) {
        this.recipientId = recipientId;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public DocusignType getType() {
        return type;
    }

    public String getEnvelopeId() {
        return envelopeId;
    }

    public void setEnvelopeId(String envelopeId) {
        this.envelopeId = envelopeId;
    }

    public ZonedDateTime getSignedDocumentImported() {
        return signedDocumentImported;
    }

    public void setSignedDocumentImported(ZonedDateTime signedDocumentImported) {
        this.signedDocumentImported = signedDocumentImported;
    }

    public Project getProject() {
        return project;
    }
}
