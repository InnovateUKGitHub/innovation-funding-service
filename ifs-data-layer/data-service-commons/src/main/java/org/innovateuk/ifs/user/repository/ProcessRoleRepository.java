package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.springframework.data.jpa.repository.Query;
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
    ProcessRole findByUserIdAndRoleAndApplicationId(long userId, ProcessRoleType role, long applicationId);
    ProcessRole findOneByUserIdAndRoleInAndApplicationId(long userId, Set<ProcessRoleType> role, long applicationId);
    List<ProcessRole> findByApplicationId(long applicationId);
    List<ProcessRole> findByApplicationIdAndRole(long applicationId, ProcessRoleType role);
    ProcessRole findOneByApplicationIdAndRole(long applicationId, ProcessRoleType role);
    List<ProcessRole> findByApplicationIdAndOrganisationId(long applicationId, long organisationId);
    boolean existsByUserIdAndRoleInAndApplicationId(long userId, Set<ProcessRoleType> role, long applicationId);
    boolean existsByUserIdAndRoleAndApplicationIdAndOrganisationId(long userId, ProcessRoleType role, long applicationId, long organisationId);
    boolean existsByUserIdAndRoleInAndApplicationIdAndOrganisationId(long userId, Set<ProcessRoleType> role, long applicationId, long organisationId);
    boolean existsByUserIdAndApplicationId(long userId, long applicationId);
    boolean existsByUserIdAndApplicationIdAndRole(long id, long applicationId, ProcessRoleType role);
    void deleteByApplicationId(long applicationId);

    boolean existsByUserIdAndOrganisationId(long userId, long organisationId);
    boolean existsByOrganisationId(long organisationId);

    @Query("SELECT other.organisationId FROM ProcessRole pr " +
            "JOIN ProcessRole other on other.applicationId = pr.applicationId " +
            "WHERE pr.user.id = :userId")
    List<Long> findOrganisationIdsSharingApplicationsWithUser(long userId);
}