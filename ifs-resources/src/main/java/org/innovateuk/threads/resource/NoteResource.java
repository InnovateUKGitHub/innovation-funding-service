package org.innovateuk.threads.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

public class NoteResource {
    public final Long id;
    public final Long contextClassPk;
    public final List<PostResource> posts;
    public final String title;
    public final ZonedDateTime createdOn;

    @JsonCreator
    public NoteResource(@JsonProperty("id") Long id, @JsonProperty("contextClassPk") Long contextClassPk,
                        @JsonProperty("posts") List<PostResource> posts, @JsonProperty("title") String title,
                        @JsonProperty("createdOn") ZonedDateTime createdOn) {
        this.id = id;
        this.contextClassPk = contextClassPk;
        this.posts = ofNullable(posts).map(ArrayList::new).orElse(new ArrayList<>());
        this.title = title;
        this.createdOn = createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NoteResource that = (NoteResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(contextClassPk, that.contextClassPk)
                .append(posts, that.posts)
                .append(title, that.title)
                .append(createdOn, that.createdOn)
                .isEquals();
    }
}