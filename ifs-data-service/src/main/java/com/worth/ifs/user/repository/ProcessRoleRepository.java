package com.worth.ifs.user.repository;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
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
    List<ProcessRole> findByUserAndApplication(User user, Application application);
    List<ProcessRole> findByUserAndApplicationId(User user, Long applicationId);
    List<ProcessRole> findByUserIdAndRoleAndApplicationId(Long userId, Role role, Long applicationId);
    List<ProcessRole> findByUserIdAndRoleInAndApplicationId(Long userId, List<Role> role, Long applicationId);
    List<ProcessRole> findByApplication(Application application);
    List<ProcessRole> findByApplicationId(Long applicationId);
    List<ProcessRole> findByApplicationIdAndRoleId(Long applicationId, Long roleId);
    List<ProcessRole> findByApplicationIdAndOrganisationId(Long applicationId, Long organisationId);
    ProcessRole findByUserIdAndApplicationId(Long userId, Long applicationId);
    ProcessRole findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(Long userId, Long roleId, Long applicationId, Long organisationId);
}
