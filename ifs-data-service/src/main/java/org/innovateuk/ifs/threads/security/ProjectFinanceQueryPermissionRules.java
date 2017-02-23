package org.innovateuk.ifs.threads.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isProjectFinanceUser;

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

    @PermissionRule(value = "PF_READ", description = "Only Project Finance or Finance Contact Users can view Queries")
    public boolean onlyProjectFinanceUsersOrFinanceContactCanViewTheirQueries(final QueryResource query, final UserResource user) {
        return isProjectFinanceUser(user) || isFinanceContact(user, query.contextClassPk);
    }

    @PermissionRule(value = "PF_ADD_POST", description = "Project Finance users or Finance Contact Users can add posts to a query")
    public boolean onlyProjectFinanceUsersOrFinanceContactAddPostToTheirQueries(final QueryResource query, final UserResource user) {
        return query.posts.isEmpty() ? isProjectFinanceUser(user) : isProjectFinanceUser(user) || isFinanceContact(user, query.contextClassPk);
    }

    private boolean isFinanceContact(UserResource user, Long projectFinance) {
        return findProjectFinance(projectFinance)
                .map(pf -> pf.isFinanceContact(user.getId())).orElse(false);
    }

    private Optional<ProjectFinance> findProjectFinance(Long id) {
        return ofNullable(projectFinanceRepository.findOne(id));
    }
}