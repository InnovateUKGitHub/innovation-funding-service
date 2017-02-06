package org.innovateuk.ifs.threads.security;

import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isProjectFinanceUser;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isSystemMaintenanceUser;

@Component
@PermissionRules
public class ThreadPermissionRules {

    @PermissionRule(value = "CREATE", description = "Only Internal Users can create Queries")
    public boolean onlyInternalUsersCanCreateQueries(final QueryResource query, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "ADD_POST", description = "Internal users or Project Finance users can add posts to a query,"
            + " but first post has to come from the Internal user.")
    public boolean onlyInternalOrProjectFinanceUsersCanAddPosts(final QueryResource query, final UserResource user) {
        return query.posts.isEmpty() ? isInternal(user) : isInternal(user) || isProjectFinanceUser(user);
    }

    @PermissionRule(value = "DELETE", description = "Only Internal Users can delete a Query")
    public boolean onlyInternalUsersCanDeleteQueries(final QueryResource query, final UserResource user) {
        return isInternal(user);
    }
}

