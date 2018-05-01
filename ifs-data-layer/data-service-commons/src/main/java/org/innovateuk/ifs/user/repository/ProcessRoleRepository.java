package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface ProcessRoleRepository extends PagingAndSortingRepository<ProcessRole, Long> {
    List<ProcessRole> findByUser(User user);
    List<ProcessRole> findByUserId(Long userId);
    List<ProcessRole> findByUserAndApplicationId(User user, long applicationId);
    List<ProcessRole> findByUserIdAndApplicationId(long userId, long applicationId);
    ProcessRole findByUserIdAndRoleAndApplicationId(Long userId, Role role, long applicationId);
    List<ProcessRole> findByUserIdAndRoleInAndApplicationId(Long userId, List<Role> role, long applicationId);
    ProcessRole findOneByUserIdAndRoleInAndApplicationId(Long userId, List<Role> role, long applicationId);
    List<ProcessRole> findByApplicationId(long applicationId);
    List<ProcessRole> findByApplicationIdAndRole(long applicationId, Role role);
    ProcessRole findOneByApplicationIdAndRole(long applicationId, Role role);
    List<ProcessRole> findByApplicationIdAndOrganisationId(long applicationId, long organisationId);
    ProcessRole findByUserIdAndRoleAndApplicationIdAndOrganisationId(Long userId, Role role, long applicationId, long organisationId);
    boolean existsByUserIdAndApplicationId(Long userId, long applicationId);
    boolean existsByUserIdAndApplicationIdAndRole(Long id, long applicationId, Role role);
}