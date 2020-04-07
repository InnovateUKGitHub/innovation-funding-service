package org.innovateuk.ifs.address.repository;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AddressRepository extends PagingAndSortingRepository<Address, Long> {

    Optional<Address> findByAddressLine1AndAddressLine2AndAddressLine3AndTownAndCountyAndCountryAndPostcode(
            String addressLine1,
            String addressLine2,
            String addressLine3,
            String town,
            String county,
            String country,
            String postcode
    );

    default Optional<Address> findAddressEqualTo(AddressResource addressResource) {
        return findByAddressLine1AndAddressLine2AndAddressLine3AndTownAndCountyAndCountryAndPostcode(
                addressResource.getAddressLine1(),
                addressResource.getAddressLine2(),
                addressResource.getAddressLine3(),
                addressResource.getTown(),
                addressResource.getCounty(),
                addressResource.getCountry(),
                addressResource.getPostcode()
        );
    }
}
