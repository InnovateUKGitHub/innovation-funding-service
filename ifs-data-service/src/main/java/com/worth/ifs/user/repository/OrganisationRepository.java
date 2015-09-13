package com.worth.ifs.user.repository;

import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.UserApplicationRole;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface OrganisationRepository extends PagingAndSortingRepository<Organisation, Long> {
    Organisation findById(@Param("id") Long id);
    Organisation findByUserApplicationRoles(@Param("userApplicationRoles") UserApplicationRole userApplicationRole);
}
