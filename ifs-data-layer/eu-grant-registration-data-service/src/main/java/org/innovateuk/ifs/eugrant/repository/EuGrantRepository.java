package org.innovateuk.ifs.eugrant.repository;

import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface EuGrantRepository extends PagingAndSortingRepository<EuGrant, UUID> {

    boolean existsByShortCode(String shortCode);
    EuGrant getByContact(EuContact euContact);
    Page<EuGrant> findBySubmittedTrueAndContactNotifiedTrue(Pageable pageable);
    Page<EuGrant> findBySubmittedTrueAndContactNotifiedFalse(Pageable pageable);
}