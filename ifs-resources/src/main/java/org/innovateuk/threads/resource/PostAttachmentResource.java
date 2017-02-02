package org.innovateuk.threads.resource;

import org.innovateuk.ifs.file.resource.FileEntryResource;

public class PostAttachmentResource {
    public final Long postId;
    public final FileEntryResource file;

    public PostAttachmentResource(Long postId, FileEntryResource file) {
        this.postId = postId;
        this.file = file;
    }
}
