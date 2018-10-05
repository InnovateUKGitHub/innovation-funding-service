package org.innovateuk.ifs.project.document.resource;

public class ProjectDocumentResource {

    private Long id;

    private Long project;

    private org.innovateuk.ifs.competition.resource.ProjectDocumentResource projectDocument;

    private Long fileEntry;

    private DocumentStatus status;

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

    public Long getFileEntry() {
        return fileEntry;
    }

    public void setFileEntry(Long fileEntry) {
        this.fileEntry = fileEntry;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }
}
