package org.innovateuk.ifs.threads.domain;

import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.security.SecurityRuleUtil;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("QUERY")
@Component
public final class Query extends Thread {

    private FinanceChecksSectionType section;

    @Autowired
    private UserMapper userMapper;

    public boolean isAwaitingResponse() {
        return latestPost()
                .map(Post::author).map(userMapper::mapToResource).map(SecurityRuleUtil::isInternal)
                .orElse(false);
    }

    public final FinanceChecksSectionType section() {
        return section;
    }
}