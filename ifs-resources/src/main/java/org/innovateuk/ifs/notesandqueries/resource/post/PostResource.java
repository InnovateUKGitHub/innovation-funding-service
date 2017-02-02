package org.innovateuk.ifs.notesandqueries.resource.post;

import java.time.LocalDateTime;
import java.util.List;

public class PostResource {
    private String postBody;
    private LocalDateTime createdOn;
    private Long userId;
    private List<PostAttachmentResource> attachments;

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<PostAttachmentResource> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<PostAttachmentResource> attachments) {
        this.attachments = attachments;
    }
}
