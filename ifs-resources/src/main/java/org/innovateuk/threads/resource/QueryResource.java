package org.innovateuk.threads.resource;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.List;

public class QueryResource {
    public final Long id;
    public final List<PostResource> posts;
    public final FinanceChecksSectionType sectionType;
    public final String title;
    public final boolean awaitingResponse;
    public final LocalDateTime createdOn;

    public QueryResource(Long id, List<PostResource> posts, FinanceChecksSectionType sectionType,
                         String title, boolean awaitingResponse, LocalDateTime createdOn) {
        this.id = id;
        this.posts = posts;
        this.sectionType = sectionType;
        this.title = title;
        this.awaitingResponse = awaitingResponse;
        this.createdOn = createdOn;
    }
}
