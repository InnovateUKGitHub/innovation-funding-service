package org.innovateuk.ifs.organisation.repository;

import org.innovateuk.ifs.organisation.domain.ExecutiveOfficer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExecutiveOfficerRepository extends CrudRepository<ExecutiveOfficer, Long> {

    List<ExecutiveOfficer> findByOrganisationId(Long organisationId);
}
