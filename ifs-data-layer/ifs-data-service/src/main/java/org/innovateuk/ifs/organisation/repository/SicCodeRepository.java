package org.innovateuk.ifs.organisation.repository;

import org.innovateuk.ifs.organisation.domain.SicCode;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SicCodeRepository extends CrudRepository<SicCode, Long> {

    List<SicCode> findByOrganisationId(Long organisationId);
}
