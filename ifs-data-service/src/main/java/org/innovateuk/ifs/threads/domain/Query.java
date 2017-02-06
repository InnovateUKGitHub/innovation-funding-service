package org.innovateuk.ifs.threads.domain;

import org.innovateuk.ifs.security.SecurityRuleUtil;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("QUERY")
public final class Query extends Thread {

    @Enumerated(EnumType.STRING)
    private FinanceChecksSectionType section;

    public Query() {
        super();
    }
    public Query(Long id, Long classPk, String className, List<Post> posts, FinanceChecksSectionType sectionType,
                       String title, LocalDateTime createdOn) {
        super(id, classPk, className, posts, title, createdOn);
        this.section = sectionType;
    }

    public boolean isAwaitingResponse() {
        return latestPost()
                .map(Post::author).map(SecurityRuleUtil::isInternal)
                .orElse(false);
    }

    public final FinanceChecksSectionType section() {
        return section;
    }

}