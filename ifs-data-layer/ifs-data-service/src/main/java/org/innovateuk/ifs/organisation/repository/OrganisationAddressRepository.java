package org.innovateuk.ifs.organisation.repository;

import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface OrganisationAddressRepository extends PagingAndSortingRepository<OrganisationAddress, Long> {
    List<OrganisationAddress> findByOrganisationIdAndAddressType(Long organisationId, AddressType addressType);
}
