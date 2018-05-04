package org.innovateuk.ifs.threads.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isProjectFinanceUser;

/**
 * Defines the permissions for interaction with project finance queries.
 */
@Component
@PermissionRules
public class ProjectFinanceQueryPermissionRules extends BasePermissionRules {

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectProcessRepository projectProcessRepository;

    @PermissionRule(value = "PF_CREATE", description = "Only Project Finance Users can create Queries")
    public boolean onlyProjectFinanceUsersCanCreateQueries(final QueryResource query, final UserResource user) {
        return isProjectFinanceUser(user) && isProjectInSetup(query.contextClassPk) && queryHasOnePostWithAuthorBeingCurrentProjectFinance(query, user);
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
        return isProjectFinanceUser(user) && isProjectInSetup(query.contextClassPk);
    }

    @PermissionRule(value = "PF_ADD_POST", description = "Project partners can add posts to a query")
    public boolean projectPartnersCanAddPostToTheirQueries(final QueryResource query, final UserResource user) {
        return !query.posts.isEmpty() && isProjectInSetup(query.contextClassPk) && isPartner(user, query.contextClassPk);
    }

    private boolean isPartner(UserResource user, Long projectFinance) {
        return findProjectFinance(projectFinance)
                .map(pf -> pf.isPartner(user.getId())).orElse(false);
    }

    private Optional<ProjectFinance> findProjectFinance(Long id) {
        return ofNullable(projectFinanceRepository.findOne(id));
    }

    private boolean isProjectInSetup(Long projectFinance) {
        Optional<ProjectFinance> pf = findProjectFinance(projectFinance);
        if (pf.isPresent()){
            long projectId = pf.get().getProject().getId();
            return isProjectStateInSetup(projectId);
        }
        return false;
    }

    private boolean isProjectStateInSetup(long projectId){
        ProjectProcess projectProcess = projectProcessRepository.findOneByTargetId(projectId);
        return ProjectState.SETUP.equals(projectProcess.getProcessState());
    }
}