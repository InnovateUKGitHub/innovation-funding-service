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
    List<ProcessRole> findByUserAndApplicationId(User user, Long applicationId);
    ProcessRole findByUserIdAndRoleAndApplicationId(Long userId, Role role, Long applicationId);
    List<ProcessRole> findByUserIdAndRoleInAndApplicationId(Long userId, List<Role> role, Long applicationId);
    List<ProcessRole> findByApplicationId(Long applicationId);
    List<ProcessRole> findByApplicationIdAndRole(Long applicationId, Role role);
    ProcessRole findOneByApplicationIdAndRole(Long applicationId, Role role);
    List<ProcessRole> findByApplicationIdAndOrganisationId(Long applicationId, Long organisationId);
    ProcessRole findByUserIdAndApplicationId(Long userId, Long applicationId);
    ProcessRole findByUserIdAndRoleAndApplicationIdAndOrganisationId(Long userId, Role role, Long applicationId, Long organisationId);
    boolean existsByUserIdAndApplicationId(Long userId, Long applicationId);
    boolean existsByUserIdAndApplicationIdAndRole(Long id, Long applicationId, Role role);
}