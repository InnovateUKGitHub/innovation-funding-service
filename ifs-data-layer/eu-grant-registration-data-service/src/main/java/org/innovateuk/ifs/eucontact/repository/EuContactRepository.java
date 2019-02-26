package org.innovateuk.ifs.eucontact.repository;

import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EuContactRepository extends PagingAndSortingRepository<EuContact, Long> {

    Page<EuContact> findByNotifiedTrue(Pageable pageable);
    Page<EuContact> findByNotifiedFalse(Pageable pageable);
    EuContact getById(long id);
}
