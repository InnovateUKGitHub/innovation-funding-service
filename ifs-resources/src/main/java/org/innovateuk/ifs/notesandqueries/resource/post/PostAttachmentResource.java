package org.innovateuk.ifs.notesandqueries.resource.post;

public class PostAttachmentResource {
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getFileEntryId() {
        return fileEntryId;
    }

    public void setFileEntryId(Long fileEntryId) {
        this.fileEntryId = fileEntryId;
    }

    private Long postId;
    private Long fileEntryId;
}
