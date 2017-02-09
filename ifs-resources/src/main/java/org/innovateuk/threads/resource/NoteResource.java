package org.innovateuk.threads.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

public class NoteResource {
    public final Long id;
    public final Long contextClassPk;
    public final List<PostResource> posts;
    public final String title;
    public final LocalDateTime createdOn;

    @JsonCreator
    public NoteResource(@JsonProperty("id") Long id, @JsonProperty("contextClassPk") Long contextClassPk,
                        @JsonProperty("posts") List<PostResource> posts, @JsonProperty("title") String title,
                        @JsonProperty("createdOn") LocalDateTime createdOn) {
        this.id = id;
        this.contextClassPk = contextClassPk;
        this.posts = ofNullable(posts).map(ArrayList::new).orElse(new ArrayList<>());
        this.title = title;
        this.createdOn = createdOn;
    }
}