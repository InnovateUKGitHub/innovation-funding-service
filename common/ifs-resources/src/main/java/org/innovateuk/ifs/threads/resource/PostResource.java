package org.innovateuk.ifs.threads.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.innovateuk.ifs.threads.attachment.resource.AttachmentResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

public class PostResource {
    public final Long id;
    public final UserResource author;
    public final String body;
    public final List<AttachmentResource> attachments;
    public final ZonedDateTime createdOn;

    @JsonCreator
    public PostResource(@JsonProperty("id") Long id, @JsonProperty("author") UserResource author,
                        @JsonProperty("body") String body, @JsonProperty("attachments") List<AttachmentResource> attachments,
                        @JsonProperty("createdOn") ZonedDateTime createdOn) {
        this.id = id;
        this.author = author;
        this.body = body;
        this.attachments = attachments;
        this.createdOn = createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PostResource that = (PostResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(author, that.author)
                .append(body, that.body)
                .append(attachments, that.attachments)
                .append(createdOn, that.createdOn)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, body, attachments, createdOn);
    }
}
