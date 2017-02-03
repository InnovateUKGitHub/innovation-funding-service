package org.innovateuk.threads.resource;

import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.LocalDateTime;
import java.util.List;

public class PostResource {
    public final Long id;
    public final UserResource author;
    public final String body;
    public final List<FileEntryResource> attachments;
    public final LocalDateTime createdOn;

    public PostResource(Long id, UserResource author, String body, List<FileEntryResource> attachments, LocalDateTime createdOn) {
        this.id = id;
        this.author = author;
        this.body = body;
        this.attachments = attachments;
        this.createdOn = createdOn;
    }
}
