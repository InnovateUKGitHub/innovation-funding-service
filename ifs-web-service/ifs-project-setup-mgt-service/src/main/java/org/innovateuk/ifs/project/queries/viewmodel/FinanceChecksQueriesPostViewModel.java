package org.innovateuk.ifs.project.queries.viewmodel;

import org.innovateuk.ifs.notesandqueries.resource.post.PostResource;

import java.util.List;

public class FinanceChecksQueriesPostViewModel extends PostResource {
    private String username;
    private List<FinanceChecksQueriesAttachmentResourceViewModel> viewModelAttachments;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<FinanceChecksQueriesAttachmentResourceViewModel> getViewModelAttachments() {
        return viewModelAttachments;
    }

    public void setViewModelAttachments(List<FinanceChecksQueriesAttachmentResourceViewModel> attachments) {
        this.viewModelAttachments = attachments;
    }
}
