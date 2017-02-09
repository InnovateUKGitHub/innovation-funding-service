package org.innovateuk.ifs.threads.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.of;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isProjectFinanceUser;

@Component
@PermissionRules
public class ProjectFinanceQueryPermissionRules extends BasePermissionRules {
    @Autowired
    protected ProjectFinanceRepository projectFinanceRepository;

    @PermissionRule( value = "PF_CREATE", description = "Only Internal Users can create Queries")
    public boolean onlyInternalUsersCanCreateQueries(final QueryResource query, final UserResource user) {
        return isProjectFinanceUser(user) && queryHasOnePostWithAuthorBeingCurrentProjectFinance(query, user);
    }

    private boolean queryHasOnePostWithAuthorBeingCurrentProjectFinance(QueryResource query, UserResource user) {
        return query.posts.size() == 1 && query.posts.get(0).author.getId().equals(user.getId());
    }

    @PermissionRule(value = "PF_VIEW", description = "Only Internal of Project Finance Users can view Queries")
    public boolean onlyInternalUsersOrFinanceContactCanViewTheirQueries(final QueryResource query, final UserResource user) {
        return isProjectFinanceUser(user) || isFinanceContact(user, query.contextClassPk);
    }

    @PermissionRule(value = "PF_ADD_POST", description = "Internal users or Project Finance users can add posts to a query,"
            + " but first post has to come from the Internal user.")
    public boolean onlyInternalOrProjectFinanceUsersCanAddPosts(final QueryResource query, final UserResource user) {
        return query.posts.isEmpty() ? isProjectFinanceUser(user) : isProjectFinanceUser(user) || isFinanceContact(user, query.contextClassPk);
    }

    @PermissionRule(value = "PF_DELETE", description = "Only Internal Users can delete a Query")
    public boolean onlyInternalUsersCanDeleteQueries(final QueryResource query, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    private boolean isFinanceContact(UserResource user, Long projectFinance) {
        return findProjectFinance(projectFinance)
                .map(pf -> pf.getOrganisation().isFinanceContact(user.getId())).orElse(false);
    }

    private Optional<ProjectFinance> findProjectFinance(Long id) {
        return of(projectFinanceRepository.findOne(id));
    }
}