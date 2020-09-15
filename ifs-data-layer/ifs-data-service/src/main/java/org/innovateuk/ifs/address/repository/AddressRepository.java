package org.innovateuk.ifs.address.repository;

import org.innovateuk.ifs.address.domain.Address;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AddressRepository extends PagingAndSortingRepository<Address, Long> {
}