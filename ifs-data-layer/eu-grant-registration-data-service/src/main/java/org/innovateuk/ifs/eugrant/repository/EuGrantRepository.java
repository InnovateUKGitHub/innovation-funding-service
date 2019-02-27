package org.innovateuk.ifs.eugrant.repository;

import org.innovateuk.ifs.eugrant.domain.EuContact;
import org.innovateuk.ifs.eugrant.domain.EuGrant;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface EuGrantRepository extends CrudRepository<EuGrant, UUID> {

    boolean existsByShortCode(String shortCode);
    EuGrant getByContact(EuContact euContact);
}