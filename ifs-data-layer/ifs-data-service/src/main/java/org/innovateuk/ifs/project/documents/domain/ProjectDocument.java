package org.innovateuk.ifs.project.documents.domain;

import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;

/**
 * Stores document for a project.
 */
@Entity
public class ProjectDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projectId", referencedColumnName = "id")
    public Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "documentConfigId", referencedColumnName = "id")
    public CompetitionDocument competitionDocument;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="fileEntryId", referencedColumnName = "id")
    private FileEntry fileEntry;

    @NotNull
    @Enumerated(STRING)
    private DocumentStatus status;

    private String statusComments;

    public ProjectDocument() {
    }

    public ProjectDocument(Project project, CompetitionDocument competitionDocument, FileEntry fileEntry, DocumentStatus status) {
        this.project = project;
        this.competitionDocument = competitionDocument;
        this.fileEntry = fileEntry;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public CompetitionDocument getCompetitionDocument() {
        return competitionDocument;
    }

    public void setCompetitionDocument(CompetitionDocument competitionDocument) {
        this.competitionDocument = competitionDocument;
    }

    public FileEntry getFileEntry() {
        return fileEntry;
    }

    public void setFileEntry(FileEntry fileEntry) {
        this.fileEntry = fileEntry;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public String getStatusComments() {
        return statusComments;
    }

    public void setStatusComments(String statusComments) {
        this.statusComments = statusComments;
    }
}
