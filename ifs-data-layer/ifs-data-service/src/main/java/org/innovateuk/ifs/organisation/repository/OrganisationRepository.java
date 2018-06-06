package org.innovateuk.ifs.organisation.repository;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganisationRepository extends PagingAndSortingRepository<Organisation, Long> {

    Organisation findByProcessRoles(@Param("processRoles") ProcessRole processRole);
    Organisation findOneByName(String name);
    Optional<Organisation> findFirstByUsers(User user);
    List<Organisation> findByUsers(User user);
    List<Organisation> findByUsersId(Long userId);
    List<Organisation> findByNameOrderById(String name);
    List<Organisation> findByCompanyHouseNumberOrderById(String companiesHouseNumber);

    List<Organisation> findAll(Iterable<Long> ids);
}
