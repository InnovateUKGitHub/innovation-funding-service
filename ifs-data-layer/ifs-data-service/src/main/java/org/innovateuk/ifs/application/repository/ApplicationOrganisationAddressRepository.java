package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationOrganisationAddress;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ApplicationOrganisationAddressRepository extends Repository<ApplicationOrganisationAddress, Long> {
    Optional<ApplicationOrganisationAddress> findByApplicationIdAndOrganisationAddressOrganisationIdAndOrganisationAddressAddressTypeId(long applicationId, long organisationId, long addressTypeId);

}
