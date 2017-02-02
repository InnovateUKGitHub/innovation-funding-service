package org.innovateuk.threads.resource;

import org.innovateuk.ifs.user.resource.UserResource;

import java.time.LocalDateTime;

public class PostResource {
    public final Long id;
    public final UserResource author;
    public final String body;
    public final LocalDateTime createdOn;

    public PostResource(Long id, UserResource author, String body, LocalDateTime createdOn) {
        this.id = id;
        this.author = author;
        this.body = body;
        this.createdOn = createdOn;
    }
}
