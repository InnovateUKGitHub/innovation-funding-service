package org.innovateuk.ifs.thread.viewmodel;

import org.innovateuk.ifs.notesandqueries.resource.post.PostResource;

import java.util.List;

public class ThreadPostViewModel extends PostResource {
    private String username;
    private List<ThreadPostAttachmentResourceViewModel> viewModelAttachments;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<ThreadPostAttachmentResourceViewModel> getViewModelAttachments() {
        return viewModelAttachments;
    }

    public void setViewModelAttachments(List<ThreadPostAttachmentResourceViewModel> attachments) {
        this.viewModelAttachments = attachments;
    }
}
