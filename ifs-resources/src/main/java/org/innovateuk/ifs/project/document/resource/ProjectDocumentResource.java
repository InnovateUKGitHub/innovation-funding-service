package org.innovateuk.ifs.project.document.resource;

import org.innovateuk.ifs.file.resource.FileEntryResource;

public class ProjectDocumentResource {

    private Long id;

    private Long project;

    private org.innovateuk.ifs.competition.resource.ProjectDocumentResource projectDocument;

    private FileEntryResource fileEntry;

    private DocumentStatus status;

    private String statusComments;

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

    public org.innovateuk.ifs.competition.resource.ProjectDocumentResource getProjectDocument() {
        return projectDocument;
    }

    public void setProjectDocument(org.innovateuk.ifs.competition.resource.ProjectDocumentResource projectDocument) {
        this.projectDocument = projectDocument;
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
}
