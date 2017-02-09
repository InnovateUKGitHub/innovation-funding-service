package org.innovateuk.threads.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

public class QueryResource {
    public final Long id;
    public final Long contextClassPk;
    public final List<PostResource> posts;
    public final FinanceChecksSectionType section;
    public final String title;
    public final boolean awaitingResponse;
    public final LocalDateTime createdOn;

    @JsonCreator
    public QueryResource(@JsonProperty("id") Long id, @JsonProperty("contextClassPk") Long contextClassPk,
                         @JsonProperty("posts") List<PostResource> posts, @JsonProperty("section") FinanceChecksSectionType section,
                         @JsonProperty("title") String title, @JsonProperty("awaitingResponse") boolean awaitingResponse,
                         @JsonProperty("createdOn") LocalDateTime createdOn) {
        this.id = id;
        this.contextClassPk = contextClassPk;
        this.posts = ofNullable(posts).map(ArrayList::new).orElse(new ArrayList());
        this.section = section;
        this.title = title;
        this.awaitingResponse = awaitingResponse;
        this.createdOn = createdOn;
    }
}
