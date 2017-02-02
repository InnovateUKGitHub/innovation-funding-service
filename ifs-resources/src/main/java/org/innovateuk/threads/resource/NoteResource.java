package org.innovateuk.threads.resource;

import java.time.LocalDateTime;
import java.util.List;

public class NoteResource {
    public final Long id;
    public final List<PostResource> posts;
    public final FinanceChecksSectionType sectionType;
    public final String title;
    public final LocalDateTime createdOn;

    public NoteResource(Long id, List<PostResource> posts, FinanceChecksSectionType sectionType,
                        String title, LocalDateTime createdOn) {
        this.id = id;
        this.posts = posts;
        this.sectionType = sectionType;
        this.title = title;
        this.createdOn = createdOn;
    }
}
