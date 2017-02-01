package org.innovateuk.ifs.threads.generic.post.domain;

import org.innovateuk.ifs.security.SecurityRuleUtil;
import org.innovateuk.ifs.user.domain.User;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by nalexandre@worth.systems on 31/01/2017.
 */
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private User writer;

    public User writer() {
        return writer;
    }

}
