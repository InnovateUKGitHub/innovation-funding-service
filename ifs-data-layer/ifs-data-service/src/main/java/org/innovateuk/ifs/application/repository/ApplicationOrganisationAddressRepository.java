package org.innovateuk.ifs.application.repository;

import org.innovateuk.ifs.application.domain.ApplicationOrganisationAddress;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ApplicationOrganisationAddressRepository extends Repository<ApplicationOrganisationAddress, Long> {

    List<ApplicationOrganisationAddress> findByApplicationId(long applicationId);

    Optional<ApplicationOrganisationAddress> findByApplicationIdAndOrganisationAddressOrganisationIdAndOrganisationAddressAddressTypeId(long applicationId, long organisationId, long addressTypeId);

    ApplicationOrganisationAddress save(ApplicationOrganisationAddress applicationOrganisationAddress);

    void deleteByApplicationId(long applicationId);
}
