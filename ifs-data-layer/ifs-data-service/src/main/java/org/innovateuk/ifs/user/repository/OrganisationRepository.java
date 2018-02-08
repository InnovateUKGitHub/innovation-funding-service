package org.innovateuk.ifs.user.repository;

import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrganisationRepository extends PagingAndSortingRepository<Organisation, Long> {

    Organisation findByProcessRoles(@Param("processRoles") ProcessRole processRole);
    Organisation findOneByName(String name);
    List<Organisation> findByUsers(User user);
    List<Organisation> findByUsersId(Long userId);
    List<Organisation> findByNameOrderById(String name);
    List<Organisation> findByCompanyHouseNumberOrderById(String companiesHouseNumber);

    List<Organisation> findAll(Iterable<Long> ids);
}
