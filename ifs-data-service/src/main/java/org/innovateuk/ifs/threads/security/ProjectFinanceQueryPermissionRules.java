package org.innovateuk.ifs.threads.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isProjectFinanceUser;

/**
 * Defines the permissions for interaction with project finance queries.
 */
@Component
@PermissionRules
public class ProjectFinanceQueryPermissionRules  {
    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @PermissionRule(value = "PF_CREATE", description = "Only Project Finance Users can create Queries")
    public boolean onlyProjectFinanceUsersCanCreateQueries(final QueryResource query, final UserResource user) {
        return isProjectFinanceUser(user) && queryHasOnePostWithAuthorBeingCurrentProjectFinance(query, user);
    }

    private boolean queryHasOnePostWithAuthorBeingCurrentProjectFinance(QueryResource query, UserResource user) {
        return query.posts.size() == 1 && query.posts.get(0).author.getId().equals(user.getId());
    }

    @PermissionRule(value = "PF_READ", description = "Project Finance can view Queries")
    public boolean projectFinanceUsersCanViewQueries(final QueryResource query, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "PF_READ", description = "Project partners can view Queries")
    public boolean projectPartnersCanViewQueries(final QueryResource query, final UserResource user) {
        return isPartner(user, query.contextClassPk);
    }

    @PermissionRule(value = "PF_ADD_POST", description = "Project Finance users can add posts to a query")
    public boolean projectFinanceUsersCanAddPostToTheirQueries(final QueryResource query, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "PF_ADD_POST", description = "Project partners can add posts to a query")
    public boolean projectPartnersCanAddPostToTheirQueries(final QueryResource query, final UserResource user) {
        return !query.posts.isEmpty() && isPartner(user, query.contextClassPk);
    }

    private boolean isPartner(UserResource user, Long projectFinance) {
        return findProjectFinance(projectFinance)
                .map(pf -> pf.isPartner(user.getId())).orElse(false);
    }

    private Optional<ProjectFinance> findProjectFinance(Long id) {
        return ofNullable(projectFinanceRepository.findOne(id));
    }
}