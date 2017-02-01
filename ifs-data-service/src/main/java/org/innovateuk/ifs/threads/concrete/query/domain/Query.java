package org.innovateuk.ifs.threads.concrete.query.domain;

import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.security.SecurityRuleUtil;
import org.innovateuk.ifs.threads.generic.domain.Thread;
import org.innovateuk.ifs.threads.generic.domain.Threadable;
import org.innovateuk.ifs.threads.generic.post.domain.Post;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import java.util.List;

import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;

@Entity
@Component
public final class Query implements Threadable {

    private Thread thread;
    private SectionType section;
    @Autowired
    private UserMapper userMapper;

    public boolean isAwaitingResponse() {
        return thread.latestPost()
                .map(Post::writer).map(userMapper::mapToResource).map(SecurityRuleUtil::isInternal)
                .orElse(false);
    }

    public final List<Post> posts() {
        return thread.posts();
    }

    public final SectionType section() {
        return section;
    }


}
