package org.innovateuk.threads.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class NoteResource {
    public final Long id;
    public final Long contextClassPk;
    public final String contextClassName;
    public final List<PostResource> posts;
    public final String title;
    public final LocalDateTime createdOn;

    @JsonCreator
    public NoteResource(@JsonProperty("id") Long id, @JsonProperty("contextClassPk") Long contextClassPk,
                        @JsonProperty("contextClassName") String contextClassName,
                        @JsonProperty("posts") List<PostResource> posts, @JsonProperty("title") String title,
                        @JsonProperty("createdOn") LocalDateTime createdOn) {
        this.id = id;
        this.contextClassPk = contextClassPk;
        this.contextClassName = contextClassName;
        this.posts = posts;
        this.title = title;
        this.createdOn = createdOn;
    }
}