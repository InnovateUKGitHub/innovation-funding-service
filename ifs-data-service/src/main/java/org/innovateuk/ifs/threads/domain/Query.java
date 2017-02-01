package org.innovateuk.ifs.threads.domain;

import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.security.SecurityRuleUtil;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("QUERY")
@Component
public final class Query extends Thread {

    private SectionType section; //TODO Nuno: move this to use the correct one once it gets merged in

    @Autowired
    private UserMapper userMapper;

    public boolean isAwaitingResponse() {
        return latestPost()
                .map(Post::author).map(userMapper::mapToResource).map(SecurityRuleUtil::isInternal)
                .orElse(false);
    }

    public final SectionType section() {
        return section;
    }
}