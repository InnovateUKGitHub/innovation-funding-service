package org.innovateuk.ifs.address.repository;

import org.innovateuk.ifs.address.domain.AddressType;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AddressTypeRepository extends PagingAndSortingRepository<AddressType, Long> {
}
