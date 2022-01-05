package org.innovateuk.ifs.threads.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.ZonedDateTime;
import java.util.List;

public class QueryResource {
    public final Long id;
    public final Long contextClassPk;
    public final List<PostResource> posts;
    public final FinanceChecksSectionType section;
    public final String title;
    public final boolean awaitingResponse;
    public final ZonedDateTime createdOn;
    public final UserResource closedBy;
    public final ZonedDateTime closedDate;

    @JsonCreator
    public QueryResource(@JsonProperty("id") Long id, @JsonProperty("contextClassPk") Long contextClassPk,
                         @JsonProperty("posts") List<PostResource> posts, @JsonProperty("section") FinanceChecksSectionType section,
                         @JsonProperty("title") String title, @JsonProperty("awaitingResponse") boolean awaitingResponse,
                         @JsonProperty("createdOn") ZonedDateTime createdOn,
                         @JsonProperty("closedBy") UserResource closedBy,
                         @JsonProperty("closedDate") ZonedDateTime closedDate) {
        this.id = id;
        this.contextClassPk = contextClassPk;
        this.posts = posts;
        this.section = section;
        this.title = title;
        this.awaitingResponse = awaitingResponse;
        this.createdOn = createdOn;
        this.closedBy = closedBy;
        this.closedDate = closedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        QueryResource that = (QueryResource) o;

        return new EqualsBuilder()
                .append(awaitingResponse, that.awaitingResponse)
                .append(id, that.id)
                .append(contextClassPk, that.contextClassPk)
                .append(posts, that.posts)
                .append(section, that.section)
                .append(title, that.title)
                .append(createdOn, that.createdOn)
                .append(closedBy, that.closedBy)
                .append(closedDate, that.closedDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(contextClassPk)
                .append(posts)
                .append(section)
                .append(title)
                .append(awaitingResponse)
                .append(createdOn)
                .append(closedBy)
                .append(closedDate)
                .toHashCode();
    }
}
