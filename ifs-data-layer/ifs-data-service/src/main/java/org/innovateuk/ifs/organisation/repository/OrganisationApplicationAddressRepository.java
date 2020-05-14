package org.innovateuk.ifs.organisation.repository;

import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.organisation.domain.OrganisationApplicationAddress;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface OrganisationApplicationAddressRepository extends PagingAndSortingRepository<OrganisationApplicationAddress, Long> {
    List<OrganisationApplicationAddress> findByOrganisationIdAndAddressType(long organisationId, AddressType addressType);
    OrganisationApplicationAddress findByOrganisationIdAndApplicationIdAndAddressTypeId(long organisationId, long applicationId, long addressTypeId);
    OrganisationApplicationAddress findByOrganisationIdAndApplicationIdAndAddressId(long organisationId, long applicationId, long addressId);
}
