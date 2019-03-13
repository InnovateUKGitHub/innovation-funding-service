package org.innovateuk.ifs.eugrant.repository;

import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.innovateuk.ifs.eugrant.domain.EuOrganisation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface EuGrantRepository extends PagingAndSortingRepository<EuGrant, UUID> {

    boolean existsByShortCode(String shortCode);
    Page<EuGrant> findBySubmittedTrueAndNotifiedTrueAndOrganisationOrganisationTypeNot(EuOrganisationType type, Pageable pageable);
    Page<EuGrant> findBySubmittedTrueAndNotifiedFalseAndOrganisationOrganisationTypeNot(EuOrganisationType type, Pageable pageable);
    long countBySubmittedTrueAndOrganisationOrganisationTypeNot(EuOrganisationType type);
}