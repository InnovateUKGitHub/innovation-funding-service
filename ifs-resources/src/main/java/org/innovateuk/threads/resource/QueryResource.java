package org.innovateuk.threads.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Optional.ofNullable;

public class QueryResource {
    public Long id;
    public Long contextClassPk;
    public List<PostResource> posts;
    public FinanceChecksSectionType section;
    public String title;
    public boolean awaitingResponse;
    public LocalDateTime createdOn;

    @JsonCreator
    public QueryResource(@JsonProperty("id") Long id, @JsonProperty("contextClassPk") Long contextClassPk,
                         @JsonProperty("posts") List<PostResource> posts, @JsonProperty("section") FinanceChecksSectionType section,
                         @JsonProperty("title") String title, @JsonProperty("awaitingResponse") boolean awaitingResponse,
                         @JsonProperty("createdOn") LocalDateTime createdOn) {
        this.id = id;
        this.contextClassPk = contextClassPk;
        this.posts = posts;
        this.section = section;
        this.title = title;
        this.awaitingResponse = awaitingResponse;
        this.createdOn = createdOn;
    }

    public QueryResource() {
        this.posts = Collections.emptyList();
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
