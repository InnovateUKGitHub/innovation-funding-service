package org.innovateuk.ifs.project.queries.viewmodel;

import org.innovateuk.ifs.notesandqueries.resource.post.PostAttachmentResource;

public class FinanceChecksQueriesAttachmentResourceViewModel extends PostAttachmentResource {
    private String filename;

    public Long getLocalFileId() {
        return localFileId;
    }

    public void setLocalFileId(Long localFileId) {
        this.localFileId = localFileId;
    }

    private Long localFileId;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
