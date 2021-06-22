package org.innovateuk.ifs.project.document.resource;

import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.ZonedDateTime;

public class ProjectDocumentResource {

    private Long id;

    private Long project;

    private CompetitionDocumentResource competitionDocument;

    private FileEntryResource fileEntry;

    private DocumentStatus status;

    private String statusComments;

    private UserResource modifiedBy;

    private ZonedDateTime modifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProject() {
        return project;
    }

    public void setProject(Long project) {
        this.project = project;
    }

    public CompetitionDocumentResource getCompetitionDocument() {
        return competitionDocument;
    }

    public void setCompetitionDocument(CompetitionDocumentResource competitionDocument) {
        this.competitionDocument = competitionDocument;
    }

    public FileEntryResource getFileEntry() {
        return fileEntry;
    }

    public void setFileEntry(FileEntryResource fileEntry) {
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

    public UserResource getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(UserResource modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public ZonedDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(ZonedDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
