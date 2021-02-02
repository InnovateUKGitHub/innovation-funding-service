package org.innovateuk.ifs.project.financereviewer.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.financereviewer.domain.FinanceReviewer;
import org.innovateuk.ifs.project.financereviewer.repository.FinanceReviewerRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.innovateuk.ifs.user.resource.UserStatus.ACTIVE;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service implementation for managing finance reviewers.
 */
@Service
public class FinanceReviewerServiceImpl extends BaseTransactionalService implements FinanceReviewerService {

    @Autowired
    private FinanceReviewerRepository financeReviewerRepository;

    @Override
    @Transactional
    public ServiceResult<Long> assignFinanceReviewer(long financeReviewerUserId, long projectId) {
        return getProjectFinanceUser(financeReviewerUserId)
                .andOnSuccess(user -> getProject(projectId)
                        .andOnSuccessReturn((project) -> {
                            if (project.getFinanceReviewer() != null) {
                                project.getFinanceReviewer().setUser(user);
                                return project.getFinanceReviewer().getId();
                            } else {
                                FinanceReviewer reviewer = financeReviewerRepository.save(new FinanceReviewer(user, project));
                                return reviewer.getId();
                            }
                        }));
    }

    @Override
    public ServiceResult<List<SimpleUserResource>> findFinanceUsers() {
        return serviceSuccess(userRepository.findDistinctByRolesInAndStatusIn(newArrayList(PROJECT_FINANCE, IFS_ADMINISTRATOR), EnumSet.of(ACTIVE))
                .stream()
                .map(user -> new SimpleUserResource(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()))
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<SimpleUserResource> getFinanceReviewerForProject(long projectId) {
        return getProject(projectId)
                .andOnSuccess(project -> find(project.getFinanceReviewer(), notFoundError(FinanceReviewer.class, projectId))
                .andOnSuccessReturn(reviewer -> simpleUserResourceFromUser(reviewer.getUser())));
    }

    private SimpleUserResource simpleUserResourceFromUser(User user) {
        return new SimpleUserResource(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    private ServiceResult<User> getProjectFinanceUser(long userId) {
        return find(userRepository.findById(userId), notFoundError(User.class, userId));
    }
}
