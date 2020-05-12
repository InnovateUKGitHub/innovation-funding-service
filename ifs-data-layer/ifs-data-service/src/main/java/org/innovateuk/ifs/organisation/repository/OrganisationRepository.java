package org.innovateuk.ifs.organisation.repository;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrganisationRepository extends PagingAndSortingRepository<Organisation, Long> {

    Organisation findByProcessRoles(@Param("processRoles") ProcessRole processRole);
    Organisation findOneByName(String name);
    List<Organisation> findDistinctByProcessRolesUserIdAndInternationalIsTrue(long userId);
    List<Organisation> findDistinctByProcessRolesUserId(long userId);
    List<Organisation> findDistinctByProcessRolesUser(User user);
    List<Organisation> findByNameOrderById(String name);
    List<Organisation> findByCompaniesHouseNumberOrderById(String companiesHouseNumber);
    Organisation findByProcessRolesUserIdAndProcessRolesApplicationId(long userId, long applicationId);

    @Query("SELECT o FROM Organisation o " +
            "JOIN ProjectUser pu ON o.id = pu.organisation.id " +
            "WHERE pu.user.id = :userId " +
            "AND pu.project.id = :projectId")
    Organisation findByUserAndProjectId(long userId, long projectId);
    List<Organisation> findAllById(Iterable<Long> ids);
    long countDistinctByProcessRolesApplicationId(long applicationId);
}
