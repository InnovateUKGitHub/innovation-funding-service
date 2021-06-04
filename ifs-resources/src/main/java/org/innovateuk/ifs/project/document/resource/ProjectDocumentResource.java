package org.innovateuk.ifs.project.document.resource;

import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.Calendar;

public class ProjectDocumentResource {

    private Long id;

    private Long project;

    private CompetitionDocumentResource competitionDocument;

    private FileEntryResource fileEntry;

    private DocumentStatus status;

    private String statusComments;

    private UserResource createdBy;

    private Calendar createdDate;

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

    public UserResource getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserResource createdBy) {
        this.createdBy = createdBy;
    }

    public Calendar getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Calendar createdDate) {
        this.createdDate = createdDate;
    }
}
