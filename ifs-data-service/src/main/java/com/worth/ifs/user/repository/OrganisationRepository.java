package com.worth.ifs.user.repository;

import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface OrganisationRepository extends PagingAndSortingRepository<Organisation, Long> {
    Organisation findByProcessRoles(@Param("processRoles") ProcessRole processRole);
}
