package org.innovateuk.threads.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;

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

    @JsonCreator
    public QueryResource(@JsonProperty("id") Long id, @JsonProperty("contextClassPk") Long contextClassPk,
                         @JsonProperty("posts") List<PostResource> posts, @JsonProperty("section") FinanceChecksSectionType section,
                         @JsonProperty("title") String title, @JsonProperty("awaitingResponse") boolean awaitingResponse,
                         @JsonProperty("createdOn") ZonedDateTime createdOn) {
        this.id = id;
        this.contextClassPk = contextClassPk;
        this.posts = posts;
        this.section = section;
        this.title = title;
        this.awaitingResponse = awaitingResponse;
        this.createdOn = createdOn;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        QueryResource that = (QueryResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(contextClassPk, that.contextClassPk)
                .append(posts, that.posts)
                .append(section, that.section)
                .append(title, that.title)
                .append(awaitingResponse, that.awaitingResponse)
                .append(createdOn, that.createdOn)
                .isEquals();
    }
}
