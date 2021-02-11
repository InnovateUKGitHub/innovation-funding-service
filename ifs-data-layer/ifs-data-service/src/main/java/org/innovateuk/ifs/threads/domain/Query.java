package org.innovateuk.ifs.threads.domain;

import org.innovateuk.ifs.security.SecurityRuleUtil;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("QUERY")
public final class Query extends MessageThread {

    @Enumerated(EnumType.STRING)
    private FinanceChecksSectionType section;

    public Query() {
        super();
    }
    public Query(Long id, Long classPk, String className, List<Post> posts, FinanceChecksSectionType sectionType,
                       String title, ZonedDateTime createdOn) {
        super(id, classPk, className, posts, title, createdOn);
        this.section = sectionType;
    }

    public Query(Long id, Long classPk, List<Post> posts, FinanceChecksSectionType sectionType,
                 String title, ZonedDateTime createdOn) {
        super(id, classPk, null, posts, title, createdOn);
        this.section = sectionType;
    }

    public boolean isAwaitingResponse() {
        return latestPost()
                .map(Post::author).map(SecurityRuleUtil::hasProjectFinanceAuthority)
                .orElse(false);
    }

    public final FinanceChecksSectionType section() {
        return section;
    }

}