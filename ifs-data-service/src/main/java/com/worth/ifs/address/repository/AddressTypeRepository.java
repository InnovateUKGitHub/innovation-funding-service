package com.worth.ifs.address.repository;

import com.worth.ifs.address.domain.AddressType;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AddressTypeRepository extends PagingAndSortingRepository<AddressType, Long> {
}
