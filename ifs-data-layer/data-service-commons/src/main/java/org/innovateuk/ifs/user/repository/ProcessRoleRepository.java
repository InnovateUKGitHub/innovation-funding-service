package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ProcessRoleRepository extends PagingAndSortingRepository<ProcessRole, Long> {
    List<ProcessRole> findByUser(User user);
    List<ProcessRole> findByUserId(long userId);
    List<ProcessRole> findByUserAndApplicationId(User user, long applicationId);
    ProcessRole findByUserIdAndRoleAndApplicationId(long userId, Role role, long applicationId);
    List<ProcessRole> findByUserIdAndRoleInAndApplicationId(long userId, Set<Role> role, long applicationId);
    ProcessRole findOneByUserIdAndRoleInAndApplicationId(long userId, Set<Role> role, long applicationId);
    List<ProcessRole> findByApplicationId(long applicationId);
    List<ProcessRole> findByApplicationIdAndRole(long applicationId, Role role);
    ProcessRole findOneByApplicationIdAndRole(long applicationId, Role role);
    List<ProcessRole> findByApplicationIdAndOrganisationId(long applicationId, long organisationId);
    ProcessRole findByUserIdAndRoleAndApplicationIdAndOrganisationId(long userId, Role role, long applicationId, long organisationId);
    boolean existsByUserIdAndApplicationId(long userId, long applicationId);
    boolean existsByUserIdAndApplicationIdAndRole(long id, long applicationId, Role role);
    void deleteByApplicationId(long applicationId);
}